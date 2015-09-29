(ns com.webtalk.storage.persistence
  (:gen-class)
  (:require [clojurewerkz.cassaforte.policies       :as policies]
            [com.webtalk.storage.persistence.config :as config]
            [clojurewerkz.cassaforte.client         :as cclient]))

(defn connection-session []
  (let [conn (cclient/connect config/cassandra-hosts config/keyspace)]
    (policies/retry-policy :downgrading-consistency)
    conn))

(defn shutdown [conn]
  (cclient/disconnect conn))
