(ns pre-launch.routes.user
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.user :as model]
            [clojure.java.io :as io]))


(defn save-user [user]
  (let [maybe-user (model/save user)]))

(defn user-creation []
  (layout/render "crowdfunding/new-account.html"))

(defroutes user-routes
  (GET "/user" [] (user-creation))
  (POST "/user-creation" [first_name last_name email password]
        (do
          (println {:first_name first_name :last_name last_name :email email :password password}))
        (layout/render "crowdfunding/new-account.html")))
