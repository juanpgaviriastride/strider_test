(ns com.webtalk.mailer.invite
  (:gen-class)
  (:require [sendgrid-java-wrapper.core :as mailer]
            [com.webtalk.mailer.config :as config]
            [clojure.java.io :as io]
            [clostache.parser :as template]))

(defn deliver-email [sender invitation to]
  (mailer/send-email config/auth
                     {:to to
                      :from "team@webtalk.co"
                      :subject "[Webtalk] You have been invited to join us"
                      :html (template/render-resource
                             "templates/invite.html.mustache"
                             {:name "Chila"
                              :position "NullDev at Nullindustries"
                              :image_url "https://scontent-mia.xx.fbcdn.net/hphotos-xfp1/t31.0-8/s720x720/132514_158085784241321_7607493_o.jpg"
                              :join_url "http://localhost:3000/"
                              :sign_in_url "http://localhost:3000/"})}))
