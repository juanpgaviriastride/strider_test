(ns pre-launch.routes.dashboard
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.controllers.dashboard :as controller]
            [ring.util.response :refer [response]]
            [selmer.filters :as filter]
            [clojure.java.io :as io]))


(defn dashboard [params session]
  (let [user-name (get-in session [:identity :name])
        titan-id (get-in session [:identity :titan_id])]
    (filter/add-filter! :monetizise (fn [amount] (format "$%,8d%n"(* (Integer. amount) 10))))
    (layout/render "crowdfunding/dashboard.html"
                   {:name user-name
                    :titan-id titan-id
                    :network-table (controller/get-referral-network titan-id)
                    })))

(defn sent-invites [session]
  (ok {:invites (controller/get-sent-invites (get-in session [:identity :titan_id]))}))

(defroutes dashboard-routes
  (GET "/dashboard" request (dashboard (request :params) (request :session)))
  (GET "/dashboard/sent-invites" request (sent-invites (request :session))))
