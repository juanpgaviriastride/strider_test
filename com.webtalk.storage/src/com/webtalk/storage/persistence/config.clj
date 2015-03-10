(ns com.webtalk.storage.persistence.config
  (:gen-class)
  (:require [clojurewerkz.cassaforte.client :as cclient]))

;;; List of hosts
(def cassandra-hosts
  [
   "192.168.50.4"
   ])

(def keyspace "webtalk_development")

;;; Cassandra options
(def cassandra-options
  {
   :keyspace keyspace
   })


;;; Testing

(defn -main
  [& args]
  (cclient/connect cassandra-hosts cassandra-options))