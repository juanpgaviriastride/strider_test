(ns pre-launch.controllers.customer
  (:require [pre-launch.integration.stripe :as stripe]
            [pre-launch.model.stripe-account :as model]))



(defn create-customer! [source-token email]
  (let [stripe-customer (stripe/create-customer! {"source" source-token "email" email})
        save-response (model/save-stripe-account stripe-customer)]
    save-response))
