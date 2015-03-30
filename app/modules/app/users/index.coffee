HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'
amqp = require('amqplib/callback_api')
uuid = require('node-uuid')

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
      correlation_id = uuid.v4()
      callback_qname = "com.webtalk.api.queue.created-user#{correlation_id}"
      conn.createChannel (err, ch) =>
        ch.assertQueue(callback_qname, {durable: false, autoDelete: true, exclusive: false})
        cb(err, {status: 'error'}) if err
        ch.consume(callback_qname, (msg) =>
          if (msg != null)
            console.log(msg)
            cb(null, JSON.parse msg.content.toString())
          else
            cb({status: 'error'}, nil)
        )
      qname = "com.webtalk.storage.queue.create-user"
      opts = {autoDelete: true, replyTo: callback_qname}
      console.log(err)
      conn.createChannel (err, ch) =>
        cb(err, {status: 'error'}) if err
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)
      

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "user")
    console.log query()
    @connect().execute(query, cb)

module.exports = UserController
