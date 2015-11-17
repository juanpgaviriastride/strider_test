(ns wt.persistence.queue.consumer
  (:gen-class)
  (:require [langohr.consumers :as lconsumer]
            [clojure.data.json :as json]
            [langohr.core                       :as rmq]
            [langohr.channel                    :as lch]
            [langohr.queue                      :as lq]
            [wt.persistence.queue.config   :as config]
            [langohr.basic                      :as lb]
            [wt.persistence.queue.config :as config]))

(def ^{:const true}
  default-exchange-name "")

(defn helper
  [channel {:keys [content-type delivery-tag type] :as meta} ^bytes payload]
  (let [casted-payload (String. payload "UTF-8")]
    (println (format "[consumer] Received a message: %s, delivery tag: %d, content type: %s, type: %s, reply-to: %s"
                     casted-payload delivery-tag content-type type (:reply-to meta)))
    (json/read-str casted-payload :key-fn keyword)))


(defn subscribe
  [channel queuename message-handler opts]
  (lconsumer/subscribe channel queuename (comp message-handler helper) opts))




(defn subscribe-with-connection [qname message-handler]
  (println "Getting ready to setup" qname)
  (let [connection (rmq/connect {:host (config/rmq-config)})
        channel    (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (lq/declare channel qname {:durable true :auto-delete false :exclusive false})
    (println "creating consumer" message-handler)
    (subscribe channel qname message-handler {:auto-ack true})
    (println "subscribed" message-handler)
    [channel]))
