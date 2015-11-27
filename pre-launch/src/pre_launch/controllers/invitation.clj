(ns pre-launch.controllers.invitation.clj
  (:require
   [clj-wt.queue :as queue]
   [crypto.random :refer [url-part]]))


(defn send-invitation [invite-email referer-id]
  (let [callback-queue-name (str "com.webtalk.pre-launch.invitation" (url-part 15))
        result (queue/promise-subscription callback-queue-name )]
    (queue/publish-with-qname "com.webtalk.pre-launch.invitation" callback-queue-name
                              {:email invite-email
                               :refererID referer-id})))
