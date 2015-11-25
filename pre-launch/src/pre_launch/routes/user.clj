(ns pre-launch.routes.user
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.user :as model]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]))


(defn save-user [user]
  (let [maybe-user (model/save user)]))

(defn create-user! [{{first_name :first_name last_name :last_name
                      email :email password :password} :params
                     session :session}]
  (model/save {:name (str first_name " " last_name) :email email :password password :stripe_account_id (session :stripe-costumer)})
  (println {:first_name first_name :last_name last_name :email email :password password} session)
  (-> "/dashboard"
     redirect
     (assoc :session (assoc session :identity (str first_name " " last_name)))))

(defroutes user-routes
  (POST "/user-creation" request (create-user! request)))
