(ns com.webtalk.util
  (:gen-class)
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split trim]]))

(defn get-cass-hosts []
  ;; (mapv trim (split (env :casssandra-1-port) #","))
  ["cassandra_seed"]
  )

(defn get-cass-keyspace []
  (trim (env :cass-keyspace)))

(defn get-cass-replica-factor []
  (Integer. (trim (env :cass-replica-factor))))

(defn get-rmq-host []
  ;; (trim (env :rabbitmq-1-port))
  "rabbitmq"
  )
