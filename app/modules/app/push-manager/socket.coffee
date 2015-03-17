passportSocketIo = require "passport.socketio"
express = require("express")

module.exports =
  init: (server, sessionConf, cookieParser) ->
    console.log " initialize socket io module!!!"
    @io = require('socket.io').listen(server)
    SocketRedisStore = require 'socket.io/lib/stores/redis'
    redis_lib = require("redis")
    redis = require '../../../lib/redis'

    @io.configure () =>
      @io.set 'log level', 2
      @io.set 'store', new SocketRedisStore {
        redis: redis_lib
        redisPub : redis.pub
        redisSub : redis.sub
        redisClient : redis.store
      }

      @io.set 'transports', [
        'websocket',
        'xhr-polling',
        'flashsocket',
        'htmlfile',
        'jsonp-polling'
      ]

      # join passport and socket.io authorizations
      console.log "sessionConf:"
      console.log sessionConf
      # @io.set 'authorization', passportSocketIo.authorize({
      #   cookieParser: express.cookieParser
      #   key: 'connect.sid'
      #   store: sessionConf.store
      #   secret: sessionConf.secret
      #   fail: (data, message, error, accept) ->
      #     console.log "FAIL socket auth: #{data}, #{message}, #{error}"
      #     accept null, false
      #   success: (data, accept) ->
      #     console.log "SUCCESS socket auth"
      #     accept null, true
      # })

      @io.set 'authorization', (handshake, cb) =>
        console.log "USER: ", handshake.query
        handshake.user =
          _id: handshake.query.user
        cb(null, true)

    @io.sockets.on 'connection', (socket) ->
      console.log "socket connected for user : ", socket.handshake.user
      # connect user id with sockets rooms
      socket.join "room_" + socket.handshake.user._id
      informer = require "mypa/notification-manager"
      informer.sendAllNotifications socket.handshake.user

    @io.sockets.on 'disconnect', (socket) ->
      console.log "socket disconnected for user: ", socket.handshake.user

  send: (to, message) ->
    console.log 'send notifications to users'
    console.log ' -- users  : ', to
    console.log ' -- message: ', message
    if typeof to isnt 'array'
      to = [to]
    for client in to
      @io.sockets.in("room_#{client}").emit 'notification', message

  isUserOnline: (user) ->
    console.log @io.sockets.clients()
    clients = @io.sockets.clients "room_#{user._id}"
    console.log "isUserOnline: ", clients.length isnt 0
    return clients.length isnt 0
