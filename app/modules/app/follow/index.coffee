HTTPStatus = require "http-status"
config = require('../../../config')
async = require "async"
_ = require 'underscore'
grex = require 'grex'

class FollowController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin

  connect: () =>
    #TODO: move index creation to /lib/db
    grex.createClient(config.get('titan'))

  detail: (id, cb) =>
    query = @gremlin()
    query  @graph.e(id) #use introspection for type
    # query @graph.commit()
    console.log query()
    @connect().execute(query, cb)

  create: (body, cb) =>
    query = @gremlin()
    user = query.var(@graph.v(body.user_id))
    follow = query.var(@graph.v(body.followed_id))
    query  @graph.addEdge(user, follow, "follow")
    # query @graph.commit()
    console.log query()
    @connect().execute(query, cb)

  list_follows: (id, cb) =>
    query = @gremlin()
    query @graph.v(id).out("follow")
    console.log query()
    @connect().execute(query, cb)

  list_followers: (id, cb) =>
    query = @gremlin()
    query @graph.v(id).in("follow")
    console.log query()
    @connect().execute(query, cb)

module.exports = FollowController
