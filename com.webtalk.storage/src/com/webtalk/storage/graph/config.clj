(ns com.webtalk.storage.graph.config
  (:gen-class))

(def graph-config
  {
   "storage.backend" "cassandra"
   "storage.hostname" "192.168.50.4"
   ;; "graph-name" "graph"

   ;; "index.search.backend" "elasticsearch"
   ;; "index.search.hostname" "192.168.50.4"
   ;; "index.search.client-only" "true"
   ;; "index.search.cluster-name" "webtalk"
   ;; "index.search.local-mode" "false"
   ;; "index.search.sniff" "false"
   ;; "storage.keyspace" "titan"
   ;; "graph" "graph"
   })

;;; For testing
;; (defn -main
;;   [& args]
;;   (println "Connecting" (str graph-config))
;;   (let [graph (tgraph/open graph-config)]
;;     (tgraph/with-transaction [g graph] :rollback? true
;;       (let [ghost1 (tvertex/create! g {:VertexType "ghost" :name "ghost 1"})
;;             ghost2 (tvertex/create! g {:VertexType "ghost" :name "ghost 2"})]
;;         (println ghost1 ghost2)
;;         (println (map tvertex/to-map (tvertex/get-all-vertices g)))))
;;     (println "Shutting down" (str graph))
;;     (tgraph/shutdown graph)))
