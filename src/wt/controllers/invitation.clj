(ns wt.controllers.invitation
  (:require [wt.persistence.invitation :as model]))

(defn save-invitation [invitation]
  (let [maybe-invitation (model/save-invitation invitation)]
    (println "the maybe invitation is " maybe-invitation)
    {:invitation maybe-invitation}
    )
  )

(defn get-invitation [invitation-id]
  (let [invitation (model/get-invitation invitation-id)]
    (if (nil? invitation)
      {:status 404 :body nil}
      {:status 200 :body {:invitation invitation}})))
