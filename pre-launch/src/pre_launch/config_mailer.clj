(ns pre-launch.config-mailer
  (:require [environ.core :refer [env]]))

(defn root-url []
  (or (env :root-url) "http://localhost:3000"))

(defn webtalk-url []
  (or (env :wt-url) "webtalk.co"))

(defn auth []
  (let [user (env :sengrid-username)
        key (env :sengrid-key)]
    (if (and user key)
      {:api_user user 
       :api_key  key}
      {:api_user "sarcilav"
       :api_key  "P@wU8Z#wGHAZ^n"})))

(defn inv-groupid []
  (or (Integer. (env :inv-groupid)) 301))

(defn user-groupid []
  (or (Integer. (env :user-groupid)) 303))

(defn sender-name []
  (or (env :sender-name) "Webtalk"))

(defn from-email []
  (or (env :from-email) "no_reply@webtalk.co"))
