(ns com.webtalk.storage.persistence.schema
  (:gen-class)
 (:require [com.webtalk.storage.persistence.config :as config]
           [clojurewerkz.cassaforte.client         :as cclient]
           [clojurewerkz.cassaforte.cql            :as cql]
           [clojurewerkz.cassaforte.query          :as query]))


;;; For migrating and boostrapping

(defn -main
  [& args]
  (let [conn (cclient/connect config/cassandra-hosts)]
    ;; Create main keyspace
    (try
      (cql/create-keyspace conn config/keyspace (query/with {:replication
                                                            {:class "SimpleStrategy"
                                                             :replication_factor 1}}))
      (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
        (println (.getMessage e)))) 
    ))