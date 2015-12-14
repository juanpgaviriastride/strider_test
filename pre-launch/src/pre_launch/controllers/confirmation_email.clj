(ns pre-launch.controllers.confirmation-email
  (:require 
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [sendgrid-java-wrapper.core :as mailer]
            [pre-launch.config-mailer :as config-mailer]
            [clojure.data.json :as json]))


(defn notify-referer [referer-titan-id refered-name]
  

  )

(defn confirmation-data [data]
  (assoc data :base-url (config-mailer/root-url)))


(defn deliver-email [email user-id payload]
  (let [email-agent (agent {:email email :user-id user-id})
        send-fn (fn [{user-id :user-id email :email}]
                  (println "sending the receipt email...")
                  (let [confirm-data (confirmation-data payload)]
                    (mailer/send-email (config-mailer/auth)
                                       {:to email
                                        :from "no_reply@webtalk.co"
                                        :subject "Welcome & Thank You!"
                                        :html (parser/render-file
                                               "emails/pre-launch.html"
                                               confirm-data)})))]

    (send-off email-agent send-fn)))
