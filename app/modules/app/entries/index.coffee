HTTPStatus = require "http-status"
config = require('../../../config')
async = require "async"
_ = require 'underscore'
grex = require 'grex'

class EntryController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin

  connect: () =>
    #TODO: move index creation to /lib/db
    grex.createClient(config.get('titan'))

  detail: (id, cb) =>
    query = @gremlin()
    query  @graph.v(id) #use introspection for type
    # query @graph.commit()
    console.log query()
    @connect().execute(query, cb)

  create: (body, cb) =>
    query = @gremlin()
    entry = query.var(@graph.addVertex({content: body.content, title: body.title, VertexType:'entry'}))
    owner = query.var(@graph.v(body.user_id))
    query  @graph.addEdge(owner, entry, "posted", {time: Date.now() })
    # query @graph.commit()
    console.log query()
    @connect().execute(query, cb)

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "entry")
    console.log query()
    @connect().execute(query, cb)

module.exports = EntryController
