(ns com.webtalk.mailer.prelaunch-invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [sender to]
  (mailer/send-email-groupid config/auth
                     {:to to
                      :from config/from-email
                      :subject (str "Congrats! " (:name sender) " has recommended you!")
                      :html (template/render-resource
                             "templates/prelaunch_invite.html.mustache"
                             {:to to
                              :name (:name sender)
                              :join_url (str config/base-url "/invite/" (:__id__ sender))})
                      :from-name config/sender-name
                      :group-id config/inv-groupid}))

(defn bulk-email [sender bcc]
  (mailer/bulk-email-groupid config/auth {:bcc bcc
                                  :from config/from-email
                                  :subject (str "Congrats! " (:name sender) " has recommended you!")
                                  :html (template/render-resource
                                         "templates/prelaunch_invite.html.mustache"
                                         {:to bcc
                                          :name (:name sender)
                                          :join_url (str config/base-url "/invite/" (:__id__ sender))})
                                  :from-name config/sender-name
                                  :group-id config/inv-groupid}))
