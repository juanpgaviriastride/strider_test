(ns com.webtalk.pre-launch.request-invite
  (:gen-class)
  (:require [clojurewerkz.titanium.graph    :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges    :as tedge]))

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
        invitation-hash (request-hash {:email email})
        [req-invitation status] (if-let [maybe-invitation (first (tvertex/find-by-kv graph :email email))]
                                  [maybe-invitation :old]
                                  [(tvertex/create! graph invitation-hash) :new])
        referer (first (tvertex/find-by-id (Integer. (or referer-id 0))))]

    (tedge/upconnect! graph req-invitation "invited_waitlist_by" referer)
    (if (= status :new)
      {:vertex (tvertex/to-map req-invitation)
       :status :new_record}
      {:vertex (tvertex/to-map req-invitation)
       :status :mixmatch_type_record})))
