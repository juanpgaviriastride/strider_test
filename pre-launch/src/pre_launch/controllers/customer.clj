(ns pre-launch.controllers.customer
  (:require [pre-launch.integration.stripe :as stripe]
            [pre-launch.model.stripe-account :as model]
            [pre-launch.model.stripe-charge :as charge-model]
            [taoensso.timbre :refer [spy]]))



(defn create-customer! [source-token email]
  (let [stripe-customer (stripe/create-customer! {"source" source-token "email" email})
        save-response (model/save-stripe-account stripe-customer)
        stripe-charge (stripe/create-charge! {"customer" (:id stripe-customer)
                                              "amount" 10000
                                              "currency" "usd"})
        save-charge-response (charge-model/create-charge! stripe-charge)]
    (spy stripe-charge)
    (spy save-charge-response)
    
    save-response))
