HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'

class InvitationsController
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

  create: (body, cb) =>
    amqp.connect (config.get("rabbitmq").host), (err, conn) =>
      qname = "com.webtalk.storage.queue.invite"
      opts = {autoDelete: true}
      console.log(err)
      conn.createChannel (err, ch) =>
        # ch.assertQueue(qname, opts)
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)
        # pending get back the invitation

  list: (user_id, cb) =>
    query = @gremlin()
    query @graph.v(user_id).out('invited')
    console.log query()
    @connect().execute(query, cb)

  referred: (user_id, cb) =>
    query = @gremlin()
    query @graph.v(user_id).in('referred_by')
    console.log query()
    @connect().execute(query, cb)


module.exports = InvitationsController
