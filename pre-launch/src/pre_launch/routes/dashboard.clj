(ns pre-launch.routes.dashboard
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [pre-launch.config-mailer :as config-mailer]
            [ring.util.http-response :refer [ok]]
            [pre-launch.model.user :as user-model]
            [pre-launch.controllers.dashboard :as controller]
            [ring.util.response :refer [response]]
            [selmer.filters :as filter]
            [clojure.java.io :as io]
            [taoensso.timbre :refer [spy]]))

(defn prepare-response [titan-id user-name user-email referer-name]
  {:name user-name
   :email user-email
   :titan-id titan-id
   :network-table (controller/get-referral-network titan-id)
   :referer-name referer-name
   :custom_message (user-model/get-custom-email-message titan-id)
   :join_url (str (config-mailer/root-url) "/invite/" titan-id)})


(defn dashboard [params session]
  (let [user-name (get-in session [:identity :name])
        user-email (get-in session [:identity :email])
        titan-id (get-in session [:identity :titan_id])
        referer-data (when-let [referer-id   (:refererID session)]
                       (user-model/email-by-titan (spy referer-id)))
        template-response (prepare-response titan-id user-name user-email (:name referer-data))]
    (spy session)
    (spy template-response)
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
