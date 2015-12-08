(ns com.webtalk.storage.queue.rabbit
  (:require [com.stuartsierra.component :as component]
            [langohr.core               :as rmq]
            [taoensso.timbre :refer [info]]))

(defrecord Rabbit [host username password connection]
  component/Lifecycle

  (start [component]
    (info "Starting rabbit")
    (if (= nil connection)
      (let [conn (rmq/connect {:host host
                               :username username
                               :password password})]
        (assoc component :connection conn))
      component))

  (stop [component]
    (info "Stopping rabbit")
    (when connection
      (rmq/close connection))
    (assoc component :connection nil)))

;; handle channels?

(defn new-rabbit [host username password]
  (map->Rabbit {:host host
                :username username
                :password password}))
