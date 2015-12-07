(ns pre-launch.integration.stripe
  (:require [environ.core :refer [env]]
            [clojure.data.json :as json]
            [taoensso.timbre :refer [debug spy]])
  (:import com.stripe.Stripe
           com.stripe.exception.StripeException
           com.stripe.model.Charge
           com.stripe.model.Customer
           com.stripe.net.RequestOptions
           com.stripe.net.RequestOptions$RequestOptionsBuilder
           com.google.gson.Gson))


(defn payment [customer]
  (debug "inside payment for customer"))

(defn public-key []
  (if-let [stripe-key (env :stripe-public-key)]
    (spy stripe-key)
    (throw (Exception. "No environment variable found with key STRIPE_PUBLIC_KEY. Please set the environment variable."))))

(defn private-key []
  (if-let [stripe-key (env :stripe-private-key)]
    stripe-key
    (throw (Exception. "No environment variable found with key STRIPE_PRIVATE_KEY. Please set the environment variable."))))

(defn default-request-options []
  (-> (RequestOptions$RequestOptionsBuilder. )
     (.setApiKey (private-key))
     .build))

(defn to-json [object]
  (.toJson (Gson.) object))

(defn customer-hash [payload]
  (into {} (list payload {:stripe_id (payload :id)
                          :default_source (payload :defaultSource)
                          :currency "usd"
                          :description ""
                          :connect_access_token ""
                          :refresh_token ""
                          :token_type ""
                          :sources (to-json (:sources payload)) 
                          :scope ""})))

(defn customer-from-response [customer-object]
  (-> customer-object
     bean
     (select-keys [:sources :id :email :defaultSource])
     customer-hash))

(defn create-customer! [customer-params]
  (customer-from-response (Customer/create customer-params (default-request-options))))

(defn charge-hash [payload]
  (debug "inside charge-hash")
  (spy payload)
  (spy (class (payload :source)))
  (spy (bean (payload :source)))
  (into {} (list payload {:source (to-json (:source payload))
                          :fraudDetails (to-json (:fraudDetails payload))
                          :metadata (to-json (:metadata payload))
                          :refunds (to-json (:refunds payload))
                          })))

(defn charge-from-response [charge-object]
  (-> charge-object
     bean
     charge-hash))

(defn create-charge! [charge-params]
  (debug "inside create-charge!")
  (spy charge-params)
  (charge-from-response (Charge/create charge-params (default-request-options))))
