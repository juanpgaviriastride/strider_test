(ns wt.persistence.user
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn get [id]
  (db/get-user {:id id}))

(defn delete [id]
  (db/delete-user! {:id id})
  )

(defn save [user-map]
  (db/create-user<! {:name (:name user-map)
                       :email (:email user-map)
                       :birthday (db/sql-date (:birthday user-map))
                       :gender (:gender user-map)
                       :pass (crypt-password (:pass user-map))
                       :admin false
                       :last_login nil
                       :is_active true}))


