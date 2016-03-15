(ns pre-launch.controllers.invitation
  (:require
   [clj-wt.queue :as queue]
   [crypto.random :refer [url-part]]
   [taoensso.timbre :refer [spy]]
   [pre-launch.model.user :as model]
   [pre-launch.controllers.cache :as cache]))

(defn send-invitation [invite-email referer-id custom-message]
  (cache/invalid (mapv #(str referer-id %) [:referral-network :network-detail]))
  (model/set-custom-email-message referer-id custom-message)
  (queue/publish-with-qname "com.webtalk.pre-launch.bulk-invite" "don't care"
                            {:emails invite-email
                             :refererID referer-id
                             :custom_message custom-message}))
