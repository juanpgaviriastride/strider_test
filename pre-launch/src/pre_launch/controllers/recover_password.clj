(ns pre-launch.controllers.recover-password
  (:require [buddy.sign.jwe :as jwe]
            [buddy.core.nonce :refer [random-nonce]]
            [selmer.parser :as parser]
            [sendgrid-java-wrapper.core :as mailer]
            [environ.core :refer [env]]
            [clojure.java.io :as io]
            [clj-time.core :as time]
            [clj-time.coerce :as time-coerce]
            [pre-launch.config-mailer :as config-mailer]))


(def secret (random-nonce 32))

(def jwe-hashing {:alg :a256kw :enc :a128gcm})

(parser/set-resource-path!  (io/resource "templates"))

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

(defn valid-timing?
  ([token]
   (let [{:keys [id email exp]} (decrypt-token token)]
     (valid-timing? id email exp)))
  ([id email exp]
   (and id email exp (> exp (time-coerce/to-long (time/now))))))

(defn deliver-email [user-id user-email]
  (let [email-agent (agent {:user-email user-email :user-id user-id})
        email-fn (fn [{user-email :user-email user-id :user-id}]
                   (println "sending the recover password email")
                   (mailer/send-email (config-mailer/auth)
                                      {:to user-email
                                       :from "team@webtalk.co"
                                       :subject "Webtalk | Recover password"
                                       :html (parser/render-file
                                              "emails/recover-password.html"
                                              {:url (str
                                                     root-url
                                                     "/new-password/"
                                                     (generate-token user-id user-email))})}))]
    (send-off email-agent email-fn)))
