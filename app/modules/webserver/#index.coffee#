# Configuration
http = require("http")
express = require("express")
session = require("express-session")
bodyParser = require('body-parser')
cookieParser = require('cookie-parser')
#json = require('express-json')

fs = require("fs")
path = require("path")
config = require("../../config")
#RedisStore = require('connect-redis')(express)

#redis = require("../../lib/redis")
#db = require("../../lib/db")

# Mailer
#mailer = require('../../lib/mailer')
#mailer.setTemplateRoot(path.join __dirname, './views/emails')
#mailer.setTemplateExt('.html')
#mailer.compile()


# express app
app = express()

#express.request.redis = redis

#sessionConf =
#  secret: "X:o>&O-/o\QsIa~@n))sQ|ON(x|KV0u?$+H`Y:Oi3n!0i:V2z"
#  store: new RedisStore
#    host: config.get('redis').host,
#    port: config.get('redis').port,
#    db: 2,
#    pass: config.get('redis').options.auth



#app.use express.favicon(path.join __dirname, './public/favicon.ico')
#app.use express.logger("dev")
app.use bodyParser({keepExtensions: true, uploadDir: path.join(global.root, config.get('media_root'))})
#app.use express.urlencoded()
#app.use json()
#app.use express.methodOverride()
app.use cookieParser("^^t$:w<-c9fSK&&YAS3A;)#op-$6nH\)<{)zvtc5{JO6a0j/Z5")
#app.use session(sessionConf)


app.use express.static(path.join(__dirname, "public"))

# mount applications
#app.use require "app/auth/passport_setup"
#app.use '/api/v1'#, require("app/auth")
app.use require "./static_pages"
app.use require "./api"


# TODO: app.configure() has been removed.
# Use process.env.NODE_ENV or app.get('env')
# to detect the environment and configure the app accordingly.

#if process.env.NODE_ENV == 'development'
  #only use in development
#  app.use(errorhandler())


# express app configuration for dev env
#app.configure "development", ->
#app.use errorHandler(
#  dumpExceptions: false
#  showStack: false
#)

# express app configurarion for pro env
#app.configure "production", ->
#  app.use express.errorHandler()


# *******************************************************
start = () ->
  console.log "starting PORT: #{process.env.PORT} | #{config.get('app').port}"
  if process.env.PORT != undefined
    port = process.env.PORT
  else
    port = config.get('app').port
  server = http.createServer(app)
  server.listen(port, () ->
    console.log "Express server listening on port " + port
  )

  #require("app/push-manager").init(server, sessionConf, cookieParser)

exports.start = start
exports.app = app
