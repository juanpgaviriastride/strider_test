(ns com.webtalk.pre-launch.core
  (:import [org.apache.commons.daemon Daemon DaemonContext])

  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])

  (:require [com.stuartsierra.component           :as component]
            [com.webtalk.system                   :as sys]
            [com.webtalk.util                     :as util]
            [com.webtalk.storage.queue            :as queue]
            [com.webtalk.storage.queue.publisher  :as publisher]
            [com.webtalk.pre-launch.request-invite :as invite]
            [com.webtalk.mailer.prelaunch-request-an-invite :as mailer-prelaunch-request-an-invite]
            [com.webtalk.pre-launch.user           :as user]
            [clojurewerkz.titanium.vertices       :as gvertex]
            [clojurewerkz.titanium.edges          :as gedge]))

;;; The connections can be handle by atoms or agents still not sure on how this multiple queues will share the connection
(def ^:dynamic *system* nil)


(defn friendly-invitation [invitation edge]
  (assoc (gvertex/to-map invitation)
         :invitationToken (:invitationToken (gedge/to-map edge))))

(defn friendly-edge [edge]
  (dissoc (gedge/to-map edge) :__id__))

(defn get-conn [component]
  (println "system" *system* (:system *system*))
  (println "component" component)
  (println "get-conn" component (get-in *system* [component]))
1  (flush)
  (get-in *system* [component :connection]))

;; queue-name com.webtalk.pre-launch.request-an-invite
(defn request-an-invite
  [load]
  (let [[callback-q payload] load
        ginvite (invite/request-an-invite! (get-conn :titan) payload)]
    (println "ginvite" ginvite)

    (println "sending email")
    (if (= (:status ginvite) :new_record)
      ;;(mailer-request-an-invite/deliver-email (payload "email"))
      (mailer-prelaunch-request-an-invite/deliver-email (payload "email")))))

;;; queue-name com.webtalk.pre-launch.create-user
(defn create-user
  [load]
  (println "create-user")
  (let [[callback-q payload] load
        guser (user/create-user! (get-conn :titan) payload)]
    (println "guser that is going to be send to the queue" (gvertex/to-map guser))
    (flush)
    (publisher/publish-with-qname (get-conn :rabbit) callback-q (gvertex/to-map guser))
    (println "about to save it on cassandra")))

(defn setup-queue-and-handlers
  "Setup the queue and subscribe the actions to the queues
   actions: ['create-entry 'follow 'invite 'create-user]
   N is the number of actions

   Example: (setup-queue-and-handlers \"com.webtalk.pre-launch\" actions)
   Returns: lazy [[conn1 ch1] [conn2 ch2] ... [connN chN]]"

  [connection qname-prefix actions]
  (letfn [(sub-helper [action]
            (println "starting doall" connection qname-prefix actions)
            (doall
             ;; this can use agents to be able to handle errors and things like monitoring and paralelo
             (queue/subscribe-with-connection
              connection
              (str qname-prefix "." action)
              @(ns-resolve 'com.webtalk.pre-launch.core action)))
            (catch Exception e (str "caught exception: " (.getMessage e) "in" action)))]
    (println "there isn't null up to " actions)
    (map sub-helper actions)))

(defn start []
  (alter-var-root #'*system*
                  (constantly
                   (sys/new-system {:rabbit    {:host  (util/get-rmq-host)}
                                    :titan     {:hosts (util/get-titan-hosts)}})))
  (alter-var-root #'*system* component/start)
  (println *system* (:system *system*))
  (let [rmq-conns-channels (setup-queue-and-handlers (get-conn :rabbit)
                                                     "com.webtalk.pre-launch"
                                                     ['request-an-invite 'create-user])]
    (println "the rmq-cons-channels-are" rmq-conns-channels)))

(defn init [args]
  (println "init?"))

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
