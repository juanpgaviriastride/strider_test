(ns clj-wt.config
  (:require [environ.core :refer [env]]
            [clojure.string :refer [split trim]]))

(defn rmq-config []
  (trim (env :rabbitmq-1-port)))
