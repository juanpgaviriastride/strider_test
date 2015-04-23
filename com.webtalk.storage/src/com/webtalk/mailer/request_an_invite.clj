(ns com.webtalk.mailer.request-an-invite
  (:gen-class)
  (:require [sendgrid-clj.core :as sendgrid]
            [com.webtalk.mailer.config :as config]))

(defn deliver-email [to]
  (sendgrid/send-email config/auth
              {
               :to to
               :from "chila@wt-mailer.com"
               :subject "You are on the waiting list"
               :html "<h1>Welcome to wt waiting list</h1> if you are RJ, RJ please remember to share you credentials and update the email copy for this and the template"}))


