(ns com.webtalk.storage.graph.config
  (:gen-class)
  (:require [clojurewerkz.titanium.graph  :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]))

(def graph-config
  {
   "storage.backend"  "cassandra"
   "storage.hostname" "192.168.50.4"
   "storage.keyspace" "titan"
   "graph" "graph"
   })

(defn -main
  [& args]
  (println "Connecting" (str graph-config))
  (let [g (tgraph/open graph-config)]
    (tgraph/new-transaction
      (let [ghost1 (tvertex/create! g {:VertexType "ghost" :name "ghost 1"})
            ghost2 (tvertex/create! g {:VertexType "ghost" :name "ghost 2"})]
        (println (map tvertex/to-map (tvertex/get-all-vertices g)))
        (tgraph/commit g)))
    (tgraph/shutdown g)))
