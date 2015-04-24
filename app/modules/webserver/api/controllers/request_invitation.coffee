RequestInvitation = require "app/request_invitation"


class RequestInvitationResource

  constructor: () ->
    @request_invitationsc = new RequestInvitation()

  create: (req, res) =>
    console.log("create")
    console.log(req.body)
    @request_invitationsc.create req.body, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response

      
module.exports = RequestInvitationResource
