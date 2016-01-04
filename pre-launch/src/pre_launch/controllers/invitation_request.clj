(ns pre-launch.controllers.invitation-request
  (:require [clj-wt.queue :as queue]
            [pre-launch.model.invitation-request :as model]
            [taoensso.timbre :refer [spy]]
            [pre-launch.controllers.referal-notification :as referal-controller]
            [crypto.random :refer [url-part]]))


(defn join-wait-list [email maybe-referer-id]
  (let [callback-queue-name (str "crowdfunding.webtalk." (url-part 15))
        response (future (model/save-invitation-request email maybe-referer-id))]
    (queue/publish-with-qname "com.webtalk.pre-launch.request-an-invite" callback-queue-name {:email email
                                                                                              :refererID maybe-referer-id})
    (when maybe-referer-id
 		(referal-controller/notify-referer maybe-referer-id email))))
