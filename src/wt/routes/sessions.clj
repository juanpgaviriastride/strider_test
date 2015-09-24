(ns wt.routes.sessions
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema User {:id Long
                   :username String
                   :token String})

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
                   (ok {:id 1 :username "chila" :token "1"}))

            (GET* "/" []
                  :return      User
                  :query-params [token :- String]
                  :summary     "Returns the current user session."
                  (ok {:id 1 :username "chila" :token "1"}))

            (DELETE* "/" []
                  :return      String
                  :query-params [token :- String]
                  :summary     "Ends the current user session."
                  (ok ""))))
