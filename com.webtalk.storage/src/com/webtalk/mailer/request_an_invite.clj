(ns com.webtalk.mailer.request-an-invite
  (:gen-class)
  (:require [sendgrid-clj.core :as sendgrid]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]))

(defn deliver-email [to]
  (sendgrid/send-email config/auth
                       {:to to
                        :from "Webtalk Team <no-reply@webtalk.co>"
                        :subject "Thank you for joining the wait list"
                        :html (slurp (io/resource "templates/request_an_invite.html"))}))
