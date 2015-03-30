HTTPStatus = require "http-status"
config = require('../../../config')
async = require "async"
_ = require 'underscore'
grex = require 'grex'
uuid = require('node-uuid')
Queue = require('../../../lib/queue')

class EntryController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin
    @queue = new Queue

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
    correlation_id = uuid.v4()
    callback_qname = "com.webtalk.api.queue.created-entry#{correlation_id}"
    @queue.consume(callback_qname, cb)    
    qname = "com.webtalk.storage.queue.create-entry"
    @queue.publish(qname, body, callback_qname, cb)

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "entry")
    console.log query()
    @connect().execute(query, cb)

module.exports = EntryController
