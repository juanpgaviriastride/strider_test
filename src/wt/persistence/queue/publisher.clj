(ns wt.persistence.queue.publisher
  (:gen-class)
  (:require [langohr.core                     :as rmq]
            [langohr.channel                  :as lch]
            [langohr.queue                    :as lq]
            [wt.persistence.queue.config :as config]
            [langohr.basic                    :as lb]
            [clojure.data.json                :as json]))

(def ^{:const true}
  default-exchange-name "")

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
