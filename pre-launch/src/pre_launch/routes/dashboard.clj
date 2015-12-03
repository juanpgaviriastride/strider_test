(ns pre-launch.routes.dashboard
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.controllers.dashboard :as controller]
            [ring.util.response :refer [response]]
            [selmer.filters :as filter]
            [clojure.java.io :as io]))

(defn prepare-response [titan-id user-name]
  {:name user-name
   :titan-id titan-id
   :network-table (controller/get-referral-network titan-id)})


(defn dashboard [params session]
  (let [user-name (get-in session [:identity :name])
        titan-id (get-in session [:identity :titan_id])
        template-response (prepare-response titan-id user-name)]
    (println "the template-response is" template-response)
    (filter/add-filter! :monetizise (fn [amount] (format "$%,8d%n"(* (Integer. amount) 10))))
    (layout/render "crowdfunding/dashboard.html" template-response)))



(defn referral-network-detail [session]
  (let [user-name (get-in session [:identity :name])
        titan-id (get-in session [:identity :titan_id])
        template-response (controller/get-referral-network-detail-plain titan-id)]
    (ok template-response)))

(defroutes dashboard-routes
  (GET "/dashboard" request (dashboard (request :params) (request :session)))
  (GET "/dashboard/network-detail" request (referral-network-detail (request :session))))
