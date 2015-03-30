HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'
uuid = require('node-uuid')
Queue = require('../../../lib/queue')

class UserController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin
    @queue = new Queue

  create_indexes: () =>
    #
    # Wipe graph:
    # g.V.each{g.removeVertex(it)}
    # g.commit()
    #
    # Create index
    # g.makeKey("VertexType").dataType(String.class).indexed(Vertex.class).make()
    # g.commit()
    #
    # Check indexes
    # g.getIndexedKeys(Vertex.class)
    # ==>VertexType
    #
    query = @graph.connect()
    @execute().createKeyIndex(query)

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
    callback_qname = "com.webtalk.api.queue.created-user#{correlation_id}"
    @queue.consume(callback_qname, cb)
    qname = "com.webtalk.storage.queue.create-user"
    @queue.publish(qname, body, callback_qname, cb)      

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "user")
    console.log query()
    @connect().execute(query, cb)

module.exports = UserController
