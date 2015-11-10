(ns wt.persistence.invitation
  (:require [wt.db.core :as db]))

(defn save-invitation [invitation]
  (let [insert-result (db/create-invitation<! invitation)]
    (assoc invitation :id (:generated_key insert-result))))

(defn get-invitation [invitation-id]
  (let [maybe-invitation (db/get-invitation {:id invitation-id})]
    (first maybe-invitation)))


(defn delete-invitation [invitation-id]
  (db/delete-invitation! invitation-id))
