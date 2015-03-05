User = require "app/users"


class UserResource

  constructor: () ->
    @userc = new User()

  detail: (req, res) =>
    console.log("detail")
    @userc.detail req.params.id, (err, response) ->
      console.log(response)
      res.json response

  create: (req, res) =>
    console.log("create")

    @userc.create req.body, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response.results[0]

  list: (req, res) =>
    console.log("list")
    @userc.list (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response


  update: (req, res, next) =>
    console.log("update")
    next()

module.exports = UserResource
