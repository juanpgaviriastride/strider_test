Follow = require "app/follow"


class FollowResource

  constructor: () ->
    @followc = new Follow()

  detail: (req, res) =>
    console.log("detail")
    @followc.detail req.params.id, (err, response) ->
      console.log(response)
      res.json response

  create: (req, res) =>
    console.log("create")

    @followc.create req.body, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response.results[0]

  list_follows: (req, res) =>
    console.log("list follows")
    id = req.query.user_id
    @followc.list_follows id, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response

  list_followers: (req, res) =>
    console.log("list followers")
    id = req.query.user_id
    @followc.list_followers id, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response



module.exports = FollowResource
