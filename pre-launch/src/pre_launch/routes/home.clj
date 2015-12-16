(ns pre-launch.routes.home
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ring.middleware.params]
            [selmer.filters :as filter]
            [ring.util.response :refer [redirect response]]
            [pre-launch.controllers.landing :as scontroller]
            [pre-launch.controllers.invitation-request :as controller]
            [pre-launch.config-mailer :as config]
            [taoensso.timbre :refer [spy]]
            [clojure.java.io :as io]))

(defn home-page [success]
  (let [params-hash (scontroller/get-overal-app-stats)
        final-params (assoc params-hash :success success)]
    (filter/add-filter! :money-format (fn [amount] (format (Integer. "$%,8d%n"))))
    (layout/render
     "home.html" final-params)))

(defn refered-home-page [{params :params session :session}]
  (let [referer-id (:titan_id params)]
    (->
     (redirect "/")
     (assoc :session (assoc session :refererID referer-id)))))


(defn decide-home [request success]
  (let [requesting-url (get-in request [:headers "host"])
        wt-url (config/webtalk-url)]
    (spy requesting-url)
    (spy wt-url)
    (if (= wt-url requesting-url) 
      (layout/render "webtalk/home.html" {:success  success})
      (home-page success))))

(defn join-wait-list [{session :session params :params :as req}]
  (controller/join-wait-list (:email params) (:refererID session))
  (decide-home req true))



(defroutes home-routes
  (GET "/" request (decide-home request false))
  (GET "/invite/:titan_id" request (refered-home-page request))
  (POST "/request-invitation" request (join-wait-list request)))

