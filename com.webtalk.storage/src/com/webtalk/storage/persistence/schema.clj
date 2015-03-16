(ns com.webtalk.storage.persistence.schema
  (:gen-class)
  (:require [com.webtalk.storage.persistence.config :as config]
            [com.webtalk.storage.persistence.util   :as util]
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
    (util/create-keyspace conn config/keyspace {:replication
                                           {:class "SimpleStrategy"
                                            :replication_factor config/replication-factor}})
    
    (cql/use-keyspace conn config/keyspace)

    ;; force lazy evaluation of map to ensure the side effects of create tables
    (doall
     (map (fn [[table-name cols]]
            (let [options (auto-table-options cols)]
              (util/create-table conn table-name cols options)))
          tables-definitions))
    (cclient/disconnect conn)))

(defn clean-up
  "Clean all columnfamilies that are given in tables

   tables: [:t1, :t2, ...]
   Example: (clean-up tables)"

  [tables]
  (let [conn (cclient/connect config/cassandra-hosts config/keyspace)]
    (doall
     (map #(util/drop-table conn %) tables))
    (cclient/disconnect conn)))