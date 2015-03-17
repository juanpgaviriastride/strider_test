(ns com.webtalk.storage.graph
  (:gen-class)
  (:require [clojurewerkz.titanium.graph      :as tgraph]
            [com.webtalk.storage.graph.config :as config]))

(defn connection-session []
  (tgraph/open config/graph-config))