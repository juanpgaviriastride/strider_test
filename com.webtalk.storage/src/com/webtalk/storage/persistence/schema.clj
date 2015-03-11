(ns com.webtalk.storage.persistence.schema
  (:gen-class)
  (:require [com.webtalk.storage.persistence.config :as config]
            [clojurewerkz.cassaforte.client         :as cclient]
            [clojurewerkz.cassaforte.cql            :as cql]
            [clojurewerkz.cassaforte.query          :as query]))

(def tables-definitions
  {
   "users" {
            :user_id :uuid
            :email :varchar
            :full_name :varchar
            :username :varchar
            :primary-key [:user_id]
            }

   "entries" {
              :owner_id :uuid
              :entry_id :timeuuid
              :type :varchar
              :title :varchar
              :text_content :text
              :url_content :varchar
              :file_content :varchar
              :primary-key [:owner_id :entry_id]
              }
   
   "invitations" {
                  :invitation_id :uuid
                  :email :varchar
                  :inviter_id :uuid
                  :primary-key [:invitation_id :inviter_id]
                  }

   "user_followings" {
                      :user_id :uuid
                      :following_id :uuid
                      :primary-key [:user_id :following_id]
                   }

   "user_followers" {
                     :user_id :uuid
                     :follower_id :uuid
                     :primary-key [:user_id :follower_id]
                     }

   "referrer" {
               :user_id :uuid
               :referred_user_id :uuid
               :primary-key [:user_id :referred_user_id]
               }

   "user_timeline" {
                    :user_id :uuid
                    :entry_id :timeuuid
                    :owner_id :uuid
                    :primary-key [:user_id :entry_id]
                    }
   
   })

(defn auto-table-options
  "Checks for compound keys and returns the clustering-column

   Example: (auto-table-options {
                                 :user_id :uuid
                                 :email :varchar
                                 :full_name :varchar
                                 :username :varchar
                                 :primary-key [:user_id, :email]
                                 })
   Will return :email"

  [columns]
  (let [clustering-column (second (columns :primary-key))]
    (if (nil? clustering-column)
      nil
      {:clustering-order [[clustering-column :desc]]})))

(defn create-tables
  "Create all the defined tables within tables

  Example: (create-tables tables)"

  [tables]
  (let [conn (cclient/connect config/cassandra-hosts)]
    ;; Create main keyspace
    (try
      (cql/create-keyspace conn config/keyspace (query/with {:replication
                                                            {:class "SimpleStrategy"
                                                             :replication_factor 1}}))
      (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
        (println (.getMessage e))))
    (cql/use-keyspace conn config/keyspace)
    ;; force lazy evaluation of map to ensure the side effects
    (doall (map (fn [[table-name colums]]
                  (let [options (auto-table-options colums)]
                   (try
                     (cql/create-table conn table-name
                                       (query/column-definitions colums)
                                       (if-not (nil? options) (query/with options)))
                     (println "Table" table-name "was created")
                     (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
                       (println (.getMessage e)))
                     (catch Exception e
                       (do
                         (println "Witin" table-name "table creation" (.getMessage e))
                         (throw e))))))
                tables-definitions))))