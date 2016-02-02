(ns com.webtalk.util
  (:gen-class)
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split trim]]))

(defn get-cass-hosts []
  (mapv trim (split (env :cassandra-1-port) #",")))

(defn get-titan-hosts []
  (mapv trim (split (env :titan-1-port) #",")))

(defn get-cass-keyspace []
  (trim (env :cass-keyspace)))

(defn get-cass-replica-factor []
  (Integer. (or (env :cass-replica-factor) "1")))

(defn get-rmq-host []
  (trim (env :rabbitmq-1-port)))

(defn get-rmq-username []
  (trim (or (env :rabbitmq-username) "guest")))

(defn get-rmq-password []
  (trim (or (env :rabbitmq-password) "guest")))
