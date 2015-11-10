(ns wt.routes.user
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [wt.controllers.user :as controller]
            [schema.core :as s]))

(s/defschema User {
                   :user {
                          :name String
                          :email String
                          :birthday String
                          :profession String
                          :avatar_url String
                          :type String
                          :city_name String
                          :job_title String
                          :former_job_title String 
                          :place_of_employment String
                          :former_place_of_employment String 
                          :start_date String
                          :end_date String
                          :location String
                          :ocupation String
                          :industry String
                          :seeking_job Boolean
                          :phone String
                          :degree String
                          :field_of_study String 
                          :school_name String
                          :facebook_profile String
                          :linkedin_profile String
                          :gender String
                          :password String}
                   })


(s/defschema User-Response {:user {
                                   :id Long
                                   :name String
                                   :email String
                                   :birthday String
                                   :gender String
                                   :profession String
                                   :avatar_url String
                                   :type String
                                   :city_name String
                                   :job_title String
                                   :former_job_title String 
                                   :place_of_employment String
                                   :former_place_of_employment String 
                                   :start_date String
                                   :end_date String
                                   :location String
                                   :ocupation String
                                   :industry String
                                   :seeking_job Boolean
                                   :phone String
                                   :degree String
                                   :field_of_study String 
                                   :school_name String
                                   :facebook_profile String
                                   :linkedin_profile String}
                            
                   })
(def ^:dynamic *update-response* (atom
                                  {:user {
                                          :id 123
                                          :name "John Galt"
                                          :email "john@nullindustries.co"
                                          :birthday "11/31/1980"  
                                          :profession "Software Developer"
                                          :avatar_url "http://www.google.com"
                                          :type "type"
                                          :city_name "New York"
                                          :job_title "Lead developer"
                                          :former_job_title "Junior Developer" 
                                          :place_of_employment "Null industries"
                                          :former_place_of_employment "SAP" 
                                          :start_date "10/20/2014"
                                          :end_date ""
                                          :location "New York, New York, United States"
                                          :ocupation "Software development"
                                          :industry "Software development"
                                          :seeking_job false
                                          :phone "(054) 4445566"
                                          :degree "Software Engineer"
                                          :field_of_study "Distributed Systems" 
                                          :school_name "Michigan Institute for Technology"
                                          :facebook_profile "http://facebook.com"
                                          :linkedin_profile "http://linkedin.com"
                                          :gender "male"
                                          }
                                   }))

(def ^:dynamic *user-response* (atom
                                {:user {
                                        :id 123
                                        :name "J Galt"
                                        :email "john@gmail.com"
                                        :birthday "11/31/1980"  
                                        :profession "Software Developer"
                                        :avatar_url "http://www.google.com"
                                        :type "type"
                                        :city_name "New York"
                                        :job_title "Lead developer"
                                        :former_job_title "Junior Developer" 
                                        :place_of_employment "Null industries"
                                        :former_place_of_employment "SAP" 
                                        :start_date "10/20/2014"
                                        :end_date ""
                                        :location "Medellin, Antioquia, Colombia"
                                        :ocupation "Software development"
                                        :industry "Software development"
                                        :seeking_job true
                                        :phone "(054) 12345678"
                                        :degree "Software Engineer"
                                        :field_of_study "Distributed Systems" 
                                        :school_name "Michigan Institute for Technology"
                                        :facebook_profile "http://facebook.com"
                                        :linkedin_profile "http://linkedin.com"
                                        :gender "male"
                                        }
                                 }))


(defroutes* user-routes
  (context* "/api/v1/invites/user" []
            :tags ["user"]

            (GET* "/:email" []
                  :return      User-Response
                  :header-params [x-authorization :- String]
                  :path-params [email :- String]
                  :summary     "Finds the info of a user in the system. and does something"
                  (let [c-result (controller/get email)]
                    (println "controller result" c-result)
                    (ok c-result)
                    ))

            (DELETE* "/:id" []
                  :return      String
                  :header-params [x-authorization :- String]
                  :path-params [id :- Long]
                  :summary     "Deletes a user (logically) in the app."
                  (ok (controller/delete id)))

            (POST* "/" []
                   :return      User-Response
                   :body-params [user :- 
                                 {:name String
                                  :email String
                                  :birthday String
                                  :profession String
                                  :avatar_url String
                                  :type String
                                  :city_name String
                                  :job_title String
                                  :former_job_title String 
                                  :place_of_employment String
                                  :former_place_of_employment String 
                                  :start_date String
                                  :end_date String
                                  :location String
                                  :ocupation String
                                  :industry String
                                  :seeking_job Boolean
                                  :phone String
                                  :degree String
                                  :field_of_study String 
                                  :school_name String
                                  :facebook_profile String
                                  :linkedin_profile String
                                  :gender String
                                  :password String
                                  }]
                   :summary     "Creates an user on the system so that a session can be created."
                   (ok (controller/save user)))

            (PUT* "/:id" []
                  :return      User-Response
                  :body-params [user :- 
                                {:name String
                                 :email String
                                 :birthday String
                                 :profession String
                                 :avatar_url String
                                 :type String
                                 :city_name String
                                 :job_title String
                                 :former_job_title String 
                                 :place_of_employment String
                                 :former_place_of_employment String 
                                 :start_date String
                                 :end_date String
                                 :location String
                                 :ocupation String
                                 :industry String
                                 :seeking_job Boolean
                                 :phone String
                                 :degree String
                                 :field_of_study String 
                                 :school_name String
                                 :facebook_profile String
                                 :linkedin_profile String
                                 :gender String
                                 }]
                  :path-params [id :- Long]
                  :header-params [x-authorization :- String]
                  :summary     "Updates the basic information of a user in the system."
                  (ok (controller/update user id)))))


