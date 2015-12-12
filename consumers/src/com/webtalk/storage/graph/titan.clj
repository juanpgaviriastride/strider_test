(ns com.webtalk.storage.graph.titan
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.titanium.graph :as t]
            [taoensso.timbre :refer [info]]))

(defrecord Titan [backend hosts name connection]
  component/Lifecycle

  (start [component]
    (info "Starting titan")
    (if (= nil connection)
      (let [config {"storage.backend" backend
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
                    }
            conn (t/open config)]
        (assoc component :connection conn))
      component))

  (stop [component]
    (info "Stopping titan")
    (when connection
      (t/shutdown connection))
    (assoc component :connection nil)))

(defn new-titan [hosts]
  (map->Titan {:hosts hosts :backend "cassandra" :graph "graph"}))
