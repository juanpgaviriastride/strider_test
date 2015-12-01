(ns com.webtalk.system
  (:require [com.stuartsierra.component :as component]
            [com.webtalk.storage.queue.rabbit :refer [new-rabbit]]
            [com.webtalk.storage.persistence.cassandra :refer [new-cassandra]]
            [com.webtalk.storage.graph.titan :refer [new-titan]]))

(defn new-system [{:keys [cassandra rabbit titan]}]
  (component/system-map
   :cassandra (new-cassandra (:hosts cassandra) (:keyspace cassandra))
   :rabbit    (new-rabbit    (:host rabbit) (:username rabbit) (:password rabbit))
   :titan     (new-titan     (:hosts titan))))
