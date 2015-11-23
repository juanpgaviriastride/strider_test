(ns clj-wt.queue
  (:require [langohr.consumers :as lconsumer]
            [clojure.data.json :as json]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [clj-wt.config :as config]
            [langohr.basic :as lb]))

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

(defn promise-subscription [qname message-handler]
  (let [result (promise)]
    (subscribe-with-connection qname
                               (fn [payload]
                                 (deliver result (message-handler payload))))
    result))

(defn publish-with-qname [qname reply-queue payload]
  (let [connection (rmq/connect {:host (config/rmq-config)})
        channel    (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (println "qname" qname)
    (flush)
    ;;(lq/declare channel qname {:durable false :auto-delete true :exclusive true})
    (println "about to publish")
    (flush)
    (lb/publish channel default-exchange-name qname (json/write-str payload) {:content-type "text/json" :reply-to reply-queue})
    [connection channel]))
