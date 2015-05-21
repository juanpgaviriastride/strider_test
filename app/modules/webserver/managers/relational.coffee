class BaseWebserverManager
  instance = null

  start: (app) ->
    console.log "starting"
    @app = app
    @app.set 'bookshelf', @bookshelf

  constructor: () ->
    console.log "constructor"
    if instance
      console.log ">reusing"
      return instance
    else
      console.log ">creating"
      instance = @
    @initialize(arguments)


class RelationalManager extends BaseWebserverManager
  initialize: () =>
    console.log "initializing"
    @knex = require('knex')({
      "client": "pg",
      "connection": {
        "host": "192.168.50.4",
        "user": "webtalk",
        "password": "dummy",
        "database": "webtalk-dev",
      }
    })

    @bookshelf = require('bookshelf')(@knex)
    @Model = @bookshelf.Model
    console.log @Model

global.orm = module.exports = new RelationalManager()
