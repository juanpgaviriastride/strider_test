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
            [taoensso.timbre :refer [spy]]
            ring.middleware.session))

(defn check-password [email passw]
  (let [valid-password (session/validate-password email passw)]
    (spy valid-password)
    (if valid-password 
      (let [user (user/get email)]
        (spy  user)
        user))))

(defn login [session login-error]
  (layout/render  "crowdfunding/login.html" {:error login-error}))


(defn login! [{{email :email passw :password} :params
               session :session}]
  (if-let [identity (check-password email passw)]
    (-> (redirect "/dashboard")
       (assoc :session (assoc session :identity identity)))
    (login session true)))

(defn signup [request]
  (spy request)
  (let [key (stripe/public-key)]
    (layout/render "crowdfunding/new-account.html" {:public_stripe_key key})))

(defn logout! [request]
  (-> (redirect "/signin")
     (assoc :session (dissoc (:session request) :identity))))

(defroutes login-routes
  (GET  "/signin" request (if (authenticated? request)
                            (redirect "/dashboard")
                            (login (:session request) false)))
  (POST "/login"  request (login! request))
  (GET "/logout"  request (logout! request))
  (GET  "/signup" request (if (authenticated? request)
                            (redirect "/dashboard")
                            (signup request))))
