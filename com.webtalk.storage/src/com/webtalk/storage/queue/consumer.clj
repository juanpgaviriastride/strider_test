(ns com.webtalk.storage.queue.consumer
  (:gen-class)
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as lchannel]
            [langohr.queue     :as lqueue]
            [langohr.consumers :as lconsumer]))

(defn message-handler
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s"
                   (String. payload "UTF-8") delivery-tag content-type type)))

(defn subscribe
  [channel queuename opts]
  (lconsumer/subscribe channel queuename message-handler opts))