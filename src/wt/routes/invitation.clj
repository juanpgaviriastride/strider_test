(ns wt.routes.invitation
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [wt.controllers.invitation :as controller]
            [schema.core :as s]
            wt.middleware))

(s/defschema Invitation {:invitation {:id Long
                                      :inviter_id Long
                                      :email String
                                      :token String
                                      }
                         })



(def ^:dynamic *invitation* (atom {:invitation {:id 1
                                                :inviter_id 5
                                                ;;TODO add phonr :phone "+57 3004174815"
                                                :email "sarcilav@gmail.com"
                                                :token "kjsaksljakslj"}}))

(defroutes* invite-routes
  (context* "/api/v1/invites" []
            :tags ["invitation"]

            (POST* "/" []
                   :auth token
                   :return      String
                   :body-params [invitation :- {:email  String :inviter_id Long}]
                   :header-params [x-authorization :- String]
                   :summary     "Creates an invitation. "
                   (ok (controller/save-invitation invitation)))

            (GET* "/token/:token" []
                  :return      (s/maybe Invitation)
                  :path-params [token :- String]
                  :summary     "Creates an invitation. "
                  (controller/validate-invitation token))

            (GET* "/" []
                  :auth token
                  :return      (s/maybe  Invitation)
                  :query-params [invitation_id :- Long]
                  :header-params [x-authorization :- String]
                  :summary     "Returns an invitation for the current phone if exists."
                  (controller/get-invitation invitation_id))))

            

