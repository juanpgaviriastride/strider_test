HTTPStatus = require "http-status"
Queue = require('../../../lib/queue')

class RequestInvitationController
  constructor: () ->
    @queue = new Queue

  create: (body, cb) =>
    qname = "com.webtalk.storage.queue.request-an-invite"
    #pending make email and phone validations and better messages with status codes
    @queue.publish(qname, body, null, cb)
    cb(null, {msg: "added to the waiting list."})

module.exports = RequestInvitationController
