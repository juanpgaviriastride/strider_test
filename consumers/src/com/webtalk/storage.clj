(ns com.webtalk.storage
  (:import [org.apache.commons.daemon Daemon DaemonContext])

  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])

  (:require [com.stuartsierra.component           :as component]
            [com.webtalk.system                   :as sys]
            [com.webtalk.util                     :as util]
            [com.webtalk.storage.queue            :as queue]
            [com.webtalk.storage.queue.publisher  :as publisher]
            [com.webtalk.resilience.user          :as user]
            [com.webtalk.resilience.invitation    :as invitation]
            [com.webtalk.resilience.follow        :as follow]
            [com.webtalk.resilience.entry         :as entry]
            ;;[com.webtalk.resilience               :refer [start-jetty]]
            [com.webtalk.mailer.request-an-invite :as mailer-request-an-invite]
            [com.webtalk.mailer.prelaunch-request-an-invite :as mailer-prelaunch-request-an-invite]
            [clojurewerkz.titanium.vertices       :as gvertex]
            [clojurewerkz.titanium.edges          :as gedge]
            [taoensso.timbre :refer [debug spy info]]))

;;; The connections can be handle by atoms or agents still not sure on how this multiple queues will share the connection
(def ^:dynamic *system* nil)


(defn friendly-invitation [invitation edge]
  (assoc (gvertex/to-map invitation)
         :invitationToken (:invitationToken (gedge/to-map edge))))

(defn friendly-edge [edge]
  (dissoc (gedge/to-map edge) :__id__))

(defn get-conn [component]
  (debug "system" *system* (:system *system*))
  (spy component)
  (debug "get-conn" component (get-in *system* [component]))
  (get-in *system* [component :connection]))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry
  [load]
  ;; start timeline users populator
  (let [[callback-q payload] load
        gentry (entry/gcreate-entry (get-conn :titan) payload)]
    (publisher/publish-with-qname (get-conn :rabbit) callback-q (gvertex/to-map gentry))
    (entry/pcreate-entry (get-conn :cassandra) (gvertex/get gentry :id) (Integer. (payload "user_id")) payload)))

;;; queue-name com.webtalk.storage.queue.follow
(defn follow
  [load]
  (let [[callback-q payload] load
        gfollow (follow/gfollow (get-conn :titan) payload)]
    (publisher/publish-with-qname (get-conn :rabbit) callback-q (friendly-edge gfollow))
    (follow/pfollow (get-conn :cassandra) (Integer. (payload "user_id")) (Integer. (payload "followed_id")))))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite
  [load]
  (let [[callback-q payload] load
        [ginvitation ginv-edge] (invitation/gcreate-invitation (get-conn :titan) payload)]
    (debug "invite response" (friendly-invitation ginvitation ginv-edge))
    (publisher/publish-with-qname (get-conn :rabbit) callback-q (friendly-invitation ginvitation ginv-edge))
    (invitation/pcreate-invitation (get-conn :cassandra) (gvertex/get ginvitation :id) payload)))

;; queue-name com.webtalk.storage.queue.request-an-invite
(defn request-an-invite
  [load]
  (let [[callback-q payload] load
        ginvite (invitation/grequest-an-invite (get-conn :titan) payload)]
    (spy ginvite)

    (when (= (:status ginvite) :new_record)
      (debug "sending email")
      (mailer-request-an-invite/deliver-email (payload "email")))

    (when (not= (:status ginvite) :mixmatch_type_record)
      (debug "saving into cass")
      (invitation/prequest-invitation (get-conn :cassandra) (:__id__ (:vertex ginvite)) payload))))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [load]
  (debug "create-user")
  (let [[callback-q payload] load
        guser (user/gcreate-user (get-conn :titan) payload)]
    (debug "guser that is going to be send to the queue" (gvertex/to-map guser))
    (publisher/publish-with-qname (get-conn :rabbit) callback-q (gvertex/to-map guser))
    (debug "about to save it on cassandra")
    (user/pcreate-user (get-conn :cassandra) (gvertex/get guser :id) payload)
    ;; pending create network as we do for titan
    ))

(defn setup-queue-and-handlers
  "Setup the queue and subscribe the actions to the queues
   actions: ['create-entry 'follow 'invite 'create-user]
   N is the number of actions

   Example: (setup-queue-and-handlers \"com.webtalk.storage.queue\" actions)
   Returns: lazy [[conn1 ch1] [conn2 ch2] ... [connN chN]]"

  [connection qname-prefix actions]
  (letfn [(sub-helper [action]
            (info "starting doall" connection qname-prefix actions)
            (try
              (doall
              ;; this can use agents to be able to handle errors and things like monitoring and paralelo
              (queue/subscribe-with-connection
               connection
               (str qname-prefix "." action)
               @(ns-resolve 'com.webtalk.storage action))
              )
              (catch Exception e (str "caught exception: " (.getMessage e) "in" action))))]
    (debug "there isn't null up to " actions)
    (map sub-helper actions)))

(defn start []
  (alter-var-root #'*system*
                  (constantly
                   (sys/new-system {:cassandra {:hosts (util/get-cass-hosts)
                                                :keyspace (util/get-cass-keyspace)}
                                    :rabbit    {:host  (util/get-rmq-host)
                                                :username (util/get-rmq-username)
                                                :password (util/get-rmq-password)}
                                    :titan     {:hosts (util/get-titan-hosts)}})))
  (alter-var-root #'*system* component/start)
  (debug *system* (:system *system*))
  (let [rmq-conns-channels (setup-queue-and-handlers (get-conn :rabbit)
                                                     "com.webtalk.storage.queue"
                                                     ['request-an-invite 'invite])]
    (info "the rmq-cons-channels-are" rmq-conns-channels)))

(defn init [args]
  (debug "init?"))

(defn stop []
  ;; Important to close
  ;; from queue
  (alter-var-root #'*system* component/stop))

;; Daemon implementation

(defn -init [this ^DaemonContext context]
  (init (.getArguments context)))

(defn -start [this]
  (future (start)))

(defn -stop [this]
  (stop))

;; Enable command-line invocation
(defn -main [& args]
  (init args)
  (start))
