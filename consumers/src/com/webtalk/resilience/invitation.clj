(ns com.webtalk.resilience.invitation
  (:gen-class)
  (:require [com.netflix.hystrix.core                   :as hystrix]
            [com.webtalk.storage.graph.invitation       :as graph-invitation]
            [com.webtalk.storage.persistence.invitation :as persistence-invitation]))


;; (hystrix/defcommand gcreate-invitation
;;   {:hystrix/fallback-fn (fn [connection payload]
;;                           (constantly nil))}
;;   [connection payload]
;;   (graph-invitation/create-invitation! connection payload))

(defn gcreate-invitation [connection payload]
  (graph-invitation/create-invitation! connection payload))

;; (hystrix/defcommand pcreate-invitation
;;   {:hystrix/fallback-fn (fn [connection id payload]
;;                           (constantly nil))}
;;   [connection id payload]
;;   (persistence-invitation/create-invitation connection id payload))

(defn pcreate-invitation [connection id payload]
  (persistence-invitation/create-invitation connection id payload))


;; (hystrix/defcommand gcreate-invitation
;;   {:hystrix/fallback-fn (fn [connection payload]
;;                           (constantly nil))}
;;   [connection payload]
;;   (graph-invitation/request-an-invite! connection payload))
(defn grequest-an-invite
  [connection payload]
  (graph-invitation/request-an-invite! connection payload))

;; (hystrix/defcommand prequest-invitation
;;   {:hystrix/fallback-fn (fn [connection id payload]
;;                           (constantly nil))}
;;   [connection id payload]
;;   (persistence-invitation/request-invitation connection id payload))

(defn prequest-invitation [connection id payload]
  (persistence-invitation/request-invitation connection id payload))
