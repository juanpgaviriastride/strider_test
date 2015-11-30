(ns com.webtalk.pre-launch.computation
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]
            [clojurewerkz.ogre.core :as oq]))

(defn deep [q n]
  (if (= n 1)
    q
    (recur (oq/<-- q ["refered_by"]) (dec n))))

(defn get-level [root-node lvl edge-tag]
  (oq/query root-node
            (deep lvl)
            (oq/<-- [edge-tag])
            oq/count!))
