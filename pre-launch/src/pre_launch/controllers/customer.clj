(ns pre-launch.controllers.customer
  (:require [pre-launch.integration.stripe :as stripe]
            [pre-launch.model.stripe-account :as model]
            [clojure.data.json :as json])
  (:import com.google.gson.Gson))


(defn customer-hash [payload]
  (into {} (list payload {
                          :stripe_id (payload :id)
                          :default_source (payload :defaultSource)
                          :currency "usd"
                          :description ""
                          :connect_access_token ""
                          :refresh_token ""
                          :token_type ""
                          :sources (json/write-str (payload :sources))
                          :scope ""})))

(defn map-from-response [customer-object]
  (let [partial-response (-> (Gson.)
                            (.toJson customer-object)
                            (json/read-str :key-fn keyword) 
                            (select-keys [:sources :id :email :defaultSource]))]
    (customer-hash partial-response)))

(defn create-customer! [source-token email]
  (let [stripe-customer (stripe/create-customer! {"source" source-token "email" email})
        save-response (model/save-stripe-account (map-from-response stripe-customer))]
    save-response))
