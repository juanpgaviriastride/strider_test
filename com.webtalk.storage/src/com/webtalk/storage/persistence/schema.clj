(ns com.webtalk.storage.persistence.schema
  (:gen-class)
 (:require [com.webtalk.storage.persistence.config :as config]
           [clojurewerkz.cassaforte.client         :as cclient]
           [clojurewerkz.cassaforte.cql            :as cql]
           [clojurewerkz.cassaforte.query          :as query]))


(def tables
  {
   "users" {
            :user_id :uuid
            :timestamp :timestamp
            :email :varchar
            :full_name :varchar
            :primary-key [:user_id]
            }

   "email_users" {
                  :email :varchar
                  :timestamp :timestamp
                  :user_id :uuid
                  :full_name :varchar
                  :primary-key [:email :user_id :timestamp]
                  }

   "name_users" {
                 :full_name :varchar
                 :timestamp :timestamp
                 :user_id :uuid
                 :primary-key [:full_name :user_id :timestamp]
                 }

   "entries" {
              :entry_id :uuid
              :timestamp :timestamp
              :owner_id :uuid
              :type :varchar
              :title :varchar
              :text_content :text
              :url_content :varchar
              :file_content :varchar
              :primary-key [:entry_id :owner_id :timestamp]
              }

   "user_entries" {
                   :owner_id :uuid
                   :timestamp :timestamp
                   :entry_id :uuid
                   :type :varchar
                   :title :varchar
                   :text_content :text
                   :url_content :varchar
                   :file_content :varchar
                   :primary-key [:owner_id :entry_id :timestamp]
                   }

   "user_type_entries" {
                        :owner_id :uuid
                        :type :varchar
                        :timestamp :timestamp
                        :entry_id :uuid
                        :title :varchar
                        :text_content :text
                        :url_content :varchar
                        :file_content :varchar
                        :primary-key [:owner_id :type :timestamp]                        
                        }
   
   "invitations" {
                  :invitation_id :uuid
                  :timestamp :timestamp
                  :email :varchar
                  :inviter_id :uuid
                  :primary-key [:invitation_id :inviter_id :timestamp]
                  }

   "inviter_invitations" {
                          :inviter_id :uuid
                          :timestamp :timestamp
                          :email :varchar
                          :invitation_id :uuid
                          :primary-key [:inviter_id :invitation_id :timestamp]
                          }

   "email_invitations" {
                        :email :varchar
                        :timestamp :timestamp
                        :invitation_id :uuid
                        :inviter_id :uuid
                        :primary-key [:email :timestamp]
                        }

   "user_followings" {
                      :user_id :uuid
                      :timestamp :timestamp
                      :following_id :uuid
                      :primary-key [:user_id :following_id :timestamp]
                   }

   "user_followers" {
                     :user_id :uuid
                     :timestamp :timestamp
                     :follower_id :uuid
                     :primary-key [:user_id :follower_id :timestamp]
                     }

   "referrer" {
               :user_id :uuid
               :timestamp :timestamp
               :referred_user_id :uuid
               :primary-key [:user_id :referred_user_id :timestamp]
               }

   "user_timeline" {
                    :user_id :uuid
                    :timestamp :timestamp
                    :entry_id :uuid
                    :owner_id :uuid
                    :primary-key [:user_id :timestamp :entry_id]
                    }
   
   })

;;; For migrating

(defn -main
  [& args]
  (let [conn (cclient/connect config/cassandra-hosts)]
    ;; Create main keyspace
    (try
      (cql/create-keyspace conn config/keyspace (query/with {:replication
                                                            {:class "SimpleStrategy"
                                                             :replication_factor 1}}))
      (catch com.datastax.driver.core.exceptions.AlreadyExistsException e
        (println (.getMessage e))))
    (cql/use-keyspace conn config/keyspace)
    ))