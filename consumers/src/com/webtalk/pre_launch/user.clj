(ns com.webtalk.pre-launch.user
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]
            [taoensso.timbre :require [spy debug]]))

(defn user-hash [payload]
  (into {}  (list (remove (fn [[a b]] (nil? b)) payload)
                  {:VertexType "prelaunchUser"
                   :TeamTrainer false
                   :Commission 0})))

(defn create-user!
  "Given a graph and user payload, creates a vertex within titandb,
   with the provide user properties, and creating the proper network connections.
   refererID is the titan id of the user who is referring the new user and it is optional
  Example: (create-user! graph {\"name\" \"Sebastian\"
                                \"email\" \"sebastian@email.com\"
                                \"refererID\" 234})"

  [graph payload]
  (debug "create-user! graph" graph "payload" payload)
  (tgraph/with-transaction [g graph]
    (let [{email "email" referer-id "refererID"} payload
          referer (when referer-id
                    (tvertex/find-by-id g (Integer. referer-id)))
          [user status] (if-let [maybe-user (first (tvertex/find-by-kv g :email email))]
                          [maybe-user :old]
                          [(tvertex/create! g (user-hash payload)) :new])]
      (when (and (= status :new) referer)
        (tedge/upconnect! g user "refered_by" referer {:time (System/currentTimeMillis)}))
      (spy user)
      (spy referer)
      (tvertex/to-map user))))
