(ns com.webtalk.mailer.prelaunch-request-an-invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [to]
  (mailer/send-email config/auth
                     {:to to
                      :from "team@webtalk.co"
                      :subject "Waitlist Confirmation"
                      :html (template/render-resource
                             "templates/prelaunch_request_an_invite.html.mustache"
                             {:to to
                              :join_url (str config/base-url)})}))