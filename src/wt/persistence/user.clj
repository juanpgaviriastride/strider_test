(ns wt.persistence.user
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn query-user [email]
  (db/get-user {:email email}))

(defn save [user-map]
  (let [result (db/create-user! {:name (:name user-map)
                                 :email (:email user-map)
                                 :birthday (db/sql-date (:birthday user-map))
                                 :gender (:gender user-map)
                                 :pass (crypt-password (:pass user-map))
                                 :admin false
                                 :last_login nil
                                 :is_active true})]

    (println "result!!")
    (println (query-user (:email user-map)))))


