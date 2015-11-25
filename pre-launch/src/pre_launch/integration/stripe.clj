(ns pre-launch.integration.stripe
  (:require [environ.core :refer [env]]))


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
