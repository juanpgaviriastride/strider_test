(ns com.webtalk.storage.persistence.cassandra
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.cassaforte.policies       :as policies]
            [clojurewerkz.cassaforte.client         :as cclient]
            [taoensso.timbre :refer [info]]))

(defrecord Cassandra [hosts keyspace connection]
  component/Lifecycle

  (start [component]
    (info "Starting cassandra")
    (if (= nil connection)
      (let [conn (cclient/connect hosts keyspace)]
        (policies/retry-policy :downgrading-consistency)
        (assoc component :connection conn))
      component))

  (stop [component]
    (info "Stop cassandra")
    (when connection
      (cclient/disconnect connection))
    (assoc component :connection nil)))

(defn new-cassandra [hosts keyspace]
  (map->Cassandra {:hosts hosts :keyspace keyspace}))
