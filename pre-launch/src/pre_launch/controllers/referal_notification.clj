(ns pre-launch.controllers.referal-notification
  (:require 
   [selmer.parser :as parser]
   [pre-launch.model.user :as user-model]
   [environ.core :refer [env]]
   [sendgrid-java-wrapper.core :as mailer]
   [pre-launch.config-mailer :as config-mailer]
   [clojure.data.json :as json]))


(defn deliver-email [payload email]
  (let [email-agent (agent {:email email})
        send-fn (fn [{email :email}]
                  (mailer/send-email-groupid (config-mailer/auth)
                                       {:to email
                                        :from (config-mailer/from-email)
                                        :subject "Congrats! You have a new referral!"
                                        :html (parser/render-file
                                               "emails/join-notification.html"
                                               payload)
                                        :from-name (config-mailer/sender-name)
                                        :group-id (config-mailer/user-groupid)}))]
    (send-off email-agent send-fn)))


(defn notify-referer [referer-titan-id refered-name]
  (when-let [referer-data (user-model/email-by-titan referer-titan-id)]
    (-> referer-data
       (assoc :refered-name refered-name)
       (assoc :referer-titan referer-titan-id)
       (assoc :base-url (config-mailer/root-url))
       (deliver-email (:email referer-data)))))




