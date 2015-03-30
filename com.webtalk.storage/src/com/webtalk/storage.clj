(ns com.webtalk.storage
  (:gen-class)
  (:require [com.webtalk.storage.graph           :as graph]
            [com.webtalk.storage.persistence     :as persistence]
            [com.webtalk.storage.queue           :as queue]
            [com.webtalk.storage.queue.publisher :as publisher]
            [com.webtalk.resilience.user         :as user]
            [com.webtalk.resilience.invitation   :as invitation]
            [com.webtalk.resilience.follow       :as follow]
            [com.webtalk.resilience.entry        :as entry]
            [clojurewerkz.titanium.vertices      :as gvertex]
            [clojurewerkz.titanium.edges         :as gedge]))

;;; The connections can be handle by atoms or agents still not sure on how this multiple queues will share the connection
(def graph-connection (graph/connection-session))

(def persistence-connection (persistence/connection-session))

;;; queue-name com.webtalk.storage.queue.create-entry
(defn create-entry
  [load]
  ;; start timeline users populator
  (let [[callback-q payload] load
        gentry (entry/gcreate-entry graph-connection payload)]
    (publisher/publish-with-qname callback-q (gvertex/to-map gentry))
    (entry/pcreate-entry persistence-connection (gvertex/get gentry :id) (payload "user_id") payload)))

;;; queue-name com.webtalk.storage.queue.follow
(defn follow
  [load]
  (let [[callback-q payload] load
        gfollow (follow/gfollow graph-connection payload)
        _ (publisher/publish-with-qname callback-q (gedge/to-map gfollow))
        pfollow (follow/pfollow persistence-connection (payload "user_id") (payload "followed_id"))]))

;;; queue-name com.webtalk.storage.queue.invite 
(defn invite
  [load]
  (let [[callback-q payload] load
        ginvitation (invitation/gcreate-invitation graph-connection payload)]
    (publisher/publish-with-qname callback-q (gvertex/to-map ginvitation))
    (invitation/pcreate-invitation persistence-connection (gvertex/get ginvitation :id) payload)))

;;; queue-name com.webtalk.storage.queue.create-user
(defn create-user
  [load]
  (let [[callback-q payload] load
        guser (user/gcreate-user graph-connection payload)]
    (publisher/publish-with-qname callback-q (gvertex/to-map guser))
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
              ;; Optional threaded approach will need to replace the cass and titan connection globals
              ;; and define locals within this functions via let and pass them via args to avoid
              ;; concurrency issues
              ;; (.start (Thread.
              (queue/subscribe-with-connection (str qname-prefix "." action)
                                               @ (ns-resolve 'com.webtalk.storage action))
              ;; ))
              ))]
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
