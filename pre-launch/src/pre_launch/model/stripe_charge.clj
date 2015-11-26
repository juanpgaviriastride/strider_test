(ns pre-launch.model.stripe-charge
  (:require [pre-launch.db.core :as db]
            [clojure.data.json :as json]))

(defn create-charge! [stripe-api-response]
  (let [maybe-charged (db/create-charge<! stripe-api-response)]
    (assoc stripe-api-response :id (:generated_key maybe-charged))))
