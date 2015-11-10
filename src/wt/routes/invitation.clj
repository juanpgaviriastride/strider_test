(ns wt.routes.invitation
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [wt.controllers.invitation :as controller]
            [schema.core :as s]))

(s/defschema Invitation {:invitation {:id Long
                                      :inviter_id Long
                                      :email String}
                         })



(def ^:dynamic *invitation* (atom {:invitation {:id 1
                                                :inviter_id 5
                                                ;;TODO add phonr :phone "+57 3004174815"
                                                :email "sarcilav@gmail.com"}}))

(defroutes* invite-routes
  (context* "/api/v1/invites" []
            :tags ["invitation"]

            (POST* "/" []
                   :return      Invitation
                   :body-params [invitation :- {:email  String :inviter_id Long}]
                   :summary     "Creates an invitation. "
                   (ok (controller/save-invitation invitation)))

            (GET* "/" []
                  :return      Invitation
                  :query-params [invitation_id :- Long]
                  :summary     "Returns an invitation for the current phone if exists."
                  (ok (controller/get-invitation invitation_id)))))

            

