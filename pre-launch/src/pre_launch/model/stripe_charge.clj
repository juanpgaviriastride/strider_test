(ns pre-launch.model.stripe-charge
  (:require [pre-launch.db.core :as db]
            [clojure.data.json :as json]))

(defn serialize-json [map attr-name]
  (update-in map attr-name (json/write-str (attr-name map))))

(defn prepare-create [api-response]
  (-> api-response
     (serialize-json :source)
     (serialize-json :fraudDetails)
     (serialize-json :metadata)))

(defn create-charge! [stripe-api-response]
  (db/create-charge<! stripe-api-response))
