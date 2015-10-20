(ns wt.routes.request-invite
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema InvitationRequest {:id Long
                                :email String
                                :phone String
                                :enable_sms Boolean })

(def ^:dynamic *invitation-request* (atom {:id 1
                                           :email "sarcilav@gmail.com"
                                           :phone "+57 3004174815"
                                           :enable_sms false}))

(defroutes* request-invite-routes
  (context* "/api/v1/invites/request" []
            :tags ["request-invite"]

            (POST* "/" []
                   :return      InvitationRequest
                   :body-params [email :- String]
                   :summary     "Creates a request for an invitation from email."
                   (ok @*invitation-request*))

            (GET* "/" []
                  :return      InvitationRequest
                  :query-params [token :- String]
                  :summary     "Returns the current user session."
                  (ok @*invitation-request*))

            (DELETE* "/" []
                  :return      String
                  :query-params [token :- String]
                  :summary     "Ends the current user session."
                  (ok ""))))

