(ns pre-launch.routes.login
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.session :as session]
            [pre-launch.model.user :as user]
            [clojure.java.io :as io]
            [pre-launch.integration.stripe :as stripe]
            [buddy.auth :refer [authenticated?]]
            [ring.util.response :refer [response redirect]]
            ring.middleware.session))

(defn check-password [email passw]
  (let [valid-password (session/validate-password email passw)]
    (println "valid-password" valid-password)
    (if valid-password 
      (let [user (user/get email)]
        (println "the user I found is" user)
        user))))

(defn login! [{{email :email passw :password} :params
               session :session}]
  (if-let [identity (check-password email passw)]
    (-> (redirect "/dashboard")
       (assoc :session (assoc session :identity identity)))
    (redirect "/signin")))

(defn login [{session :session}]
  (layout/render  "crowdfunding/login.html"))

(defn signup [request]
  (let [key (stripe/public-key)]
    (layout/render "crowdfunding/new-account.html" {:public_stripe_key key})))

(defn logout! [request]
  (-> (redirect "/signin")
     (assoc :session (dissoc (:session request) :identity))))

(defn recover-password [request]
  (layout/render "crowdfunding/recover-password.html"))

(defroutes login-routes
  (GET  "/signin" request (if (authenticated? request)
                            (redirect "/dashboard")
                            (login request)))
  (POST "/login"  request (login! request))
  (GET "/logout"  request (logout! request))
  (GET  "/signup" request (if (authenticated? request)
                            (redirect "/dashboard")
                            (signup request))))
