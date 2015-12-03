(ns pre-launch.routes.home
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ring.middleware.params]
            [selmer.filters :as filter]
            [ring.util.response :refer [redirect response]]
            [pre-launch.controllers.landing :as scontroller]
            [pre-launch.controllers.invitation-request :as controller]
            [clojure.java.io :as io]))

(defn home-page [success]
  (let [params-hash (scontroller/get-overal-app-stats)]
    (filter/add-filter! :money-format (fn [amount] (format (Integer. "$%,8d%n"))))
    (layout/render
      "home.html" params-hash)))

(defn refered-home-page [{params :params session :session}]
  (let [referer-id (:titan_id params)]
    (->
     (redirect "/")
     (assoc :session (assoc session :refererID referer-id)))))

(defn join-wait-list [{session :session params :params}]
  (controller/join-wait-list (:email params) (:refererID session))
  (home-page true))

(defroutes home-routes
  (GET "/" request (home-page false))
  (GET "/invite/:titan_id" request (refered-home-page request))
  (POST "/request-invitation" request (join-wait-list request)))

