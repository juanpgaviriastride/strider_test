(ns com.webtalk.storage
  (:import [org.apache.commons.daemon Daemon DaemonContext])

  (:gen-class
   :implements [org.apache.commons.daemon.Daemon])

  (:require [com.webtalk.storage.graph            :as graph]
            [com.webtalk.storage.persistence      :as persistence]
            [com.webtalk.storage.queue            :as queue]
            [com.webtalk.storage.queue.publisher  :as publisher]
            [com.webtalk.resilience.user          :as user]
            [com.webtalk.resilience.invitation    :as invitation]
            [com.webtalk.resilience.follow        :as follow]
            [com.webtalk.resilience.entry         :as entry]
            [com.webtalk.resilience               :refer [start-jetty]]
            [com.webtalk.mailer.request-an-invite :as mailer-request-an-invite]
            [clojurewerkz.titanium.vertices       :as gvertex]
            [clojurewerkz.titanium.edges          :as gedge]))

;;; The connections can be handle by atoms or agents still not sure on how this multiple queues will share the connection
(defonce state (atom {}))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry
  [load]
  ;; start timeline users populator
  (let [[callback-q payload] load
        gentry (entry/gcreate-entry (:graph-connection @state) payload)]
    (publisher/publish-with-qname callback-q (gvertex/to-map gentry))
    (entry/pcreate-entry (:persistence-connection @state) (gvertex/get gentry :id) (payload "user_id") payload)))

;;; queue-name com.webtalk.storage.queue.follow
(defn follow
  [load]
  (let [[callback-q payload] load
        gfollow (follow/gfollow (:graph-connection @state) payload)
        _ (publisher/publish-with-qname callback-q (gedge/to-map gfollow))
        pfollow (follow/pfollow (:persistence-connection @state) (payload "user_id") (payload "followed_id"))]))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite
  [load]
  (let [[callback-q payload] load
        ginvitation (invitation/gcreate-invitation (:graph-connection @state) payload)]
    (publisher/publish-with-qname callback-q (gvertex/to-map ginvitation))
    (invitation/pcreate-invitation (:persistence-connection @state) (gvertex/get ginvitation :id) payload)))

;; queue-name com.webtalk.storage.queue.request-an-invite
(defn request-an-invite
  [load]
  (println "request-an-invite")
  (println "load" load)
  (let [[callback-q payload] load
        _ (println "q" callback-q "payload" payload)
        ginvite (invitation/grequest-an-invite (:graph-connection @state) payload)]
    (println "sending email")
    (mailer-request-an-invite/deliver-email (payload "email"))
    (println "ginvite" (gvertex/to-map ginvite))
    (flush)
    (println "saving into cass")
    (invitation/prequest-invitation (:persistence-connection @state) (gvertex/get ginvite :id) payload)))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [load]
  (println "create-user")
  (let [[callback-q payload] load
        guser (user/gcreate-user (:graph-connection @state) payload)]
    (println "guser that is going to be send to the queue" (gvertex/to-map guser))
    (flush)
    (publisher/publish-with-qname callback-q (gvertex/to-map guser))
    (println "about to save it on cassandra")
    (user/pcreate-user (:persistence-connection @state) (gvertex/get guser :id) payload)
    ;; pending create network as we do for titan
    ))

(defn setup-queue-and-handlers
  "Setup the queue and subscribe the actions to the queues
   actions: ['create-entry 'follow 'invite 'create-user]
   N is the number of actions

   Example: (setup-queue-and-handlers \"com.webtalk.storage.queue\" actions)
   Returns: lazy [[conn1 ch1] [conn2 ch2] ... [connN chN]]"

  [qname-prefix actions]
  (letfn [(sub-helper [action]
            (do
              ;; this can use agents to be able to handle errors and things like monitoring and paralelo
              (println "Setting up queue for" action)
              ;; Optional threaded approach will need to replace the cass and titan connection globals
              ;; and define locals within this functions via let and pass them via args to avoid
              ;; concurrency issues
              ;; (.start (Thread.
              (queue/subscribe-with-connection (str qname-prefix "." action)
                                               @(ns-resolve 'com.webtalk.storage action))
              ;; ))
              ))]
    (map sub-helper actions)))

(defn start []
  (swap! state assoc :graph-connection (graph/connection-session))
  (swap! state assoc :persistence-connection (persistence/connection-session))
  (let [rmq-conns-channels (setup-queue-and-handlers "com.webtalk.storage.queue" ['request-an-invite 'create-entry 'follow 'invite 'create-user
                                                                                  ])]
    (swap! state assoc :rmq-conns-channels rmq-conns-channels)
    
    (println rmq-conns-channels))
  ;;(start-jetty)
  )

(defn init [args]
  (swap! state assoc :running true))

(defn stop []
  ;; Important to close
  ;; from queue
  (doall
   (map (fn [conn-ch] (queue/shutdown conn-ch)) (:rmq-conns-channels @state)))
  (swap! state assoc :rmq-conns-channels nil)
  ;; from persistence
  (persistence/shutdown (:persistence-connection @state))
  (swap! state assoc :persistence-connection nil)
  ;; from graph
  (graph/shutdown (:graph-connection @state))
  (swap! state assoc :graph-connection nil)
  ;; update the running state
  (swap! state assoc :running false))

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
