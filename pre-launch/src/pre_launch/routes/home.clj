(ns pre-launch.routes.home
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ring.middleware.params]
            [ring.util.response :refer [redirect response]]
            [pre-launch.controllers.invitation-request :as controller]
            [clojure.java.io :as io]))

 (defn home-page [success]
     (layout/render
      "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                   :success success}))

(defn refered-home-page [{params :params session :session}]
  (let [referer-id (:titan_id params)]
    (println "the referer-id in home page is" referer-id)
    (->
     (redirect "/")
     (assoc :session (assoc session :refererID referer-id)))))

(defn join-wait-list [{session :session params :params}]
  (controller/join-wait-list (:email params) (:refererID session))
  (home-page true))

(defroutes home-routes
  (GET "/" request (home-page false))
  (GET "/:titan_id" request (refered-home-page request))
  (POST "/request-invitation" request (join-wait-list request)))

