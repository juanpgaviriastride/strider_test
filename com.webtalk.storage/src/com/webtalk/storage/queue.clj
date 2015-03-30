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

;;; For testing

(defn test-handler [json]
  (println (json "user_id")))

(defn -main
  [& args]
  (let [qname "com.webtalk.storage.queue.manager-test"
        [connection channel] (subscribe-with-connection qname test-handler)]
    
    (lb/publish channel default-exchange-name qname "{\"user_id\":25,\"email\":\"sebastian@wt.com\"}" {:content-type "text/json" :type "create invitation"})
    (Thread/sleep 2000)
    (println "[main] Disconnecting...")
    (rmq/close channel)
    (rmq/close connection)))
