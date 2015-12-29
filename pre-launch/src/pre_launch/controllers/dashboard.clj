(ns pre-launch.controllers.dashboard
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]
            [taoensso.carmine :as car :refer (wcar)]
            [environ.core :refer [env]])
  (:import java.util.Date))

(def server1-conn {:pool {}
                   :spec {:uri (or (env :redis-uri)
                                  "redis://localhost:6379")}}) ; See `wcar` docstring for opts

(defmacro wcar* [& body]
  `(car/wcar server1-conn ~@body))

(defn cache [key value]
  (wcar* (car/set key value)
         (car/expire key 1800))) ;; 30m ttl

(defmacro get-or-set-cache [key body]
  `(if-let [cached-value# (wcar* (car/get ~key))]
     cached-value#
     (let [value# ~body]
       (cache ~key value#)
       value#)))

(defn get-referral-network [titan-id]
  (get-or-set-cache (str titan-id ".get-referral-network")
                    (let [queue-name "com.webtalk.pre-launch.referral-network"
                          callback-queue-name (str queue-name (url-part 15))
                          result (queue/promise-subscription callback-queue-name (fn [a] (identity a)))]
                      (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id})
                      @result)))

(defn divide-detail-response [response-map]
  (map (fn [record]
         (if (:time record)
           (assoc-in record [:time] (java.util.Date. (long (:time record))))
           record)) response-map))

(defn get-referral-network-detail-plain [titan-id]
  (get-or-set-cache (str titan-id ".get-referral-network-detail-plain")
                    (let [queue-name "com.webtalk.pre-launch.referral-network-detail"
                          callback-queue-name (str queue-name (url-part 15))
                          result (queue/promise-subscription callback-queue-name (fn [a] (identity a)))]
                      (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id})
                      (divide-detail-response @result))))
