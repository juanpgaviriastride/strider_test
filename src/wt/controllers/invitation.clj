(ns wt.controllers.invitation
  (:require
   [wt.persistence.invitation :as model]
   [wt.persistence.queue.publisher :as publisher]
   [crypto.random :refer [url-part]]
   [wt.persistence.queue.consumer :as consumer]))

(defn invitation-sent [{inviter_id :__id__ email :email token :invitationToken :as payload}]
  (println {:inviter_id inviter_id :email email :token token} "payload" payload)
  (let [maybe-invitation (model/save-invitation {:inviter_id inviter_id :email email :token token})]
    (println "maybe invitation" maybe-invitation)
    maybe-invitation))

(defn save-invitation [invitation]
  (let [callback-queue-name (str "web.webtalk.invitation." (url-part 15))
        result (promise)]
    (consumer/subscribe-with-connection callback-queue-name (fn [payload] (deliver result (invitation-sent payload))))
    ;;TODO: link the sql and nosql users by inviter_id
    (publisher/publish-with-qname "com.webtalk.storage.queue.invite" callback-queue-name {:user_id 53760256 :email (:email invitation)})
    (println "result is" @result)
    (if (nil? result)
      {:status 400 :body nil}
      {:status 200 :body {:invitation @result}})))

(defn get-invitation [invitation-id]
  (let [invitation (model/get-invitation invitation-id)]
    (if (nil? invitation)
      {:status 404 :body nil}
      {:status 200 :body {:invitation invitation}})))


(defn validate-invitation [token]
  (let [maybe-invitation  (model/find-by-token token)]
    (println "maybe invitation" maybe-invitation)
    (if (nil? maybe-invitation)
      {:status 404 :body nil}
      {:status 200 :body {:invitation maybe-invitation}})))
