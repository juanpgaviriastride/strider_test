(ns com.webtalk.pre-launch.computation
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

(defn get-level [root-node edge-tag]
  (tvertex/incoming-edges-of root-node edge-tag))

(defn get-level-list [sibling-list]
  {:refered-by-me (map #(get-level % "refered_by") sibling-list)
   :invited-by-me (map #(get-level % "invited_by") sibling-list)
   :waiting-because-of-me (map #(get-level % "invited_waitlist_by") sibling-list)})

(defn dashboard [root]
  (let [first-lvl (get-level-list [root])
        second-lvl (get-level-list (:refered-by-me first-lvl))
        third-lvl (get-level-list (:refered-by-me second-lvl))
        fourth-lvl (get-level-list (:refered-by-me third-lvl))
        fifth-lvl (get-level-list (:refered-by-me fourth-lvl))]))
