(ns pre-launch.controllers.invitation
  (:require
   [clj-wt.queue :as queue]
   [crypto.random :refer [url-part]]
   [taoensso.timbre :refer [spy]]
   [pre-launch.controllers.cache :as cache]))

(defn send-invitation [invite-email referer-id]
  (cache/invalid (mapv #(str referer-id %) [:referral-network :network-detail]))
  (queue/publish-with-qname "com.webtalk.pre-launch.bulk-invite" "don't care"
                            {:emails invite-email
                             :refererID referer-id}))
