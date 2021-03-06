(ns pre-launch.model.user
  (:require
   [pre-launch.db.core :as db]
   [bcrypt-clj.auth :refer :all])
  (:import [java.sql.Date]))

(defn get [email]
  (let [query-result (db/get-user {:email email})]
    (if (empty? query-result)
      nil
      (first query-result))))

(defn delete [id]
  (db/delete-user! {:id id}))

(defn prepare-save [user-map]
  (->
   user-map
   (update-in [:password] crypt-password)
   (assoc :admin false)
   (assoc :last_login nil)
   (assoc :is_active true)))

(defn save [user-map]
  (let [user (prepare-save user-map)
        save-response (db/create-user<! user)]
    (println "[debugging create user] the save object in model is" user)
    (assoc user-map :id (:generated_key save-response))))


(defn set-titan-id [email titan-id]
  (db/set-titan-id! {:titan_id titan-id :email email}))

