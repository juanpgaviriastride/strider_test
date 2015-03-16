(ns com.webtalk.storage
  (:gen-class)
  (:require [com.webtalk.storage.graph       :as graph]
            [com.webtalk.storage.persistence :as persistence]
            [com.webtalk.storage.queue       :as queue]))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

;;; Important to close
;;; from queue
;;; (rmq/close channel)
;;; (rmq/close connection)
;;; from persistence
;;; (cclient/disconnect connection)
;;; from graph
;;; (tgraph/shutdown graph)