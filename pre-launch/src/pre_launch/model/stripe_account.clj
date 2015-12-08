(ns pre-launch.model.stripe-account
  (:require [pre-launch.db.core :as db]
            [taoensso.timbre :refer [debug]]))


(defn save-stripe-account [stripe-account]
  (when-let [stripe-id (db/create-stripe-account<! stripe-account)]
    (assoc stripe-account :id (:generated_key stripe-id))))


(defn stripe-users-count []
  (let [response (db/get-accounts-count)]
    (debug "the stripe users account is" response)
    (:total (first response))))
