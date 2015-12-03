(ns pre-launch.model.receipt
  (:require [pre-launch.db.core :as db]))


(defn receipt-data [user-id]
  (let [payment-data (db/get-receipt {:user_id user-id})]
    (println "the payment data for email is" payment-data)
    (first payment-data)))
