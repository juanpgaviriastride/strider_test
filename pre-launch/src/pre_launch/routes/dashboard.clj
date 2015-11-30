(ns pre-launch.routes.dashboard
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.user :as model]
            [ring.util.response :refer [response]]
            [clojure.java.io :as io]))


(defn dashboard [params session]
  (let [user-name (get-in session [:identity :name])
        titan-id (get-in session [:identity :titan_id])]
    (layout/render "crowdfunding/dashboard.html" {:name user-name :titan-id titan-id})))

(defroutes dashboard-routes
  (GET "/dashboard" request (dashboard (request :params) (request :session))))
