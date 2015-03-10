(ns com.webtalk.storage.graph.follow
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

(defn follow!
  "It adds an edge between the user_id vertex and
   followed_id vetex.

  Example: (follow! graph {\"user_id\" ID \"followed_id\" ID})"
  [graph payload]
  (let [user     (tvertex/find-by-id graph (payload "user_id"))
        followed (tvertex/find-by-id graph (payload "followed_id"))]
    (tgraph/with-transaction [g graph]
      (tedge/upconnect! g user "follow" followed))))