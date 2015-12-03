(ns pre-launch.routes.payment
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [pre-launch.integration.stripe :as stripe]
            [ring.util.response :refer [response content-type]]
            [clojure.data.json :as json]
            [pre-launch.controllers.receipt :as receipt-model]
            [pre-launch.integration.stripe :as stripe]
            [pre-launch.controllers.customer :as controller]
            ring.middleware.session))

(defn create-customer! [request session]
  (println "inside create stripe-customer and the info sent is" (receipt-model/get-payment-detail 1))
  (let [payload (json/read-str (request :payload))
        user-response (controller/create-customer! (payload "id") (payload "email"))]
    (-> (response user-response)
       (content-type "application/json")
       (assoc :session (assoc session
                              :stripe-costumer (user-response :id))))))

(defroutes payment-routes
  (POST "/stripe/customer" request (create-customer! (:params request) (:session request))))
