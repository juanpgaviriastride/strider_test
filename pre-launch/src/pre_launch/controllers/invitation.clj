(ns pre-launch.controllers.invitation
  (:require
   [clj-wt.queue :as queue]
   [crypto.random :refer [url-part]]))


(defn on-invitation-received [request-response]
  (println "the result is " request-response))

(defn send-invitation [invite-email referer-id]
  (let [callback-queue-name (str "com.webtalk.pre-launch.invite" (url-part 15))
        result (queue/promise-subscription callback-queue-name on-invitation-received)]
    (queue/publish-with-qname "com.webtalk.pre-launch.invite" callback-queue-name
                              {:email invite-email
                               :refererID referer-id})))
