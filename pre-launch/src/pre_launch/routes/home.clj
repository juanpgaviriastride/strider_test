(ns pre-launch.routes.home
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ring.middleware.params]
            [pre-launch.controllers.invitation-request :as controller]
            [clojure.java.io :as io]))

 (defn home-page [success]
     (layout/render
      "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                   :success success}))

(defn new-account-page []
  (layout/render "crowdfunding/new-account.html"))

(defn join-wait-list [email]
  (controller/join-wait-list email)
  (home-page true))

(defroutes home-routes
  (GET "/" [] (home-page false))
  (POST "/request-invitation" [email] (join-wait-list email))
  (GET "/new-account" [] (new-account-page)))

