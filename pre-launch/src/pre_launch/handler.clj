(ns pre-launch.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [pre-launch.layout :refer [error-page]]
            [pre-launch.routes.home :refer [home-routes]]
            [pre-launch.routes.user :refer [user-routes]]
            [pre-launch.routes.dashboard :refer [dashboard-routes]]
            [pre-launch.routes.login :refer [login-routes]]
            [pre-launch.routes.recover-password :refer [recover-password-routes]]
            [pre-launch.routes.invitation :refer [invitation-routes]]
            [pre-launch.routes.payment :refer [payment-routes]]
            [pre-launch.middleware :as middleware]
            [pre-launch.db.core :as db]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]
            [pre-launch.config :refer [defaults]]
            [mount.core :as mount]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     ((fnil keyword :info) (env :log-level))
     :appenders {:rotor (rotor/rotor-appender
                          {:path (or (env :log-path) "pre_launch.log")
                           :max-size (* 512 1024)
                           :backlog 10})}})
  (mount/start)
  ((:init defaults)))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "pre-launch is shutting down...")
  (mount/stop)
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
   (wrap-routes #'login-routes middleware/wrap-csrf)
   (wrap-routes #'home-routes middleware/wrap-csrf)
   (wrap-routes #'user-routes middleware/wrap-csrf)
   (wrap-routes #'payment-routes middleware/wrap-csrf)
   (wrap-routes #'invitation-routes middleware/wrap-csrf)
   (-> #'dashboard-routes
            (wrap-routes  middleware/wrap-csrf)
            (wrap-routes  middleware/wrap-restricted))
   ;;(wrap-routes #'dashboard-routes middleware/wrap-csrf)
   (wrap-routes #'recover-password-routes middleware/wrap-csrf)
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
