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
                             {:to to
                              :name (:name sender)
                              :join_url (str config/base-url "/invite/" (:__id__ sender))})}))

(defn bulk-email [sender bcc]
  (mailer/bulk-email config/auth {:bcc bcc
                                  :from "team@webtalk.co"
                                  :subject (str "Webtalk | Congratulations! " (:name sender) " has recommended you!")
                                  :html (template/render-resource
                                         "templates/prelaunch_invite.html.mustache"
                                         {:to "Future Webtalker"
                                          :name (:name sender)
                                          :join_url (str config/base-url "/invite/" (:__id__ sender))})}))
