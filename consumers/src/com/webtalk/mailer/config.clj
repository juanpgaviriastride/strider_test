(ns com.webtalk.mailer.config
  (:gen-class)
  (:require [environ.core :refer [env]]))

(def auth {:api_user (env :sengrid-username)
           :api_key  (env :sengrid-key)})

(def base-url (env :base-url))
