(ns com.webtalk.storage.graph.invitation-test
  (:use midje.sweet)
  (:require [com.webtalk.storage.graph.invitation :refer :all]))

(facts "about invitation-hash"
       (fact "it injects the vertex type"
             (invitation-hash {:s 's}) => {:VertexType "invitedUser" :s 's}))

(facts "about create-invitation!"
       (fact "it creates a new vertex")
       (fact "it overwrite the type of a requestedInvitation to invitedUser")
       (fact "it links the sending invite user with the invited user")
       (fact "it does nothing for already created user"))

(facts "about macro connect-invitation"
       (fact "it exapands"
             (connect-invitation "s") => ""))
