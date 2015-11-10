(ns wt.persistence.user
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn prepare-get [db-record]
  (-> db-record
     (update-in [:birthday] db/string-date)
     (update-in [:start_date] db/string-date)
     (update-in [:end_date] db/string-date)))

(defn prepare-update [user-map]
  (-> user-map
     (update-in [:birthday] db/sql-date)
     (update-in [:start_date] db/sql-date)
     (update-in [:end_date] db/sql-date)))

(defn update-user [user-map id]
  (let [user (prepare-update user-map)]
    (println "the user after preparing for update is" user)
    (db/update-user! (assoc user :id id))))

(defn get [email]
  (prepare-get (first (db/get-user {:email email}))))

(defn delete [id]
  (db/delete-user! {:id id}))

(defn prepare-save [user-map]
  (->
   user-map
   (update-in [:birthday] db/sql-date)
   (update-in [:start_date] db/sql-date)
   (update-in [:end_date] db/sql-date)
   (update-in [:password] crypt-password)
   (assoc :admin false)
   (assoc :last_login nil)
   (assoc :is_active true)))

(defn save [user-map]
  (let [user (prepare-save user-map)]
    (println "the prepared user map" user)
    (db/create-user<! user)))




