(ns wt.routes.invitation
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))

(s/defschema Invitation {:invitation {:invitation_id Long
                                      :inviter_id Long
                                      :email String}
                         })



(def ^:dynamic *invitation* (atom {:invitation {:invitation_id 1
                                                :inviter_id 5
                                                ;;TODO add phonr :phone "+57 3004174815"
                                                :email "sarcilav@gmail.com"}}))

(defroutes* invite-routes
  (context* "/api/v1/invites" []
            :tags ["invitation"]

            (POST* "/" []
                   :return      Invitation
                   :body-params [invitation :- {:email  String}]
                   :summary     "Creates an invitation. "
                   {:status 400 :body {:invitation {:invitation_id 1 :inviter_id 2 :email "lalala@la.com"}}})

            (GET* "/" []
                  :return      Invitation
                  :query-params [phone :- String]
                  :summary     "Returns an invitation for the current phone if exists."
                  (ok @*invitation*))))

            

