(ns com.webtalk.mailer.invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [sender token to]
  (mailer/send-email config/auth
                     {:to to
                      :from "team@webtalk.co"
                      :subject (str "Webtalk | Congratulations! " (:full_name sender) " has recommended you!")
                      :html (template/render-resource
                             "templates/invite.html.mustache"
                             {:name (:full_name sender)
                              :position (:position sender)
                              :image_url (:avatar_url sender)
                              :join_url (str config/base-url "/join/" token)
                              :sign_in_url (str config/base-url "/signin")
                              :profile_url (str config/base-url "/" (:username sender))})}))
