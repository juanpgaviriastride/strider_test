(ns com.webtalk.storage.persistence.cassandra
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.cassaforte.policies       :as policies]
            [clojurewerkz.cassaforte.client         :as cclient]))

(defrecord Cassandra [hosts keyspace connection]
  component/Lifecycle

  (start [component]
    (println "Starting cassandra")
    (if (= nil connection)
      (let [conn (cclient/connect hosts keyspace)]
        (policies/retry-policy :downgrading-consistency conn)
        (assoc component :connection conn))
      component))

  (stop [component]
    (println "Stop cassandra")
    (when connection
      (cclient/disconnect connection))
    (assoc component :connection nil)))

(def new-cassandra [hosts keyspace]
  (map->Cassandra {:hosts hosts :keyspace keyspace}))
