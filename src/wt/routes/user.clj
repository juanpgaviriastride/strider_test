(ns wt.routes.user
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema User {
                   :name String
                   :birthday String
                   :gender String
                   :email String
                   :password String
                   })


(s/defschema User-Response {
                            :id Long
                            :name String
                            :birthday String
                            :gender String
                            :email String
                            
                   })
(def ^:dynamic *update-response* (atom {
                                        :id 123
                                        :name "John Galt"
                                        :birthday "11/31/1975"
                                        :gender "male"
                                        :email "john.galt@gmail.com"
                            
                             }))

(def ^:dynamic *user-response* (atom {
                                      :id 123
                                      :name "John Galt"
                                      :birthday "12/30/1985"
                                      :gender "male"
                                      :email "john.galt@gmail.com"
                            
                             }))


(defroutes* user-routes
  (context* "/api/v1/invites/user" []
            :tags ["user"]

            (POST* "/" []
                   :return      User-Response
                   :body-params [name :- String birthday :- String gender :- String email :- String password :- String]
                   :summary     "Creates an user on the system so that a session can be created"
                   (ok @*user-response*))

            (DELETE* "/:id" []
                  :return      String
                  :header-params [x-authorization :- String]
                  :path-params [id :- Long]
                  :summary     "Deletes a user (logically) in the app."
                  (ok ""))

            (PUT* "/:id" []
                  :return      User-Response
                  :body-params [name :- String birthday :- String gender :- String email :- String password :- String]
                  :path-params [id :- Long]
                  :header-params [x-authorization :- String]
                  :summary     "Updates the basic information of a user in the system."
                  (ok @*update-response*))))


