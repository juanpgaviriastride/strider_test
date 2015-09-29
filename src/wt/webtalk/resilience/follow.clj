(ns com.webtalk.resilience.follow
  (:gen-class)
  (:require [com.netflix.hystrix.core                  :as hystrix]
            [com.webtalk.storage.graph.follow          :as graph-follow]
            [com.webtalk.storage.persistence.following :as persistence-following]))


;; (hystrix/defcommand gfollow
;;   {:hystrix/fallback-fn (fn [connection payload]
;;                           (constantly nil))}
;;   [connection payload]
;;   (graph-follow/follow! connection payload))

(defn gfollow [connection payload]
  (graph-follow/follow! connection payload))

;; (hystrix/defcommand pfollow
;;   {:hystrix/fallback-fn (fn [connection user-id followed-id]
;;                           (constantly nil))}
;;   [connection user-id followed-id]
;;   (persistence-following/create-following connection user-id followed-id))

(defn pfollow [connection user-id followed-id]
  (persistence-following/create-following connection user-id followed-id))
