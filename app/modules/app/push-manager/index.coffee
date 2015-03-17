socket = require "./socket"
apns = require "./apn"
#notifications = require("app/notifications")
# TODO: notification schema and storage (cassandra)
# current implementations assumes the normal CRUD
common = require "../../../config/common"


module.exports = new class PushManager
  init: (server, sessionConf) =>
    socket.init server, sessionConf
    apns.init()

  saveNotSentNotification: (to, notification) ->
    if notification._id then return yes # notification already saved
    notification_to_save = _.clone notification
    notification_to_save.user_id = to._id
    notification_to_save.message = notification.message.id
    notifications.create notification_to_save, (err, doc) ->
      if err then console.error err
      console.log " not send notification saved : ", doc

  deleteSentNotificaiton: (notification) ->
    notifications.deleteOne {_id: notification._id}, (err) ->
      if err
        console.log "ERROR on deleting sent notification", err
      else
        console.log "Sent notification deleted successfully"

  send: (to, notification) =>
    console.log " send message to user : ", to

    notification_to_send = _.omit notification, ['_id', '__v', 'user_id'] #General omits to support multiple backends
    notification_to_send.message = notification.message.text

    console.log " message to send : ", notification_to_send
    # logic of sending notification: browser/iOS/nothing
    # 1) check if the user is online
    # 2) check apn_token
    sent = no
    if socket.isUserOnline to
      socket.send to._id, notification_to_send
      sent = yes
    if to.apn_token?
      apns.send to.apn_token, notification_to_send
      sent = yes
    if sent and notification._id
      @deleteSentNotificaiton notification
    if not sent
      console.log " - can't send notification - user is offline -"
      @saveNotSentNotification to, notification
