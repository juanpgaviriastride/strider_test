(ns com.webtalk.storage
  (:gen-class)
  (:require [com.webtalk.storage.graph         :as graph]
            [com.webtalk.storage.persistence   :as persistence]
            [com.webtalk.storage.queue         :as queue]
            [com.webtalk.resilience.user       :as user]
            [com.webtalk.resilience.invitation :as invitation]
            [com.webtalk.resilience.follow     :as follow]
            [com.webtalk.resilience.entry      :as entry]
            [clojurewerkz.titanium.vertices    :as gvertex]))

;;; The connections can be handle by atoms or agents still not sure on how this multiple queues will share the connection
(def graph-connection (graph/connection-session))

(def persistence-connection (persistence/connection-session))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry [payload]
  ;; start timeline users populator
  (let [gentry (entry/gcreate-entry graph-connection payload)]
    (entry/pcreate-entry persistence-connection (gvertex/get gentry :id) (payload "user_id") payload)))

;;; queue-name com.webtalk.storage.queue.follow
(defn follow [payload]
  (let [gfollow (follow/gfollow graph-connection payload)
        pfollow (follow/pfollow persistence-connection (payload "user_id") (payload "followed_id"))]))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite [payload]
  (let [ginvitation (invitation/gcreate-invitation graph-connection payload)]
    (invitation/pcreate-invitation persistence-connection (gvertex/get ginvitation :id) payload)))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [payload]
  (let [guser (user/gcreate-user graph-connection payload)]
    (user/pcreate-user persistence-connection (gvertex/get guser :id) payload)
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
              (println "Setting up queue for " action)
              (queue/subscribe-with-connection (str qname-prefix "." action) @ (ns-resolve 'com.webtalk.storage action))))]
    (map sub-helper actions)))

(defn -main
  ""
  [& args]
  (let [rmq-conns-channels (setup-queue-and-handlers "com.webtalk.storage.queue" ['create-entry 'follow 'invite 'create-user])]
    (println rmq-conns-channels)))

;;; Important to close
;;; from queue
;;; (rmq/close channel)
;;; (rmq/close connection)
;;; from persistence
;;; (cclient/disconnect connection)
;;; from graph
;;; (tgraph/shutdown graph)
