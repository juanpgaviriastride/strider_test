(ns pre-launch.controllers.dashboard
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]])

  (:import java.util.Date))


(defn get-query [titan-id queue-name level response-key]
  (let [callback-queue-name (str queue-name (url-part 15))
        result (queue/promise-subscription callback-queue-name (fn [a] (response-key a)))]
    (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id
                                                              :level level})
    @result))

(defn get-referral-network [titan-id]
  (let [queue-name "com.webtalk.pre-launch.referral-network"
        callback-queue-name (str queue-name (url-part 15))
        result (queue/promise-subscription callback-queue-name (fn [a] (identity a)))]
    (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id})
    @result))

(defn divide-detail-response [response-map]
  (println "[.....debug....] the map in divide-detail-response is " response-map)
  {:names  (map (fn [record] (:email record)) response-map)
   :dates (map (fn [record] (if (:time record) (java.util.Date. (long (:time record))) nil)) response-map)
   :statuses (map (fn [record] (:status record)) response-map)
   })

(defn divide-detail-response-2 [response-map]
  (map (fn [record]
         (if
             (:time record)
           (assoc-in record [:time] (java.util.Date. (long (:time record))))
           record)) response-map))

(defn get-referral-network-detail [titan-id]
  (let [queue-name "com.webtalk.pre-launch.referral-network-detail"
        callback-queue-name (str queue-name (url-part 15))
        result (queue/promise-subscription callback-queue-name (fn [a] (identity a)))]
    (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id})
    (divide-detail-response @result)))



