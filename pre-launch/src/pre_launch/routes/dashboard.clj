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
                    :joined-waitlist-2 (controller/get-joined-waitlist titan-id 2)
                    :sent-invites-3 (controller/get-sent-invites titan-id 3)
                    :joined-prelaunch-3 (controller/get-joined-prelaunch titan-id 3)
                    :joined-waitlist-3 (controller/get-joined-waitlist titan-id 3)
                    :sent-invites-4 (controller/get-sent-invites titan-id 4)
                    :joined-prelaunch-4 (controller/get-joined-prelaunch titan-id 4)
                    :joined-waitlist-4 (controller/get-joined-waitlist titan-id 4)
                    :sent-invites-5 (controller/get-sent-invites titan-id 5)
                    :joined-prelaunch-5 (controller/get-joined-prelaunch titan-id 5)
                    :joined-waitlist-5 (controller/get-joined-waitlist titan-id 5)
                    })))

(Defn sent-invites [session]
  (ok {:invites (controller/get-sent-invites (get-in session [:identity :titan_id]))}))

(defroutes dashboard-routes
  (GET "/dashboard" request (dashboard (request :params) (request :session)))
  (GET "/dashboard/sent-invites" request (sent-invites (request :session))))
