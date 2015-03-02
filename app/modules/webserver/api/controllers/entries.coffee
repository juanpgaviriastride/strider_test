Entry = require "app/entries"


class EntryResource

  constructor: () ->
    @entryc = new Entry()

  detail: (req, res) =>
    console.log("detail")
    @entryc.detail req.params.id, (err, response) ->
      console.log(response)
      res.json response

  create: (req, res) =>
    console.log("create")

    @entryc.create req.body, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response.results[0]

  list: (req, res) =>
    console.log("list")
    @entryc.list (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response



module.exports = EntryResource
