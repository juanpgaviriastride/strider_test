(ns pre-launch.routes.recover-password
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [pre-launch.model.user :as user]
            [pre-launch.controllers.recover-password :as controller]
            [ring.util.response :refer [response redirect]]
            ring.middleware.session))

(defn send-recover-email [email]
  (when-let [current-user (user/get email)]
    (controller/deliver-email (:id current-user) "sebastianarcila@gmail.com"))
  (redirect "/"))

(defn recover-password [request]
  (layout/render "crowdfunding/recover-password.html"))

(defn new-password [token]
  (let [{:keys [id email exp]} (controller/decrypt-token token)]
)
  (redirect "/"))

(defroutes recover-password-routes
  (GET "/recover-password" request (recover-password request))
  (POST "/recover" [email] (send-recover-email email))
  (GET "/new-password/:token" [token] (new-password token)))

