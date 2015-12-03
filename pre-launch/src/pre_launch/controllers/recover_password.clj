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

(def jwe-hashing {:alg :a256kw :enc :a128gcm})

(parser/set-resource-path!  (io/resource "templates"))

(defn valid-timing?
  ([token]
   (let [{:keys [id email exp]} (controller/decrypt-token token)]
     (valid-timing? id email exp)))
  ([id email exp]
   (and id email exp (> exp (time-coerce/to-long (time/now))))))

(defn generate-token [user-id user-email]
  (jwe/encrypt {:id user-id
                :email user-email
                :exp (time-coerce/to-long (time/plus (time/now) (time/hours 1)))}
               secret jwe-hashing))

(defn decrypt-token [token]
  (try
    (jwe/decrypt token secret jwe-hashing)
    (catch Exception e
      (hash-map))))

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
