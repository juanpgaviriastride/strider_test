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

(defn detailed-lvl-1 [root]
  (let [get-info (fn [edge-label status] (pmap
                                         (fn [edge] {:email (tvertex/get (tedge/tail-vertex edge) :email)
                                                    :time (tedge/get edge :time)
                                                    :status status})
                                         (oq/query root
                                                   (oq/<E- [edge-label])
                                                   oq/into-set!)))
        invites-nodes (get-info "invited_by" "Sent Invitation")
        wait-nodes (get-info "invited_waitlist_by" "Joined Waitlist")
        user-nodes (get-info "refered_by" "Joined Pre-Launch")]
    (flatten [invites-nodes wait-nodes user-nodes])))
