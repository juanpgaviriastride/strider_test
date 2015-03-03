HTTPStatus = require "http-status"
config = require('../../../config')
async = require "async"
_ = require 'underscore'
grex = require 'grex'

class TimelineController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin

  connect: () =>
    #TODO: move index creation to /lib/db
    grex.createClient(config.get('titan'))

  list: (user_id, cb) =>
    query = @gremlin()
    query @graph.v(user_id).out("follow").out('posted')
    console.log query()
    @connect().execute(query, cb)

module.exports = TimelineController
