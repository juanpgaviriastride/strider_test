(ns pre-launch.controllers.dashboard
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]))


(defn get-query [titan-id queue-name level response-key]
  (let [callback-queue-name (str queue-name (url-part 15))
        result (queue/promise-subscription callback-queue-name (fn [a] (response-key a)))]
    (queue/publish-with-qname queue-name callback-queue-name {:titan_id titan-id
                                                              :level level})
    @result))

(defn get-sent-invites [titan-id]
  (get-query titan-id "com.webtalk.pre-launch.invite-count" 1 :invites))


