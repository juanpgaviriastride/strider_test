(ns com.webtalk.storage.queue.rabbit
  (:require [com.stuartsierra.component :as component]
            [langohr.core               :as rmq]))

(defrecord Rabbit [host connection]
  component/Lifecycle

  (start [component]
    (println "Starting rabbit")
    (if (= nil connection)
      (let [conn (rmq/connect {:host host})]
        (assoc component :connection conn))
      component))

  (stop [component]
    (println "Stopping rabbit")
    (when connection
      (rmq/close connection))
    (assoc component :connection nil)))

;; handle channels?
(defn new-rabbit [host]
  (map->Rabbit {:host host}))
