(ns com.webtalk.pre-launch.invite
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

(defn invitation-hash [payload]
  (into {} (list (remove (fn [[a b]] (nil? b)) payload)
                 {:VertexType "prelaunchInvitation"})))

(defn invite!
  "It creates the user invitation node if needed adding links

   Example: (invite! graph {\"email:\" \"some@wt.com\"
                                       \"refererID\" 234})
   email: is the email of the invited user
   refererID is the titan id of the user who is referring the new user and it is optional"

  [graph payload]
  (tgraph/with-transaction [g graph]
    (let [{email "email" referer-id "refererID"} payload
          invitation-hash (invitation-hash {:email email})
          [invitation status] (if-let [maybe-invitation (first (tvertex/find-by-kv g :email email))]
                                [maybe-invitation :old]
                                [(tvertex/create! g invitation-hash) :new])
          referer (when referer-id
                    (tvertex/find-by-id g (Integer. referer-id)))]

      (when referer
        (tedge/upconnect! g invitation "invited_by" referer))
      (if (= status :new)
        {:vertex (tvertex/to-map invitation)
         :status :new_record
         :sender (when referer
                   (tvertex/to-map referer))}
        (if (= (tvertex/get invitation :VertexType) "prelaunchInvitation")
          {:vertex (tvertex/to-map (tvertex/merge! invitation invitation-hash))
           :status :updated_record
           :sender (when referer
                   (tvertex/to-map referer))}
          {:vertex (tvertex/to-map invitation)
           :status :mixmatch_type_record
           :sender (when referer
                   (tvertex/to-map referer))})))))
