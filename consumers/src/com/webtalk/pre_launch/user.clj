(ns com.webtalk.pre-launch.user
  (:gen-class)
  (:require [clojurewerkz.titanium.graph :as tgraph]
            [clojurewerkz.titanium.vertices :as tvertex]
            [clojurewerkz.titanium.edges :as tedge]))

(defn user-hash [payload]
  (into {}  (list payload
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
  (println "create-user! graph" graph "payload" payload)
  (let [{email "email" referer-id "refererID"} payload
        referer (first (tvertex/find-by-id (Integer. (or referer-id 0))))
        new-user (tvertex/create! graph (user-hash payload))]
    (println "new-user" new-user)
    (println "referer" referer)
    (when referer
      (tedge/upconnect! graph new-user "refered_by" referer))
    (tvertex/to-map new-user)))
