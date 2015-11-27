(ns pre-launch.routes.user
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.user :as model]
            [pre-launch.controllers.user :as controller]
            [ring.util.response :refer [redirect]]
            [clojure.java.io :as io]))


(defn save-user [user]
  (let [maybe-user (model/save user)]))

(defn create-user! [request]
  (let [save-response (controller/create-user! request)]
    (-> "/dashboard"
     redirect
     (assoc :session (assoc (:session request) :identity save-response)))) )

(defroutes user-routes
  (POST "/user-creation" request (create-user! request)))
