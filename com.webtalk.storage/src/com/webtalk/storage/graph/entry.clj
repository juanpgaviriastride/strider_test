(ns com.webtalk.storage.graph.entry
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

(defn entry-hash [payload]
  (into {} (list payload
                 {:VertexType "entry"})))

(defn create-entry!
  "Creates an entry vertex and links as follow:
   owner -> posted -> entry
   
   Example: (create-entry! graph {\"user_id\" ID \"content\" \"Hi I'm here and I'm happy\" \"title\" \"Hello WT\"})"

  [graph payload]
  (let [owner (tvertex/find-by-id graph (payload "user_id"))]
    (tgraph/with-transaction [g graph]
      (let [entry (tvertex/create! g (entry-hash payload))]
        (tedge/upconnect! g owner "posted" entry {:time (System/currentTimeMillis)})))))