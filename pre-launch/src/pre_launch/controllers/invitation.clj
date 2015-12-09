(ns pre-launch.controllers.invitation
  (:require
   [clj-wt.queue :as queue]
   [crypto.random :refer [url-part]]
   [taoensso.timbre :refer [spy]]))


(defn on-invitation-received [request-response]
  (spy request-response))

(defn send-invitation [invite-email referer-id]
  (let [callback-queue-name (str "com.webtalk.pre-launch.bulk-invite" (url-part 15))
        result (queue/promise-subscription callback-queue-name on-invitation-received)]
    (queue/publish-with-qname "com.webtalk.pre-launch.bulk-invite" callback-queue-name
                              {:emails invite-email
                               :refererID referer-id})))
