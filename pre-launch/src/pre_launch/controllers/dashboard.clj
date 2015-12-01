(ns pre-launch.controllers.dashboard
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]))


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

(defn get-sent-invites [titan-id level]
  (get-query titan-id "com.webtalk.pre-launch.invite-count" level :invites))

(defn get-joined-waitlist [titan-id level]
  (get-query titan-id "com.webtalk.pre-launch.joined-waitlist-count" level :joined-waitlist))

(defn get-joined-prelaunch [titan-id level]
  (get-query titan-id "com.webtalk.pre-launch.joined-prelaunch-count" level :joined-prelaunch))



(defn get-referals-data [titan-id])

