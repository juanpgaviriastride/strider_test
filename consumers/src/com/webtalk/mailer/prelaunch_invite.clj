(ns com.webtalk.mailer.prelaunch-invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [sender to]
  (mailer/send-email config/auth
                     {:to to
                      :from "team@webtalk.co"
                      :subject (str "Webtalk | Congratulations! " (:name sender) " has recommended you!")
                      :html (template/render-resource
                             "templates/prelaunch_invite.html.mustache"
                             {:name (:name sender)
                              :join_url (str "/invite/" (:__id__ sender))})}))