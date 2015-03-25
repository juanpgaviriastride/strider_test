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
    amqp.connect (config.get("rabbitmq").host), (err, conn) =>
      qname = "com.webtalk.storage.queue.follow"
      opts = {autoDelete: true}
      console.log(err)
      conn.createChannel (err, ch) =>
        # ch.assertQueue(qname, opts)
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)
        # pending get back the follow creation status
  

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
