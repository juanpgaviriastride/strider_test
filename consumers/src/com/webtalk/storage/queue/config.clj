(ns com.webtalk.storage.queue.config
  (:gen-class)
  (:require [com.webtalk.util :refer [get-rmq-host]]))

(def rmq-config get-rmq-host)
