(ns pre-launch.controllers.receipt
  (:require [pre-launch.model.receipt :as model]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [sendgrid-java-wrapper.core :as mailer]
            [clojure.data.json :as json])

  (:import [java.text.DecimalFormat]))


(def auth {:api_user (or (env :sengrid-username) "sarcilav")
           :api_key  (or (env :sengrid-key) "P@wU8Z#wGHAZ^n")})


(defn money-filter [ammount]
  (.format (java.text.DecimalFormat. "$0.00") (/ ammount 100.0)))

(defn filter-data [response]
  (let [payment (json/read-str (:source response) :key-fn keyword)]
    {:user-name (:name response)
     :id (:id response)
     :date-purchased (java.util.Date. (:created response))
     :payment-method {:brand-name (:brand payment)
                      :last4  (:last4 payment)
                      }
     :ammount  (money-filter (:amount response))}))

(defn get-payment-detail [user-id]
  (filter-data (model/receipt-data user-id)))


(defn deliver-email [email user-id]
  (let [email-agent (agent {:email email :user-id user-id})
        send-fn (fn [{user-id :user-id email :email}]
                  (println "sending the receipt email...")
                  (let [payment-data (get-payment-detail user-id)]
                    (mailer/send-email auth
                                       {:to email
                                        :from "team@webtalk.co"
                                        :subject "Your Credits Purchase Receipt"
                                        :html (parser/render-file
                                               "emails/payment-confirmation.html"
                                               payment-data)})))]

    (send-off email-agent send-fn)))
