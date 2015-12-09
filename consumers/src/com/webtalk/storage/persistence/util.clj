(ns com.webtalk.storage.persistence.util
  (:gen-class)
  (:require [com.webtalk.storage.persistence.config :as config]
            [clojurewerkz.cassaforte.client         :as cclient]
            [clojurewerkz.cassaforte.cql            :as cql]
            [clojurewerkz.cassaforte.query          :as query]
            [taoensso.timbre :refer [info error]]))

(defn create-table
  "Create a table given the connection table-name options
   options: {:clustering-order key}

   Example: (create-table connection table-name columns options)"

  [connection table-name columns options]
  (try
    (cql/create-table connection table-name
                      (query/column-definitions columns)
                      (if-not (nil? options) (query/with options)))
    (info "Table" table-name "was created")
    (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
      (error (.getMessage e)))
    (catch Exception e
      (do
        (error "Witin" table-name "table creation" (.getMessage e))
        (throw e)))))

(defn drop-table
  "Remove the given table using the connection to the cluster

   Example: (drop-table connection table-name)"

  [connection table-name]
  (try
    (cql/drop-table connection table-name)
    (catch Exception e
      (error "The table" table-name "drop was not completed" (.getMessage e)))))

(defn create-keyspace
  "Create a keyspace given the connection keyspace options
   options: {:replication {:clustering-order \"SimpleStrategy\"
                           :replication_factor 1}}

   Example: (create-keyspace connection keyspace options)"

  [connection keyspace options]
  (try
    (cql/create-keyspace connection keyspace (query/with options))
    (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
      (error (.getMessage e)))
    (catch Exception e
      (do
        (error "Within" keyspace "keyspace creation" (.getMessage e))
        (throw e)))))
