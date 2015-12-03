(ns pre-launch.controllers.receipt
  (:require [pre-launch.model.receipt :as model]
            [selmer.parser :as parser]
            [sendgrid-java-wrapper.core :as mailer]
            [clojure.data.json :as json]))

(defn filter-data [response]
  (let [payment (json/read-str (:source response) :key-fn keyword)]
    {:user-name (:name response)
     :id (:id response)
     :date-purchased (java.util.Date. (:created response))
     :payment-method {:brand-name (:brand payment)
                      :last4  (:last4 payment)
                      }
     :ammount (:amount response)}))



(defn get-payment-detail [user-id]
  (filter-data (model/receipt-data user-id)))


(defn deliver-email [email user-id payment-data]
  (mailer/send-email auth
                     {:to user-email
                      :from "team@webtalk.co"
                      :subject "Webtalk | Payment receipt"
                      :html (parser/render-file
                             "emails/payment-confirmation.html"
                             (get-payment-detail user-id))}))
