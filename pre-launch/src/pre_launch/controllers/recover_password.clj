(ns pre-launch.controllers.recover-password
  (:require [buddy.sign.jwe :as jwe]
            [buddy.core.nonce :refer [random-nonce]]
            [selmer.parser :as parser]
            [sendgrid-java-wrapper.core :as mailer]
            [environ.core :refer [env]]
            [clojure.java.io :as io]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]))
  

(def secret (random-nonce 32))
(def root-url "http://localhost:3000")
(def auth {:api_user (or (env :sengrid-username) "sarcilav")
           :api_key  (or (env :sengrid-key) "P@wU8Z#wGHAZ^n")})

(parser/set-resource-path!  (io/resource "templates"))


(defn deliver-email [user-id user-email]
  (mailer/send-email auth
                     {:to user-email
                      :from "team@webtalk.co"
                      :subject "Webtalk | Recover password"
                      :html (parser/render-file
                             "emails/recover-password.html"
                             {:url (str
                                    root-url
                                    "/new-password/"
                                    (generate-token user-id user-email))})}))

(defn generate-token [user-id user-email]
  (jwe/encrypt {:id user-id
                :email user-email
                :exp (time-coerce/to-long (time/plus (time/now) (time/hours 1)))}
               secret {:alg :a256kw :enc :a128gcm}))

(defn decrypt-token [token]
  (jwe/decrypt token secret {:alg :a256kw :enc :a128gcm}))
