(ns com.webtalk.mailer.request-an-invite
  (:gen-class)
  (:require [com.webtalk.mailer :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]))

(defn deliver-email [to]
  (mailer/send-email config/auth
                     {:to to
                      :from "team@webtalk.co"
                      :subject "Thank you for joining the wait list"
                      :html (slurp (io/resource "templates/request_an_invite.html"))}))
