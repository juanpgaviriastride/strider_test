(ns com.webtalk.storage.queue
  (:gen-class)
  (:require [langohr.core                       :as rmq]
            [langohr.channel                    :as lch]
            [langohr.queue                      :as lq]
            [com.webtalk.storage.queue.consumer :as consumer]
            [com.webtalk.storage.queue.config   :as config]
            [langohr.basic                      :as lb]))

(def ^{:const true}
  default-exchange-name "")

(defn subscribe-with-connection [qname message-handler]
  (let [connection (rmq/connect config/rmq-config)
        channel    (lch/open connection)]
     (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
     (lq/declare channel qname {:durable true :auto-delete true :exclusive false})
     (consumer/subscribe channel qname message-handler {:auto-ack true})
     [connection channel]))

(defn shutdown [[conn ch]]
  (rmq/close ch)
  (rmq/close conn))
