(ns wt.routes.sessions
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [wt.controllers.session :as controller]
            [schema.core :as s]))

(s/defschema Session-User {:session {:id Long
                                     :email String
                                     :token String}})

(def ^:dynamic *user* (atom {:session {:id 1
                                      :email "sarcilav@nullindustries.co"
                                      :token "resdhfjgkasldf"}}))

(defroutes* sessions-routes
  (context* "/api/v1/users/session" []
            :tags ["session"]
            
            (POST* "/" []
                   :return      Session-User
                   :body-params [session :- {:email String :password String}]
                   :summary     "Creates a user session."
                   (ok (controller/save session)))

            (GET* "/" []
                  :return      Session-User
                  :query-params [token :- String]
                  :summary     "Returns the current user session."
                  (ok (controller/retrieve-session token)))

            (DELETE* "/" []
                  :return      String
                  :query-params [id :- String]
                  :summary     "Ends the current user session."
                  (controller/delete-session id))))
