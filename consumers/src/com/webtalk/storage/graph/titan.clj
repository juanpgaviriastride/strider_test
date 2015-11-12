(ns com.webtalk.storage.graph.titan
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.titanium.graph :as t]))

(defrecord Titan [backend hosts name connection]
  component/Lifecycle

  (start [component]
    (println "Starting titan")
    (if (= nil connection)
      (let [conn (t/open {"storage.backend" backend
                          "storage.hostname" hosts
                          ;; "graph-name" "graph"

                          ;; "index.search.backend" "elasticsearch"
                          ;; "index.search.hostname" "192.168.50.4"
                          ;; "index.search.client-only" "true"
                          ;; "index.search.cluster-name" "webtalk"
                          ;; "index.search.local-mode" "false"
                          ;; "index.search.sniff" "false"
                          ;; "storage.keyspace" "titan"
                          ;; "graph" "graph"
                          })]
        (assoc component :connection conn))
      component))

  (stop [component]
    (println "Stopping titan")
    (when connection
      (t/shutdown connection))
    (assoc component :connection nil)))

(defn new-titan [hosts]
  (map->Titan {:hosts hosts :backend "cassandra" :graph "graph"}))
