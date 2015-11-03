(ns wt.persistence.session
  (:require
   [wt.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn get-password [email]
  (:pass (first (db/get-password {:email email}))))

(defn validate-password [email password]
  (check-password password (get-password email)))


