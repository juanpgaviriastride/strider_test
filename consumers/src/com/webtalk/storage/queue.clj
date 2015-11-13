(ns com.webtalk.storage.queue
  (:gen-class)
  (:require [langohr.core                       :as rmq]
            [langohr.channel                    :as lch]
            [langohr.queue                      :as lq]
            [com.webtalk.storage.queue.consumer :as consumer]
            [com.webtalk.storage.queue.config   :as config]
            [langohr.basic                      :as lb]
            [com.stuartsierra.component         :as component]))

(def ^{:const true}
  default-exchange-name "")

(defn subscribe-with-connection [connection qname message-handler]
  (println "Getting ready to setup" qname)
  (let [channel    (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (lq/declare channel qname {:durable true :auto-delete false :exclusive false})
    (println "creating consumer" message-handler)
    (consumer/subscribe channel qname message-handler {:auto-ack true})
    (println "subscribed" message-handler)
    [channel]))

(defn shutdown [ch]
  (rmq/close ch))
