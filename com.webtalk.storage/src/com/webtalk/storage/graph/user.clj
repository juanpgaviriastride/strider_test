(ns com.webtalk.storage.graph.user
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

(defn user-hash [payload]
  (into {}  (list payload
                  {:VertexType "user"
                   :TeamTrainer false
                   :Commission 0})))

(defn create-new-user
  "Create a new user without linking its invitation network.

   Example: (create-user graph {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph payload]
  (tgraph/with-transaction [g graph]
    (tvertex/create! g (user-hash payload))))


(defn follow-and-followback
  "Creates a double follow edge between the first and second user

   Example: (follow-and-followback graph user1 user2)"

  [graph user1 user2]
  (tgraph/with-transaction [g graph]
    (println "connecting" user1 user2)
    (tedge/upconnect! g user1 "follow" user2)
    (tedge/upconnect! g user2 "follow" user1)))

(defn set-refered-by
  "Look from the invited edges who has the biggest time and picks it
   as the parent. WT referral/affiliate policy of assigning the resource
   to the most recent

   Example: (set-referer-by graph user-vertex)"

  [graph user]
  ;; TODO: Would be ideal to have an index by time to sort the edges
  (tgraph/with-transaction [g graph]
    (let [recent-invite (last (sort-by :time (map tedge/to-map (tvertex/incoming-edges-of user "invited"))))
          refer-edge    (tedge/find-by-id graph (:__id__ recent-invite))
          referrer      (tedge/tail-vertex refer-edge)
          child         (tedge/head-vertex refer-edge)]
      (tedge/upconnect! g child "refered_by" referrer))))

(defn setup-invited-user
  "Creates user network of follows from invites and setup refered_by.

   Example: (setup-invited-user graph user-vertex {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph user payload]
  (tgraph/with-transaction [g graph]
    (let [network-users (tvertex/connected-in-vertices user "invited")]
      (if-not (empty? network-users)
        ;; pick the referer_by link
        (set-refered-by g user))

      ;; follow and follow back
      ;; Force this lazy sequence may thread this one
      (doall (map #(follow-and-followback g user %) network-users))
      ;; complete user info like name and that stuff
      ;; set vertex type as 'user instead of invited user
      (tvertex/merge! user (user-hash payload)))))

(defn create-user!
  "Given a graph and user payload, creates a vertex within titandb,
   with the provide user properties, and creating the proper network connections.

  Example: (create-user! graph {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph payload]
  (println "create-user! graph" graph "payload" payload)
  (let [email (payload "email")
        invited-user (first (tvertex/find-by-kv graph :email email))]
    (println "email" email)
    (println "invited-user" invited-user)
    (if (nil? invited-user)
      (do
        (println "create-new-user")
        (create-new-user graph payload))
      ;; else user was invited
      (if (= (tvertex/get invited-user :VertexType) "invitedUser")
        (do
          (println "setting up invited user")
          (setup-invited-user graph invited-user payload))
        (println "do nothing for already created users")))
    (println (tvertex/to-map (first (tvertex/find-by-kv graph :email email))))
    (first (tvertex/find-by-kv graph :email email))))
