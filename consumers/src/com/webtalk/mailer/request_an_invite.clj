(ns com.webtalk.mailer.request-an-invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]))

(defn deliver-email [to]
  (mailer/send-email config/auth
                     {:to to
                      :from "no_reply@webtalk.co"
                      :subject "Waitlist Confirmation"
                      :html (slurp (io/resource "templates/request_an_invite.html"))}))
