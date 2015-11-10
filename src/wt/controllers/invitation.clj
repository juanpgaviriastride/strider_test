(ns wt.controllers.invitation
  (:require [wt.persistence.invitation :as model]))

(defn save-invitation [invitation]
  (let [maybe-invitation (model/save-invitation invitation)]
    (println "the maybe invitation is " maybe-invitation)
    {:invitation maybe-invitation}
    )
  )

(defn get-invitation [invitation-id]
  {:invitation (model/get-invitation invitation-id)})
