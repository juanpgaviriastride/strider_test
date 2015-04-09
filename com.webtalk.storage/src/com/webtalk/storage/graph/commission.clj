(ns com.webtalk.storage.graph.commission
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

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
      (= steps 0) nil
      (true? (tvertex/get user :TeamTrainer)) (do
                                                (add-commission! user value)
                                                (bubble-up-commissions next (dec steps) value))
      :else (bubble-up-commissions next steps value))))
