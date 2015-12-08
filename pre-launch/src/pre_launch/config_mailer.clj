(ns pre-launch.config-mailer
  (:require [environ.core :refer [env]]))

(defn root-url []
  (or (env :root-url) "http://localhost:3000"))

(defn auth []
  (let [user (env :sengrid-username)
        key (env :sengrid-key)]
    (if (and user key)
      {:api_user user 
       :api_key  key}
      {:api_user "sarcilav"
       :api_key  "P@wU8Z#wGHAZ^n"})))
