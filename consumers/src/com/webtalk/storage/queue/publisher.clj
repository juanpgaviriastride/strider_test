(ns com.webtalk.storage.queue.publisher
  (:gen-class)
  (:require [langohr.core                     :as rmq]
            [langohr.channel                  :as lch]
            [langohr.queue                    :as lq]
            [com.webtalk.storage.queue.config :as config]
            [langohr.basic                    :as lb]
            [clojure.data.json                :as json]
            [com.stuartsierra.component       :as component]
            [taoensso.timbre :refer [debug spy]]))

(def ^{:const true}
  default-exchange-name "")

(defn publish-with-qname [connection qname payload]
  (let [channel    (lch/open connection)]
    (debug (format "[main] Connected. Channel id: %d" (.getChannelNumber channel)))
    (spy qname)
    (debug "about to publish")
    (lb/publish channel default-exchange-name qname (json/write-str payload) {:content-type "text/json"})
    (lch/close channel)))
