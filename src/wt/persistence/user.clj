(ns wt.persistence.user
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn prepare-get [db-record]
  (println "the db-record inside prepare-get is" db-record)
  (-> db-record
     (update-in [:birthday] db/string-date)
     (update-in [:start_date] db/string-date)
     (update-in [:end_date] db/string-date)
     )
  )

(defn get [id]
  (prepare-get (first (db/get-user {:id id}))))

(defn delete [id]
  (db/user-soft-delete! {:id id}))

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




