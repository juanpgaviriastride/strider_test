(ns com.webtalk.storage
  (:gen-class)
  (:require [com.webtalk.storage.graph            :as graph]
            [com.webtalk.storage.graph.user       :as graph-user]
            [com.webtalk.storage.graph.invitation :as graph-invitation]
            [com.webtalk.storage.graph.follow     :as graph-follow]
            [com.webtalk.storage.graph.entry      :as graph-entry]
            [com.webtalk.storage.persistence      :as persistence]
            [com.webtalk.storage.queue            :as queue]))

(def *graph-connection* (graph/connection-session))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry [payload]
  (let [gentry (graph-entry/create-entry! *graph-connection* payload)]
    ;; use gentry to get the id but everything should be done using the payload
    )
  )

;;; queue-name com.webtalk.storage.queue.follow
(defn follow [payload]
  (let [gfollow (graph-follow/follow! *graph-connection* payload)
        ;; use payload to insert follower and following into cassandra don't need to wait for titan
        ]))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite [payload]
  (let [ginvitation (graph-invitation/create-invitation! *graph-connection* payload)]
    ;; use ginvitation to insert cassandra invitation (invitation_id is the invitedUser id)
    ))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [payload]
  (let [guser (graph-user/create-user! *graph-connection* payload)]
    ;; use guser to insert cassandra user and possible follows and referrer_by
    ;; but all the other data is taken from the payload
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
  (let [rmq-conns-channels (setup-queue-and-handlers "com.webtalk.storage.queue" ['create-entry 'follow 'invite 'create-user])]))

;;; Important to close
;;; from queue
;;; (rmq/close channel)
;;; (rmq/close connection)
;;; from persistence
;;; (cclient/disconnect connection)
;;; from graph
;;; (tgraph/shutdown graph)