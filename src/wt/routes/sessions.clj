(ns wt.routes.sessions
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema User {:id Long
                   :username String
                   :token String})

(def *user* (atom {:id 1
                  :username 'sarcilav
                  :token 'resdhfjgkasldf}))
(defapi sessions-routes
  (ring.swagger.ui/swagger-ui
   "/swagger-ui")
  ;;JSON docs available at the /swagger.json route
  (swagger-docs
   {:info {:title "WT Api"}})
  (context* "/api/v1/users/session" []
            :tags ["session"]

            (POST* "/" []
                   :return      User
                   :body-params [username :- String, password :- String]
                   :summary     "Creates a user session."
                   (ok *user*))

            (GET* "/" []
                  :return      User
                  :query-params [token :- String]
                  :summary     "Returns the current user session."
                  (ok *user*))

            (DELETE* "/" []
                  :return      String
                  :query-params [token :- String]
                  :summary     "Ends the current user session."
                  (ok ""))))
