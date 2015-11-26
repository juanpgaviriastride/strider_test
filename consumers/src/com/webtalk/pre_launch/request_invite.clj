(ns com.webtalk.pre-launch.request-invite
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]
            [com.webtalk.mailer.invite      :as deliver-invite]))

(defn request-hash [payload]
  (into {} (list payload
                 {:VertexType "prelaunchRequestedInvitation"})))

(defn request-an-invite!
  "It creates the user invitation node if needed adding links

   Example: (request-an-invite! graph {\"email\" \"some@wt.com\"
                                       \"refererID\" 234})
   email: is the email of the invited user
   refererID is the titan id of the user who is referring the new user and it is optional"

  [graph payload]
  (let [{email "email" referer-id "refererID"} payload
        invitation (first (tvertex/find-by-kv graph :email email))
        referer (first (tvertex/find-by-id (Integer. referer-id)))
        invitation-hash (request-hash {:email email})]
    (if (nil? invitation)
      (let [new-invitation (tvertex/create! graph invitation-hash)]
        (tedge/upconnect! graph new-invitation "refered_by" referer)
        {:vertex (tvertex/to-map new-invitation)
         :status :new_record})
      (do
        (tedge/upconnect! graph invitation "refered_by" referer)        
        (if (= (tvertex/get invitation :VertexType) "prelaunchRequestedInvitation")
          {:vertex (tvertex/to-map (tvertex/merge! invitation invitation-hash))
          :status :updated_record}
         {:vertex (tvertex/to-map invitation)
          :status :mixmatch_type_record})))))
