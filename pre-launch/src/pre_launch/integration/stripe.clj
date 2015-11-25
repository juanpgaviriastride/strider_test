(ns pre-launch.integration.stripe
  (:require [environ.core :refer [env]])
  (:import com.stripe.Stripe
           com.stripe.exception.StripeException
           com.stripe.model.Charge
           com.stripe.model.Customer
           com.stripe.net.RequestOptions
           com.stripe.net.RequestOptions$RequestOptionsBuilder))


(defn payment [customer]
  (println "inside payment for customer"))

(defn public-key []
  (if-let [stripe-key (env :stripe-public-key)]
    (do
      (println "stripe-key" stripe-key)
      stripe-key)
    (throw (Exception. "No environment variable found with key STRIPE_PUBLIC_KEY. Please set the environment variable."))))

(defn private-key []
  (if-let [stripe-key (env :stripe-private-key)]
    stripe-key
    (throw (Exception. "No environment variable found with key STRIPE_PRIVATE_KEY. Please set the environment variable."))))

(defn default-request-options []
  (-> (RequestOptions$RequestOptionsBuilder. )
     (.setApiKey (private-key))
     .build))

(defn create-customer! [customer-params]
  (Customer/create customer-params (default-request-options)))
