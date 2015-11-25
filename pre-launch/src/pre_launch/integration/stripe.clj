(ns pre-launch.integration.stripe
  (:require [environ.core :refer [env]]
            [clojure.data.json :as json])
  (:import com.stripe.Stripe
           com.stripe.exception.StripeException
           com.stripe.model.Charge
           com.stripe.model.Customer
           com.stripe.net.RequestOptions
           com.stripe.net.RequestOptions$RequestOptionsBuilder
           com.google.gson.Gson))


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

(defn customer-hash [payload]
  (into {} (list payload {:stripe_id (payload :id)
                          :default_source (payload :defaultSource)
                          :currency "usd"
                          :description ""
                          :connect_access_token ""
                          :refresh_token ""
                          :token_type ""
                          :sources (json/write-str (bean (payload :sources)))
                          :scope ""})))

(defn customer-from-response [customer-object]
  (-> customer-object
     bean
     (select-keys [:sources :id :email :defaultSource])
     customer-hash))

(defn create-customer! [customer-params]
  (customer-from-response (Customer/create customer-params (default-request-options))))

(defn charge-hash [payload]
  (into {} (list payload {,,,
                          ;;defaults here
                          })))

(defn charge-from-response [charge-object]
  (-> charge-object
     bean
     (select-keys [:description :disputed :applicationFee :amount :failureMessage :transfer :captured :dispute :created :source :statementDescription :failureCode :customer :balanceTransaction :receiptEmail :card :invoice :currency :refunded :amountRefunded :status :id :class :statementDescriptor :paid :fraudDetails :livemode :shipping :receiptNumber :metadata :destination :refunds])
     charge-hash))

(defn create-charge! [charge-params]
  (charge-from-response (Charge/create charge-params (default-request-options))))
