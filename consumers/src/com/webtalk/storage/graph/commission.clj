(ns com.webtalk.storage.graph.commission
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

;;; This commission system need to be replaced by ogre using side-effects and pipes
(defn add-commission! [user value]
  (tvertex/merge! user {
                        :Commission (+ value
                                       (tvertex/get user :Commission))})) ;; this should append values to avoid race conditions
                                     ;; we need also a second method to reduce the values on the payment-cut-off times

(defn commissions-tree-upgrade!
  [user steps value]
  (let [next-user (first (tvertex/connected-out-vertices user "refered_by"))]
    (cond
      (nil? next-user) nil
      (zero? steps) nil
      (true? (tvertex/get user :TeamTrainer)) (do
                                                (add-commission! user value)
                                                (commissions-tree-upgrade! next (dec steps) value))
      :else (commissions-tree-upgrade! next steps value))))
