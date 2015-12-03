(ns pre-launch.model.stripe-account
  (:require [pre-launch.db.core :as db]))


(defn save-stripe-account [stripe-account]
  (when-let [stripe-id (db/create-stripe-account<! stripe-account)]
    (assoc stripe-account :id (:generated_key stripe-id))))


(defn stripe-users-count []
  (let [response (db/get-accounts-count)]
    (println "the stripe users account is" response)
    (:total (first response))))
