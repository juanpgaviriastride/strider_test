(ns com.webtalk.pre-launch.graph-schema
  (:require [clojurewerkz.titanium.schema :as schema]
            [clojurewerkz.titanium.graph :as tgraph]
            [taoensso.timbre :as timbre]
            [com.webtalk.util :as util]
            [com.webtalk.pre-launch.core :as core])
  (:gen-class)
  (:import [java.util Properties Map ArrayList]
           java.io.File
           com.tinkerpop.blueprints.Vertex
           [com.thinkaurelius.titan.core.schema SchemaStatus SchemaAction]
           com.thinkaurelius.titan.core.util.TitanCleanup
           com.thinkaurelius.titan.hadoop.TitanIndexRepair
           com.thinkaurelius.titan.hadoop.formats.util.TitanInputFormat))

(defn ^Properties map->properties
  [^Map m]
  (let [p (Properties.)]
    (doseq [[k ^String v] m]
      (.setProperty p (name k) v))
    p))

(defn get-or-create-property-key [mgmt key data-type]
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
       (.buildIndex index-name Vertex)
       (.addKey key)
       (.unique)
       (.buildCompositeIndex))))

(defn build-mixed-index [mgmt index-name key]
  "this for elasticsearch indexes"
  (when-not (.getGraphIndex mgmt index-name)
    (-> mgmt
       (.buildIndex index-name Vertex)
       (.addKey key)
       (.buildMixedIndex "search"))))

(defn create-titan-indexes [graph]
  "create the needed indexes for wt prelaunch"
  (schema/with-management-system [mgmt graph]
    (let [email-key (get-or-create-property-key mgmt "email" String)]
      (build-composite-index mgmt "byEmail" email-key))))

(defn cleanup [graph]
  "cleanup the given graph"
  (tgraph/shutdown graph)
  (TitanCleanup/clear graph))


(defn fix-cassandra [graph index-names]
  "this waits for indexes to transition from installed to registerd or enable
and run the repair cassandra thing"
  (schema/with-management-system [mgmt graph]
    (letfn [(get-index [index-name]
              (.getGraphIndex mgmt index-name))
            (check-index [index-name]
              (let [idx (get-index index-name)]
                (when idx
                  (apply = true (map #(let [index-status (.getIndexStatus idx %)]
                                        (or (= SchemaStatus/REGISTERED index-status) (= SchemaStatus/ENABLED))) (.getFieldKeys idx))))))
            (repair-cass-index [index-name]
              (TitanIndexRepair/cassandraRepair (map->properties {"storage.backend" "cassandra"
                                                                  "storage.hostname" "localhost"})
                                                index-name "" "org.apache.cassandra.dht.Murmur3Partitioner"))
            (enable-index [index-name]
              (-> mgmt
                 (.updateIndex (get-index index-name) (SchemaAction/ENABLE_INDEX))))]
      (doseq [idx-name index-names]
        (loop [idx-status (check-index idx-name)]
          (when-not idx-status
            (Thread/sleep 500)
            (recur (check-index idx-name))))
        (timbre/info "Index REGISTERED" idx-name)

        ;; (timbre/info "Start hadoop job to repair indexes")
        ;; (repair-cass-index idx-name)
        
        ;; (timbre/info "RE enable index" idx-name)
        ;; (enable-index idx-name)
        ))))


(defn -main [& args]
  (core/start)
  (let [graph (get-in core/*system* [:titan :connection])
        mgmt (.getManagementSystem graph)]
    (timbre/debug (bean mgmt))
    ;;(cleanup graph)
    (create-titan-indexes graph)
    
    (fix-cassandra graph ["byEmail"])))
