(ns com.webtalk.storage.graph.config
  (:gen-class)
  (:require [com.webtalk.util :refer [get-cass-hosts]]))

(def graph-config
  {
   "storage.backend" "cassandra"
   "storage.hostname" (get-cass-hosts)
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

