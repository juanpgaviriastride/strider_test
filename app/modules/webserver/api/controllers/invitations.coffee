Invitations = require "app/invitations"


class InvitationResource

  constructor: () ->
    @invitationsc = new Invitations()

  create: (req, res) =>
    console.log("create")
    console.log(req.body)
    @invitationsc.create req.body, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response.results[0]

  list: (req, res) =>
    console.log("list")
    @invitationsc.list req.params.user_id, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response

  referred: (req, res) =>
    console.log("referred")
    @invitationsc.referred req.params.user_id, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response
      
module.exports = InvitationResource
