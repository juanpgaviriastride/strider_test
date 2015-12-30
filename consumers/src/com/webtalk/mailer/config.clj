(ns com.webtalk.mailer.config
  (:gen-class)
  (:require [environ.core :refer [env]]))

(def auth {:api_user (or (env :sengrid-username) "sarcilav")
           :api_key  (or (env :sengrid-key) "P@wU8Z#wGHAZ^n")})

(def base-url (or (env :root-url) "http://wtwebthor-env.elasticbeanstalk.com"))

(def inv-groupid (or (Integer. (env :inv-groupid)) 301))

(def user-groupid (or (Integer. (env :user-groupid)) 303))

(def sender-name (or (env :sender-name) "Webtalk"))

(def from-email (or (env :from-email) "no_reply@webtalk.co"))
