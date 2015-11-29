(ns pre-launch.controllers.invitation-request
  (:require [clj-wt.queue :as queue]
            [crypto.random :refer [url-part]]))


(defn join-wait-list [email maybe-referer-id]
  (let [callback-queue-name (str "crowdfunding.webtalk." (url-part 15))]
    (queue/publish-with-qname "com.webtalk.pre-launch.request-an-invite" callback-queue-name {:email email
                                                                                                 :refererID maybe-referer-id})))

