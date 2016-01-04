(ns pre-launch.routes.recover-password
  (:require [pre-launch.layout :as layout]
            [compojure.core :refer :all]
            [pre-launch.model.user :as user]
            [taoensso.timbre :refer [spy]]
            [pre-launch.controllers.recover-password :as controller]
            [ring.util.response :refer [response redirect]]
            ring.middleware.session))

(defn send-recover-email [request]
  (let [email (get-in request [:params :email])
        session (:session request)]
    (when-let [current-user (user/get email)]
    (controller/deliver-email (:id current-user) email))
    (-> (redirect "/")
       (assoc :session
              (assoc session :flash-message {:success true
                                             :message "If we happen to have this email on our database you will receive a message with instructions to reset your password."})))))  

(defn recover-password [request]
  (layout/render "crowdfunding/recover-password.html"))

(defn new-password [{{token :token} :params
                     session :session}]

  (if (controller/valid-timing? token)
    (-> (layout/render "crowdfunding/new-password.html")
       (assoc :session (assoc session :password-token token)))
    (redirect "/")))

(defn set-password [{password :password
                     inputConfirmPassword :inputConfirmPassword}
                    {token :password-token :as session}]
  (let [{:keys [id email exp]} (controller/decrypt-token token)
        current-user (user/get email)]
    (if (and (= password inputConfirmPassword) current-user (controller/valid-timing? id email exp))
      (do
        (user/set-password id password)
        (-> (redirect "/dashboard")
           (assoc :session (assoc session :identity current-user))))

      (redirect (str "/new-password/" token)))))

(defroutes recover-password-routes
  (GET "/recover-password" request (recover-password request))
  (POST "/recover" request (send-recover-email request))
  (GET "/new-password/:token" request (new-password request))
  (POST "/set-password" request (set-password (:params request) (:session request))))

