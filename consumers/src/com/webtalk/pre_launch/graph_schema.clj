(ns com.webtalk.pre-launch.graph-schema
  (:require [clojurewerkz.titanium.schema :as schema]
            [clojurewerkz.titanium.graph :as tgraph]
            [taoensso.timbre :as timbre]))

(defn get-or-create-propery-key [mgmt key data-type]
  "Try to create the key or find if we can create it"
  (try
    (schema/make-property-key mgmt key data-type)
    (catch IllegalArgumentException e
          (schema/get-property-key mgmt key))))

;; (schema/build-composite-index mgmt index-name type keys)

;; needed composite
;; vertex
;; index by email (vertex)
;; index by name (vertex)

;; Fixing eventual consistency http://s3.thinkaurelius.com/docs/titan/0.5.0/eventual-consistency.html
;; mgmt.setConsistency(name,ConsistencyModifier.LOCK) //Ensures only one name per vertex
;; mgmt.setConsistency(index,ConsistencyModifier.LOCK) //Ensures name uniqueness in the graph

(defn build-composite-index [mgmt index-name key]
  "this index only uniq keys"
  (when-not (.getGraphIndex mgmt index-name)
    (-> mgmt
       (.buildIndex index-name com.tinkerpop.blueprints.Vertex)
       (.addKey key)
       (.unique)
       (.buildCompositeIndex))))

(defn build-mixed-index [mgmt index-name key]
  "this for elasticsearch indexes"
  (when-not (.getGraphIndex mgmt index-name)
    (-> mgmt
       (.buildIndex index-name com.tinkerpop.blueprints.Vertex)
       (.addKey key)
       (.buildMixedIndex "search"))))

(defn create-titan-indexes [graph]
  "create the needed indexes for wt prelaunch"
  (schema/with-management-system [mgmt graph]
    (let [email-key (get-or-create-propery-key mgmt "email" String)
          name-key (get-or-create-propery-key mgmt "name"  String)
          time-key (get-or-create-propery-key mgmt "time" Integer)]
      (build-composite-index mgmt "byEmail" email-key)
      ;;(build-mixed-index mgmt "byName" name-key)
      )))
;; needed mixed
;; timestamp  (edge)
;; name  (vertex)


(defn cleanup [graph]
  "cleanup the given graph"
  (tgraph/shutdown graph)
  (com.thinkaurelius.titan.core.util.TitanCleanup/clear graph))
