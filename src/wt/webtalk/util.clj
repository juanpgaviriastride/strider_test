(ns com.webtalk.util
  (:gen-class)
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split trim]]))

(defn get-cass-hosts []
  (vec (map trim (split (env :cass-hosts) #","))))

(defn get-cass-keyspace []
  (trim (env :cass-keyspace)))

(defn get-cass-replica-factor []
  (Integer. (trim (env :cass-replica-factor))))

(defn get-rmq-host []
  (trim (env :rmq-host)))
