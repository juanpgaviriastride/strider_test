config = require('../config')
amqp = require('amqplib/callback_api')

class Queue
  connect: (cb) =>
    amqp.connect(config.get("rabbitmq").host, cb)

  consume: (qname, cb) =>
    @connect (err, conn) =>
      opts = {durable: false, autoDelete: true, exclusive: false}
      return cb(err, {status: 'error'}) if err
      conn.createChannel (err, ch) =>
        ch.assertQueue(qname, opts)
        return cb(err, {status: 'error'}) if err
        ch.consume(qname, (msg) =>
          if (msg != null)
            console.log(msg)
            return cb(null, JSON.parse msg.content.toString())
          else
            return cb({status: 'error'}, nil)
        )

      
  publish: (qname, body, callback_qname, cb) =>
    @connect (err, conn) =>
      opts = {autoDelete: true, replyTo: callback_qname}
      console.log(err)
      return cb(err, {status: 'error'}) if err
      conn.createChannel (err, ch) =>
        return cb(err, {status: 'error'}) if err
        ch.sendToQueue(qname, new Buffer(JSON.stringify(body)), opts)

module.exports = Queue
