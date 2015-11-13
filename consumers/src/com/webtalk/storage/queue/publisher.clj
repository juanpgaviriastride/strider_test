(ns com.webtalk.storage.queue.publisher
  (:gen-class)
  (:require [langohr.core                     :as rmq]
            [langohr.channel                  :as lch]
            [langohr.queue                    :as lq]
            [com.webtalk.storage.queue.config :as config]
            [langohr.basic                    :as lb]
            [clojure.data.json                :as json]
            [com.stuartsierra.component       :as component]))

(def ^{:const true}
  default-exchange-name "")

(defn publish-with-qname [q qname payload]
  (let [connection (:connection (component/start q))
        channel    (lch/open connection)]
    (println (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (println "qname" qname)
    (flush)
    ;;(lq/declare channel qname {:durable false :auto-delete true :exclusive true})
    (println "about to publish")
    (flush)
    (lb/publish channel default-exchange-name qname (json/write-str payload) {:content-type "text/json"})
    channel))
