(ns com.webtalk.storage.graph.config
  (:gen-class)
  (:require [clojurewerkz.titanium.graph  :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]))

(def graph-config
  {
   "storage.backend" "cassandra"
   "storage.hostname" "192.168.50.4"
   ;; "storage.keyspace" "titan"
   ;; "graph-name" "graph"

   ;; "storage.index.search.backend" "elasticsearch"
   ;; "storage.index.search.hostname" "192.168.50.4"
   ;; "storage.index.search.client-only" "true"
   ;; "storage.index.search.cluster-name" "webtalk"
   ;; "storage.index.search.local-mode" "false"
   ;; "storage.index.search.sniff" "false"
   ;; "storage.backend"  "cassandra"
   ;; "storage.hostname" "192.168.50.4"
   ;; "storage.keyspace" "titan"
   ;; "graph" "graph"
   })

(defn -main
  [& args]
  (println "Connecting" (str graph-config))
  (let [graph (tgraph/open graph-config)]
    (tgraph/with-transaction [g graph]
      (let [ghost1 (tvertex/create! g {:VertexType "ghost" :name "ghost 1"})
            ghost2 (tvertex/create! g {:VertexType "ghost" :name "ghost 2"})]
        (println ghost1 ghost2)
        (println (map tvertex/to-map (tvertex/get-all-vertices g)))))
    (println "Shutting down" (str graph))
    (tgraph/shutdown graph)))
