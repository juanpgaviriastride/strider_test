(ns wt.routes.request-invite
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema InvitationRequest {:invite_request {:id Long
                                                 :email String
                                                 :phone String
                                                 :enable_sms Boolean} })

(def ^:dynamic *invitation-request* (atom {:invite_request {:id 1
                                                            :email "sarcilav@gmail.com"
                                                            :phone "+57 3004174815"
                                                            :enable_sms false}}))

(defroutes* request-invite-routes
  (context* "/api/v1/invites/request" []
            :tags ["request-invite"]

            (POST* "/" []
                   :return      (s/maybe InvitationRequest)
                   :body-params [invite_request :- {:email String :phone String :enable_sms  Boolean}]
                   :summary     "Creates a request for an invitation from email."
                   {:status 500 :body nil})

            (PUT* "/:id" []
                  :return      InvitationRequest
                  :body-params [invite_request :- {:email String :phone String :enable_sms  Boolean}]
                  :path-params [id :- Long]
                  :summary     "Updates the request for an invitation given ."
                  (ok @*invitation-request*))))


