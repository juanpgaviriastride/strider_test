config = require('../../../config')
apns = require 'apn'

module.exports = new class APNS
  init: () =>
    console.log "apns setup"
    options =
      cert : config.apn_cert
      key : config.apn_key
      gateway : config.apn_gateway
      port : config.apn_port
      errorCallback : (err, note) ->
        console.error "APN ERROR: ", err
        console.error "  -- note: ", note
    @apnsConnection = new apns.Connection options

    # listen to connection events
    @apnsConnection.on "error", (error) ->
      console.error "APN connection error: ", error
    @apnsConnection.on "transmitted", (notification) ->
      console.log "APN notification transmitted successfully"
    @apnsConnection.on "connected", () ->
      console.log "Successfully connectied to APN server"
    @apnsConnection.on "disconnected", () ->
      console.error "Connection to APN server has been closed!"
    @apnsConnection.on "transmissionError", (error, notification) ->
      console.error "APN Transmission Error, notification was invalid : ", error
      console.error "    notification : ", notification

  send: (token, message) =>
    console.log "APN send: ", token, message
    return yes
    device = new apns.Device token

    note = new apns.Notification
    note.expiry = Math.floor(Date.now() / 1000) + 3600 # expires 1 hour from now
    note.badge = 1
    note.sound = "ping.aiff"
    note.alert = "New message from Hopper"
    note.payload = message
    note.device = device

    note_size = JSON.stringify(note).length
    if note_size > 255 then return console.error "Error: too large push notification (#{note_size} more then 256b)"

    @apnsConnection.sendNotification note
