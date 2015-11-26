(ns pre-launch.routes.login
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.session :as session]
            [pre-launch.model.user :as user]
            [clojure.java.io :as io]
            [pre-launch.integration.stripe :as stripe]
            [ring.util.response :refer [response]]
            ring.middleware.session))

(defn check-password [email passw]
  (if (session/validate-password email passw)
    (user/get email)
    {}))

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
  (let [key (stripe/public-key)]
    (layout/render "crowdfunding/new-account.html" {:public_stripe_key key})))



(defroutes login-routes
  (GET  "/signin" request (login request))
  (POST "/login"  request (login! request))
  (GET  "/signup" request (signup request)))

