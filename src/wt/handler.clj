(ns wt.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [wt.layout :refer [error-page]]
            [wt.routes.home :refer [home-routes]]
            [wt.routes.services :refer [service-routes]]
            [wt.routes.sessions :refer [sessions-routes]]
            [wt.routes.request-invite :refer [request-invite-routes]]
            [wt.routes.invitation :refer [invite-routes]]
            [wt.routes.user :refer [user-routes]]
            [wt.middleware :as middleware]
            [wt.db.core :as db]
            [compojure.route :as route]
            [compojure.api.sweet :refer [defapi]]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     (if (env :dev) :trace :info)
     :appenders {:rotor (rotor/rotor-appender
                          {:path "wt.log"
                           :max-size (* 512 1024)
                           :backlog 10})}})

  (if (env :dev) (parser/cache-off!))
  (db/connect!)
  (timbre/info "Connected to the database")
  (timbre/info (str
                 "\n-=[wt started successfully"
                 (when (env :dev) " using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "wt is shutting down...")
  (db/disconnect!)
  (timbre/info "shutdown complete!"))

(defapi api
  (ring.swagger.ui/swagger-ui
   "/swagger-ui")
  ;;JSON docs available at the /swagger.json route
  (compojure.api.sweet/swagger-docs
   {:info {:title "WT Api"}})
  (var sessions-routes)
  (var invite-routes)
  (var user-routes)
  (var request-invite-routes))

(def app-routes
  (routes
   ;;(var service-routes)
   (var api)
   (wrap-routes #'home-routes middleware/wrap-csrf)
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
