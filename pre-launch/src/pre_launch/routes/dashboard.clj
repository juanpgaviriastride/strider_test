(ns pre-launch.routes.dashboard
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.controllers.dashboard :as controller]
            [ring.util.response :refer [response]]
            [clojure.java.io :as io]))


(defn dashboard [params session]
  (let [user-name (get-in session [:identity :name])
        titan-id (get-in session [:identity :titan_id])]
    (layout/render "crowdfunding/dashboard.html"
                   {:name user-name
                    :titan-id titan-id
                    :sent-invites (controller/get-sent-invites titan-id 1)
                    :joined-waitlist (controller/get-joined-waitlist titan-id 1)
                    :joined-prelaunch (controller/get-joined-prelaunch titan-id 1)
                    :sent-invites-2 (controller/get-sent-invites titan-id 2)
                    :joined-prelaunch-2 (controller/get-joined-prelaunch titan-id 2)
                    :joined-waitlist-2 (controller/get-joined-waitlist titan-id 2)})))

(defn sent-invites [session]
  (ok {:invites (controller/get-sent-invites (get-in session [:identity :titan_id]))}))

(defroutes dashboard-routes
  (GET "/dashboard" request (dashboard (request :params) (request :session)))
  (GET "/dashboard/sent-invites" request (sent-invites (request :session))))
