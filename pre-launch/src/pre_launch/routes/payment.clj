(ns pre-launch.routes.payment
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [pre-launch.integration.stripe :as stripe]
            [ring.util.response :refer [response content-type status]]
            [clojure.data.json :as json]
            [pre-launch.controllers.receipt :as receipt-model]
            [pre-launch.integration.stripe :as stripe]
            [pre-launch.controllers.customer :as controller]
            [taoensso.timbre :refer [debug]]
            ring.middleware.session))

(defn create-customer! [request session]
  (debug "inside create stripe-customer and the info sent is" request)
  (let [payload (json/read-str (request :payload))
        user-response (try
                        (controller/create-customer! (payload "id") (payload "email"))
                        (catch com.stripe.exception.StripeException e
                          {:error true
                           :message (.getMessage e)}))]
    (if (:error user-response)
      (-> (response user-response)
         (content-type "application/json")
         (status 402))
      (-> (response user-response)
         (content-type "application/json")
         (assoc :session (assoc session
                                :stripe-costumer (user-response :id)))))))

(defroutes payment-routes
  (POST "/stripe/customer" request (create-customer! (:params request) (:session request))))
