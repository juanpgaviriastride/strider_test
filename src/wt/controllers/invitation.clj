(ns wt.controllers.invitation
  (:require
   [wt.persistence.invitation :as model]
   [wt.persistence.queue.publisher :as publisher]))

(defn save-invitation [invitation]
  (let [maybe-invitation (model/save-invitation invitation)]
    (println "the maybe invitation is " maybe-invitation)
    ;;TODO: link the sql and nosql users by inviter_id
    (publisher/publish-with-qname "com.webtalk.storage.queue.invite" "com.webtalk.invitations" {:user_id 53760256 :email (:email maybe-invitation)})
    {:invitation maybe-invitation}))

(defn get-invitation [invitation-id]
  (let [invitation (model/get-invitation invitation-id)]
    (if (nil? invitation)
      {:status 404 :body nil}
      {:status 200 :body {:invitation invitation}})))
