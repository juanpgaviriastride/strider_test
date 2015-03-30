(ns com.webtalk.resilience.entry
  (:gen-class)
  (:require [com.netflix.hystrix.core              :as hystrix]
            [com.webtalk.storage.graph.entry       :as graph-entry]
            [com.webtalk.storage.persistence.entry :as persistence-entry]))


(hystrix/defcommand gcreate-entry
  {:hystrix/fallback-fn (fn [connection payload]
                          (constantly nil))}
  [connection payload]
  (graph-entry/create-entry! connection payload))

(hystrix/defcommand pcreate-entry
  {:hystrix/fallback-fn (fn [connection id user-id payload]
                          (constantly nil))}
  [connection id user-id payload]
  (persistence-entry/create-entry connection id user-id payload))
