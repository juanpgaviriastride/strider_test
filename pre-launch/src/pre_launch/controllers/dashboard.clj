(ns pre-launch.controllers.dashboard
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]))


(defn get-query [titan-id callback-queue-name level]
  (println "llame"))

(defn get-sent-invites [titan-id]
  (let [callback-queue-name (str "com.webtalk.pre-launch.invite-count" (url-part 15))
        result (queue/promise-subscription callback-queue-name (fn [r] (:invites r)))]
    (queue/publish-with-qname "com.webtalk.pre-launch.invite-count" callback-queue-name
                              {:titan_id titan-id
                               :level 1})
    @result))
