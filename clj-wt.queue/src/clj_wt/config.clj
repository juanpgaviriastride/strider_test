(ns clj-wt.config
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split trim]]))

(defn rmq-config []
  (if-let [rmq-url (env :rabbitmq-1-port)]
    (trim rmq-url)
    (throw (Exception. "You need to set RABBITMQ_1_PORT env var"))))

(defn rmq-username []
  (if-let [username (or (env :rabbitmq-username) "guest")]
    (trim username)
    (throw (Exception. "You need to set RABBITMQ_USERNAME env var"))))

(defn rmq-password []
  (if-let [password (or (env :rabbitmq-password) "guest")]
    (trim password)
    (throw (Exception. "You need to set RABBITMQ_USERNAME env var"))))
