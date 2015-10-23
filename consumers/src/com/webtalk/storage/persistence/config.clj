(ns com.webtalk.storage.persistence.config
  (:gen-class)
  (:require [com.webtalk.util :as util]))

;;; List of hosts
(def cassandra-hosts util/get-cass-hosts)

(def keyspace util/get-cass-keyspace)

(def replication-factor util/get-cass-replica-factor)

