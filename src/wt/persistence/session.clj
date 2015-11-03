(ns wt.persistence.session
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all]
   [crypto.random :as crypto-random])
  (:import [java.sql.Date]))

(defn get-password [email]
  (:pass (first (db/get-password {:email email}))))

(defn validate-password [email password]
  
  (let [maybe-email (get-password email)]
    (if (nil? maybe-email)
      false
      (check-password password maybe-email))))

(defn create-session [email]
  (let [token (crypto-random/url-part 100)
        insert-result (db/create-session<! {:email email :username "" :token token})]
    {:id (:generated_key insert-result)
     :token token
     :email email}))

(defn get-session [id]
  (first (db/get-session {:id id})))


