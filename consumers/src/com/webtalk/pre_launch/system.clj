(ns com.webtalk.pre-launch.system
  (:require [com.stuartsierra.component :as component]
            [com.webtalk.storage.queue.rabbit :refer [new-rabbit]]
            [com.webtalk.storage.graph.titan :refer [new-titan]]))

(defn new-system [{:keys [rabbit titan]}]
  (component/system-map
   :rabbit    (new-rabbit    (:host rabbit) (:username rabbit) (:password rabbit))
   :titan     (new-titan     (:hosts titan))))
