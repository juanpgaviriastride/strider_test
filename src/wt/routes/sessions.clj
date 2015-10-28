(ns wt.routes.sessions
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema Session-User {:id Long
                   :username String
                   :token String})

(def ^:dynamic *user* (atom {:id 1
                  :username "sarcilav"
                  :token "resdhfjgkasldf"}))

(defroutes* sessions-routes
  (context* "/api/v1/users/session" []
            :tags ["session"]
            
            (POST* "/" []
                   :return      Session-User
                   :body-params [username :- String, password :- String]
                   :summary     "Creates a user session."
                   (ok @*user*))

            (GET* "/" []
                  :return      Session-User
                  :query-params [token :- String]
                  :summary     "Returns the current user session."
                  (ok @*user*))

            (DELETE* "/" []
                  :return      String
                  :query-params [token :- String]
                  :summary     "Ends the current user session."
                  (ok ""))))
