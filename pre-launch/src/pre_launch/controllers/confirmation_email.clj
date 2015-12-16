(ns pre-launch.controllers.confirmation-email
  (:require 
            [selmer.parser :as parser]
            [pre-launch.model.user :as user-model]
            [environ.core :refer [env]]
            [sendgrid-java-wrapper.core :as mailer]
            [pre-launch.config-mailer :as config-mailer]
            [clojure.data.json :as json]))


(defn notify-referer [referer-titan-id refered-name]
  

  )

(defn confirmation-data [data email user-id]
  (-> data
    (assoc :base-url (config-mailer/root-url))  
    (assoc :name (:name (user-model/get email)))
    (assoc :titan-id user-id)
    (assoc :email email)

    )

  )


(defn deliver-email [email user-id payload]
  (let [email-agent (agent {:email email :user-id user-id})
        send-fn (fn [{user-id :user-id email :email}]
                  (println "sending the receipt email...")
                  (let [confirm-data (confirmation-data payload email user-id)]
                    (mailer/send-email (config-mailer/auth)
                                       {:to email
                                        :from "no_reply@webtalk.co"
                                        :subject "Welcome & Thank You!"
                                        :html (parser/render-file
                                               "emails/pre-launch.html"
                                               confirm-data)})))]

    (send-off email-agent send-fn)))
