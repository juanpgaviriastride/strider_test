(ns com.webtalk.storage.graph.invitation
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]
            [com.webtalk.mailer.invite      :as deliver-invite]
            [crypto.random :refer [url-part]]))

(defn invitation-hash [payload]
  (into {} (list payload
                 {:VertexType "invitedUser"})))

(defn request-hash [payload]
  (into {} (list payload
                 {:VertexType "requestedInvitation"})))

(defn request-an-invite!
  "It creates the user invitation node if needed but without adding links (that's the
   difference with create-invitation! - need to update  create-invitation to check the special case
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


(defmacro connect-invitation [invited-user]
  "This macro connects or updates the edge between the wt user and the invited user
   adding the updating the proper timestamp (time) and the invitation token (invitationToken)

   Example: (connect-invitation invited-user)
   invited-user: is a graph node/vertex that holds an invitedUser
   It is really important to note that this macro depends on the following context:
   - a g var that represents a graph instance of com.thinkaurelius.titan.graphdb.database.StandardTitanGraph
   - a user var that is an instance of tvertex and is the user that is sending the invitation"
  `(let [-*token# (url-part 24)]
     (tedge/upconnect! ~'g ~'user "invited" ~invited-user {:time (System/currentTimeMillis)
                                                           :invitationToken (url-part 24)})
     (deliver-invite/deliver-email (tvertex/to-map ~'user) -*token# ~'email)))

(defn create-invitation!
  "It creates the user invitation node if needed and links the user who invited with the invitation node

   Example: (create-invitation! graph {\"user_id\" ID \"email\" \"some@wt.com\"})
   user_id: is the user id that is sending the invitation
   email: is the email of the invited user"

  [graph payload]
  (let [email (payload "email")
        invitation (first (tvertex/find-by-kv graph :email email))
        user (tvertex/find-by-id graph (payload "user_id"))
        properties-hash (invitation-hash {:email email})]
    (tgraph/with-transaction [g graph]
      (if (nil? invitation)
        (let [new-invitation (tvertex/create! g properties-hash)]
          (connect-invitation new-invitation))
        ;; else user (invited or not) was already created
        (let [vertex-type (tvertex/get invitation :VertexType)]
          (when (not= vertex-type "user")
            (connect-invitation invitation)
            (if (= vertex-type "requestedInvitation")
              ;; this should overwrite the type from requestedInvitation to invitedUser
              (tvertex/merge! invitation properties-hash))))))
    (first (tvertex/find-by-kv graph :email email))))
