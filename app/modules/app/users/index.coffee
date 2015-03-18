HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'
amqp = require('amqplib/callback_api')

class UserController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin

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
    amqp.connect (config.get("rabbitmq").host), (err, conn) =>
      qname = "com.webtalk.storage.queue.create-user"
      opts = {autoDelete: true}
      console.log(err)
      conn.createChannel (err, ch) =>
        # ch.assertQueue(qname, opts)
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)
   
      

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "user")
    console.log query()
    @connect().execute(query, cb)

module.exports = UserController
