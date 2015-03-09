(ns com.webtalk.storage.graph.user
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

(defn -create-new-user!
  "Create a new user without linking its invitation network.

  Example: (create-user! graph {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph payload]
  (tgraph/with-transaction [g graph]
    (tvertex/create! g (into {}
                             (list payload
                                   {:VertexType "user"})))))

(defn -setup-user-network!
  "Creates user network of follows from invites and setup reffered_by.

   Example: (setup-user-network! graph {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph user payload]
  (let [network-users (tvertex/incoming-edges-of user "invited")]
    (if-not (empty? network-users)
      ;; follow and follow back
      ;; pick the referer_by link
      ;; else
      ;; nothing
      (println 'doin)
      )
    ;; complete user info like email and that stuff
    ;; set vertex type as 'user instead of invited user
    ))

(defn create-user!
  "Given a graph and user payload, creates a vertex within titandb,
   with the provide user properties, and creating the proper network connections.

  Example: (create-user! graph {\"name\" \"Sebastian\" \"email\" \"sebastian@email.com\"})"

  [graph payload]
  (let [invited-user (tvertex/find-by-kv graph :email (payload "email"))]
    (if (empty? invited-user)
      (-create-new-user! graph payload)
      ;; else user was invited
      (-setup-user-network! graph invited-user payload))))


;;; For testing

