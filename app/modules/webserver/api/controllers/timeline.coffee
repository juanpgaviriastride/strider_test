Timeline = require "app/timeline"


class TimelineResource

  constructor: () ->
    @timelinec = new Timeline()

  list: (req, res) =>
    console.log("list user timeline")
    id = req.query.user_id
    @timelinec.list id, (err, response) ->
      console.log err, response
      return res.json(err) if err
      res.json response

module.exports = TimelineResource
