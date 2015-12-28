(ns com.webtalk.mailer.prelaunch-request-an-invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [to]
  (mailer/send-email-groupid config/auth
                     {:to to
                      :from config/from-email
                      :subject "Waitlist Confirmation"
                      :html (template/render-resource
                             "templates/prelaunch_request_an_invite.html.mustache"
                             {:to to
                              :join_url config/base-url})
                      :from-name config/sender-name
                      :group-id config/inv-groupid}))
