(ns com.webtalk.resilience.user
  (:gen-class)
  (:require [com.webtalk.storage.graph.user       :as graph-user]
            [com.webtalk.storage.persistence.user :as persistence-user]
            [taoensso.timbre :refer [debug spy]]))


;; (hystrix/defcommand gcreate-user
;;   {:hystrix/fallback-fn (fn [connection payload]
;;                           (constantly nil))}
;;   [connection payload]
;;   (debug "gcreate-user")
;;   (spy connection)
;;   (spy payload)
;;   (graph-user/create-user! connection payload))

(defn gcreate-user [connection payload]
  (debug "gcreate-user")
  (spy connection)
  (spy payload)
  (graph-user/create-user! connection payload))

;; (hystrix/defcommand pcreate-user
;;   {:hystrix/fallback-fn (fn [connection id payload]
;;                           (constantly nil))}
;;   [connection id payload]
;;   (persistence-user/create-user connection id payload))

(defn pcreate-user [connection id payload]
  (persistence-user/create-user connection id payload))
