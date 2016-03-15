(ns com.webtalk.pre-launch.core
  (:import [org.apache.commons.daemon Daemon DaemonContext])

  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])

  (:require [com.stuartsierra.component                     :as component]
            [com.webtalk.pre-launch.system                             :as sys]
            [com.webtalk.util                               :as util]
            [com.webtalk.storage.queue                      :as queue]
            [com.webtalk.storage.queue.publisher            :as publisher]
            [com.webtalk.pre-launch.request-invite          :as req-invite]
            [com.webtalk.mailer.prelaunch-request-an-invite :as mailer-prelaunch-request-an-invite]
            [com.webtalk.mailer.prelaunch-invite            :as mailer-prelaunch-invite]
            [com.webtalk.pre-launch.user                    :as user]
            [com.webtalk.pre-launch.invite                  :as invite]
            [com.webtalk.pre-launch.computation             :as computation]
            [clojurewerkz.titanium.vertices                 :as gvertex]
            [clojurewerkz.titanium.edges                    :as gedge]
            [taoensso.timbre :refer [debug info spy]]))

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

;; queue-name com.webtalk.pre-launch.referral-network-detail
(defn referral-network-detail
  [load]
  (let [[callback-q payload] load
        root-node (gvertex/find-by-id (get-conn :titan) (payload "titan_id"))]
    (publisher/publish-with-qname (get-conn :rabbit)
                                  callback-q
                                  (computation/detailed-lvl-1 root-node))))

;; queue-name com.webtalk.pre-launch.referral-network
(defn referral-network
  [load]
  (let [[callback-q payload] load
        root-node (gvertex/find-by-id (get-conn :titan) (payload "titan_id"))
        get-network  (fn [edge-label]
                       (doall
                        (map (fn [lvl] (computation/get-level root-node lvl edge-label))
                             (range 1 6))))
        prelaunch  (get-network "refered_by")
        waitlist   (get-network "invited_waitlist_by")
        invites    (get-network "invited_by")]
    (publisher/publish-with-qname (get-conn :rabbit)
                                  callback-q
                                  {:joined-prelaunch {:levels prelaunch :total (apply + prelaunch)}
                                   :joined-waitlist {:levels waitlist :total (apply + waitlist)}
                                   :invites {:levels invites :total (apply + invites)}})))

;; queue-name com.webtalk.pre-launch.joined-prelaunch-count

(defn joined-prelaunch-count
  [load]
  (let [[callback-q payload] load
        root-node (gvertex/find-by-id (get-conn :titan) (payload "titan_id"))
        lvl (Integer. (payload "level"))]
    (publisher/publish-with-qname (get-conn :rabbit)
                                  callback-q
                                  {:joined-prelaunch (computation/get-level root-node lvl "refered_by")})))

;; queue-name com.webtalk.pre-launch.joined-waitlist-count

(defn joined-waitlist-count
  [load]
  (let [[callback-q payload] load
        root-node (gvertex/find-by-id (get-conn :titan) (payload "titan_id"))
        lvl (Integer. (payload "level"))]
    (publisher/publish-with-qname (get-conn :rabbit)
                                  callback-q
                                  {:joined-waitlist (computation/get-level root-node lvl "invited_waitlist_by")})))

;; queue-name com.webtalk.pre-launch.invite-count
(defn invite-count
  [load]
  (let [[callback-q payload] load
        root-node (gvertex/find-by-id (get-conn :titan) (payload "titan_id"))
        lvl (Integer. (payload "level"))]
    (publisher/publish-with-qname (get-conn :rabbit)
                                  callback-q
                                  {:invites (computation/get-level root-node lvl "invited_by")})))

;; queue-name com.webtalk.pre-launch.bulk-invite
(defn bulk-invite
  [load]
  (let [[_ payload] load
        ginvites (doall (map #(invite/invite! (get-conn :titan) {"email" % "refererID" (payload "refererID")})
                             (payload "emails")))]
    (spy ginvites)

    (info "sending email")
    (info "sender" (first ginvites) (:sender (first ginvites)))
    (mailer-prelaunch-invite/bulk-email (:sender (first ginvites)) (payload "emails") (payload "custom_message")))

;; queue-name com.webtalk.pre-launch.invite
(defn invite
  [load]
  (let [[_ payload] load
        ginvite (invite/invite! (get-conn :titan) payload)]
    (spy ginvite)

    (debug "sending email")
    (mailer-prelaunch-invite/deliver-email (:sender ginvite) (payload "email"))))


;; queue-name com.webtalk.pre-launch.request-an-invite
(defn request-an-invite
  [load]
  (let [[callback-q payload] load
        ginvite (req-invite/request-an-invite! (get-conn :titan) payload)]
    (spy ginvite)

    (debug "sending email")
    (if (= (:status ginvite) :new_record)
      (mailer-prelaunch-request-an-invite/deliver-email (payload "email")))))

;;; queue-name com.webtalk.pre-launch.create-user
(defn create-user
  [load]
  (debug "create-user")
  (let [[callback-q payload] load
        guser (user/create-user! (get-conn :titan) payload)]
    (debug "guser that is going to be send to the queue" guser)
    (publisher/publish-with-qname (get-conn :rabbit) callback-q guser)))

(defn setup-queue-and-handlers
  "Setup the queue and subscribe the actions to the queues
   actions: ['create-entry 'follow 'invite 'create-user]
   N is the number of actions

   Example: (setup-queue-and-handlers \"com.webtalk.pre-launch\" actions)
   Returns: lazy [[conn1 ch1] [conn2 ch2] ... [connN chN]]"

  [connection qname-prefix actions]
  (letfn [(sub-helper [action]
            (info "starting doall" connection qname-prefix actions)
            (doall
             ;; this can use agents to be able to handle errors and things like monitoring and paralelo
             (queue/subscribe-with-connection
              connection
              (str qname-prefix "." action)
              @(ns-resolve 'com.webtalk.pre-launch.core action))))]
    (debug "there isn't null up to " actions)
    (map sub-helper actions)))

(defn start []
  (alter-var-root #'*system*
                  (constantly
                   (sys/new-system {:rabbit    {:host  (util/get-rmq-host)
                                                :username (util/get-rmq-username)
                                                :password (util/get-rmq-password)}
                                    :titan     {:hosts (util/get-titan-hosts)}})))
  (alter-var-root #'*system* component/start)
  (debug *system* (:system *system*))
  (let [rmq-conns-channels (setup-queue-and-handlers (get-conn :rabbit)
                                                     "com.webtalk.pre-launch"
                                                     ['request-an-invite
                                                      'create-user
                                                      'invite
                                                      'bulk-invite
                                                      'invite-count
                                                      'joined-waitlist-count
                                                      'joined-prelaunch-count
                                                      'referral-network
                                                      'referral-network-detail])]
    (info "the rmq-cons-channels-are" rmq-conns-channels)))

(defn init [args]
  (info "init?"))

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
