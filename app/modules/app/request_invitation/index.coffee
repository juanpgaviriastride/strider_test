HTTPStatus = require "http-status"
Queue = require('../../../lib/queue')
validator = require('validator')


class RequestInvitationController
  constructor: () ->
    @queue = new Queue

  create: (body, cb) =>
    if body.requestInvite?
      body = body.requestInvite

    qname = "com.webtalk.storage.queue.request-an-invite"

    return cb({code: 400, msg: "Invalid email"} , null) unless validator.isEmail(body.email)

    return cb({code: 400, msg: "Invalid phone number"}) if body.phone? and not validator.isMobilePhone(body.phone, 'en-US')

    if body.enable_sms == "true"
      return cb({code: 400, msg: "Phone require"} , null) unless body.phone?
    
    @queue.publish(qname, body, null, cb)
    cb(null, {msg: "added to the waiting list."})

module.exports = RequestInvitationController
