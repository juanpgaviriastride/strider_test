(ns pre-launch.routes.invitation
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [pre-launch.controllers.invitation :as controller]
            [ring.util.response :refer [redirect response]]
            [clojure.java.io :as io]))

(defn send-invitation! [request]
  (println "[save invitation] the invitations are:" (get-in request [:params :emails]))
  (let [referer-id (get-in request [:session :identity :titan_id])
        emails (get-in request [:params :emails])]
    (println "[save invitation] the referer ID is:" referer-id)
    (controller/send-invitation (first emails) referer-id)
    (ok)))


(defroutes invitation-routes
  (POST "/invitation" request (send-invitation! request)))
