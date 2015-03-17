(ns com.webtalk.storage
  (:gen-class)
  (:require [com.webtalk.storage.graph                  :as graph]
            [clojurewerkz.titanium.vertices             :as graph-vertex]
            [com.webtalk.storage.graph.user             :as graph-user]
            [com.webtalk.storage.graph.invitation       :as graph-invitation]
            [com.webtalk.storage.graph.follow           :as graph-follow]
            [com.webtalk.storage.graph.entry            :as graph-entry]
            [com.webtalk.storage.persistence            :as persistence]
            [com.webtalk.storage.persistence.user       :as persistence-user]
            [com.webtalk.storage.persistence.invitation :as persistence-invitation]
            [com.webtalk.storage.persistence.following  :as persistence-following]
            [com.webtalk.storage.persistence.entry      :as persistence-entry]
            [com.webtalk.storage.queue                  :as queue]))

(def graph-connection (graph/connection-session))

(def persistence-connection (persistence/connection-session))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry [payload]
  (let [gentry (graph-entry/create-entry! graph-connection payload)]
    (persistence-entry/create-entry persistence-connection (graph-vertex/get gentry :id) (payload "user_id") payload)))

;;; queue-name com.webtalk.storage.queue.follow
(defn follow [payload]
  (let [gfollow (graph-follow/follow! graph-connection payload)
        pfollow (persistence-following persistence-connection (payload "user_id") (payload "followed_id"))]))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite [payload]
  (let [ginvitation (graph-invitation/create-invitation! graph-connection payload)]
    (persistence-invitation/create-invitation persistence-connection (graph-vertex/get ginvitation :id) payload)))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [payload]
  (let [guser (graph-user/create-user! graph-connection payload)]
    (persistence-user/create-user persistence-connection (graph-vertex/get guser :id) payload)
    ;; pending create network as we do for titan
    ))

(defn setup-queue-and-handlers
  "Setup the queue and subscribe the actions to the queues
   actions: ['create-entry 'follow 'invite 'create-user]
   N is the number of actions

   Example: (setup-queue-and-handlers \"com.webtalk.storage.queue\" actions)
   Returns: lazy [[conn1 ch1] [conn2 ch2] ... [connN chN]]"

  [qname-prefix actions]
  (map #(queue/subscribe-with-connection (str qname-prefix "." %1) %1) actions))


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