express = require "express"
require('express-namespace')


apiController = module.exports =  express()

apiController.namespace("/api/v1", () ->

  apiController.get '/logout', (req, res) ->
    if req.user
      res.clearCookie("connect.sid")
      res.clearCookie("auth_token")
      res.clearCookie("user")
      req.session.destroy()

    res.json({status: 200})
  ##
  # Users controllers
  ##

  UserResource = require("./controllers/users")

  # user list
  apiController.get('/users', (req, res, next) ->
    new UserResource().list(req, res, next)
  )

  # user detail
  apiController.get('/users/:id', (req, res, next) ->
    new UserResource().detail(req, res, next)
  )

  # create new user
  apiController.post('/users', (req, res, next) ->
    new UserResource().create(req, res, next)
  )

  # update new user
  apiController.put('/users/:id', (req, res, next) ->
    new UserResource().update(req, res, next)
  )

  # delete new user
  apiController.del('/users/:id', (req, res, next) ->
    new UserResource().delete(req, res, next)
  )

  ##
  # Entries controller
  ##

  EntryResource = require "./controllers/entries"

  # entries list of the given user_id
  apiController.get('/entries', (req, res, next) ->
    new EntryResource().list(req, res, next)
  )

  # entry detail
  apiController.get('/entries/:id', (req, res, next) ->
    new EntryResource().detail(req, res, next)
  )

  # create new entry
  apiController.post('/entries', (req, res, next) ->
    new EntryResource().create(req, res, next)
  )

  # update entry
  apiController.put('/entries/:id', (req, res, next) ->
    new EntryResource().update(req, res, next)
  )

  # delete entry
  apiController.del('/entries/:id', (req, res, next) ->
    new EntryResource().delete(req, res, next)
  )

  ##
  # Follow controller
  ##

  FollowResource = require "./controllers/follows"

  # follows list of the given user id
  apiController.get('/follows', (req, res, next) ->
    new FollowResource().list_follows(req, res, next)
  )

  # followers list of the given user id
  apiController.get('/followers', (req, res, next) ->
    new FollowResource().list_followers(req, res, next)
  )

  # follow detail
  apiController.get('/follows/:id', (req, res, next) ->
    new FollowResource().detail(req, res, next)
  )

  # create new follow
  apiController.post('/follows', (req, res, next) ->
    new FollowResource().create(req, res, next)
  )

  # update follow
  apiController.put('/follows/:id', (req, res, next) ->
    new FollowResource().update(req, res, next)
  )

  # delete follow
  apiController.del('/follows/:id', (req, res, next) ->
    new FollowResource().delete(req, res, next)
  )

  ##
  # Timeline
  ##

  TimelineResource = require("./controllers/timeline")

  # Timeline
  apiController.get('/timeline', (req, res, next) ->
    new TimelineResource().list(req, res, next)
  )

  ##
  # Invitations
  # ##

  InvitationResource = require("./controllers/invitations")

  apiController.post("/invitations", (req, res, next) ->
    new InvitationResource().create(req, res, next)
  )

  apiController.get("/invitations/:user_id", (req, res, next) ->
    new InvitationResource().list(req, res, next)
  )

  apiController.get("/referred/:user_id", (req, res, next) ->
    new InvitationResource().referred(req, res, next)
  )

)
