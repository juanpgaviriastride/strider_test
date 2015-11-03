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

(defn find-session [email]
  (first (db/get-session-email {:email email})))

(defn create-or-find [email]
  (let [maybe-existing-session (find-session email)]
    (if (nil? maybe-existing-session)
      (create-session email)
      maybe-existing-session)))

(defn retrieve-session [token]
  (first (db/get-session {:token token})))

(defn delete-session [token]
  (db/delete-session-token! {:token token}))



