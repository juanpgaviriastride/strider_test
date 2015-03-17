(ns com.webtalk.storage.graph.invitation
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

(defn invitation-hash [payload]
  (into {} (list payload
                 {:VertexType "invitedUser"})))


(defn create-invitation!
  "It creates the user invitation node if needed and links the user who invited with the invitation node

   Example: (create-invitation! graph {\"user_id\" ID \"email\" \"some@wt.com\"})
   user_id: is the user id that is sending the invitation
   email: is the email of the invited user"

  [graph payload]
  (let [email (payload "email")
        invitation (first (tvertex/find-by-kv graph :email email))
        user (tvertex/find-by-id graph (payload "user_id"))]
    (tgraph/with-transaction [g graph]
      (let [connect-invitation (partial tedge/upconnect! g user "invited")]
        (if (nil? invitation)
         (let [new-invitation (tvertex/create! g (invitation-hash {:email email}))]
           (connect-invitation new-invitation {:time (System/currentTimeMillis)}))
         ;; else user (invited or not) was already created
         (if (= (tvertex/get invitation :VertexType) "invitedUser")
           (connect-invitation invitation {:time (System/currentTimeMillis)})))))
    (first (tvertex/find-by-kv graph :email email))))