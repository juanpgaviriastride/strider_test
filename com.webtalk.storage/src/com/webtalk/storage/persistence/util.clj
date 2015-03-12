(ns com.webtalk.storage.persistence.util
  (:gen-class)
  (:require [com.webtalk.storage.persistence.config :as config]
            [clojurewerkz.cassaforte.client         :as cclient]
            [clojurewerkz.cassaforte.cql            :as cql]
            [clojurewerkz.cassaforte.query          :as query]))

(defn create-table
  [connection table-name columns options]
  (try
    (cql/create-table connection table-name
                      (query/column-definitions columns)
                      (if-not (nil? options) (query/with options)))
    (println "Table" table-name "was created")
    (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
      (println (.getMessage e)))
    (catch Exception e
      (do
        (println "Witin" table-name "table creation" (.getMessage e))
        (throw e)))))

(defn create-keyspace
  [connection keyspace options]
  (try
    (cql/create-keyspace connection keyspace (query/with options))
    (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
      (println (.getMessage e)))))

