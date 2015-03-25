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
    amqp.connect (config.get("rabbitmq").host), (err, conn) =>
      qname = "com.webtalk.storage.queue.create-entry"
      opts = {autoDelete: true}
      console.log(err)
      conn.createChannel (err, ch) =>
        # ch.assertQueue(qname, opts)
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)
        # pending get back the created entry

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "entry")
    console.log query()
    @connect().execute(query, cb)

module.exports = EntryController
