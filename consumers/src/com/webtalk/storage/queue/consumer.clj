(ns com.webtalk.storage.queue.consumer
  (:gen-class)
  (:require [langohr.consumers :as lconsumer]
            [clojure.data.json :as json]
            [taoensso.timbre :refer [info]]))


(defn helper
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [casted-payload (String. payload "UTF-8")]
    (info (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s, reply-to: %s"
                  casted-payload delivery-tag content-type type (:reply-to meta)))
    [(:reply-to meta) (json/read-str casted-payload)]))


(defn subscribe
  [channel queuename message-handler opts]
  (lconsumer/subscribe channel queuename (comp message-handler helper) opts))
