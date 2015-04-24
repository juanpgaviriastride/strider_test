(ns com.webtalk.storage.graph.invitation
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

(defn invitation-hash [payload]
  (into {} (list payload
                 {:VertexType "invitedUser"})))

(defn request-hash [payload]
  (into {} (list payload
                 {:VertexType "requestedInvitation"})))

(defn request-an-invite!
  "It creates the user invitation node if needed but without adding links (that's the
   diffence with create-invitation! - need to update  create-invitation to check the special case
   of requestedInvitation-)

   Example: (request-an-invite! graph {\"email\" \"some@wt.com\" \"phone\" \"3138670909\" \"enable_sms\" \"true\"})
   email: is the email of the invited user
   phone: is the phone of the invited user
   enable_sms: is the bool value that let you know if the user wants to be pinged by sms"
  
  [graph payload]
  (let [email (payload "email")
        invitation (first (tvertex/find-by-kv graph :email email))
        invitation-hash (request-hash {:email email
                                       :phone  (str (get payload "phone" ""))
                                       :enable-sms (= "true" (payload "enable_sms"))})]

    (tgraph/with-transaction [g graph]
      (if (nil? invitation)
        {:vertex (tvertex/to-map (tvertex/create! g invitation-hash))
         :status :new_record}
        (if (= (tvertex/get invitation :VertexType) "requestedInvitation")
          {:vertex (tvertex/to-map (tvertex/merge! invitation invitation-hash))
           :status :updated_record}
          
          {:vertex (tvertex/to-map invitation)
           :status :mixmatch_type_record})))))


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
