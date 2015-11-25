(ns pre-launch.routes.payment
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [pre-launch.integration.stripe :as stripe]
            [ring.util.response :refer [response]]
            [clojure.data.json :as json]
            [pre-launch.integration.stripe :as stripe]
            ring.middleware.session))

(defn create-customer! [request]
  (println "inside create stripe-customer and the info sent is" request)
  (let [payload (json/read-str (request :payload))
        stripe-customer (stripe/create-customer! {"source" (payload "id")
                                                  "email" (payload "email")})]
    (println "payload" payload)
    (println "id token" (payload "id"))
    (println "stripe customer" stripe-customer)
    (ok)))

(defroutes payment-routes
  (POST "/stripe/customer" request (create-customer! (:params request))))
