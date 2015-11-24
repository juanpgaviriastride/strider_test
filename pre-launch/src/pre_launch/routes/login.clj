(ns pre-launch.routes.login
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [ring.util.response :refer [response]]
            ring.middleware.session))

(defn check-password [email passw]
  :ok)

(defn login! [{{email :email passw :passw} :params
               session :session}]
  (if-let [identity (check-password email passw)]
    (do
      (assoc :session (assoc session :identity identity))
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body "rico"})
    {:status 403
     :headers {}
     :body "Not authorized"}))

(defn login [{session :session}]
  (layout/render  "crowdfunding/login.html"))

(defn signup [request]
  (layout/render "crowdfunding/new-account.html"))



(defroutes login-routes
  (GET  "/signin" request (login request))
  (POST "/login"  request (login! request))
  (GET  "/signup" request (signup request)))

