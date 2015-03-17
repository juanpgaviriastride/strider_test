#config = require('../../config/config')[global.env]
config = require '../../../config'
pkg  = require '../../../package.json'
cons = require "consolidate"
swig = require "swig"

express = require "express"
app = module.exports = express()

# Template engine
#swigHelpers = require('./helpers')
#swigHelpers(swig)


swig.setDefaults({ cache: false })
app.engine('html', swig.renderFile)

#Job = require "null/jobs"


app.set('view engine', 'html')
app.set('views', "#{global.root}/modules/webserver/views")
app.set('view cache', false)

# routers
app.get '/', (req, res) ->
  debug = (if req.query.debug then req.query.debug else config.get('debug'))
  res.render "landing/index.html", {debug: debug}


app.get '/login', (req, res) ->
  debug = (if req.query.debug then req.query.debug else config.get('debug'))

  if req.isAuthenticated()
    debug = (if req.query.debug then req.query.debug else config.get('debug'))
    res.redirect "/test-api"
    #res.render "test-api.html", {rootBase: '/test-api', address: "https://#{config.get('app').host}:#{config.get('app').port}", debug: debug}
  else
    #console.log "Redirect non auth user to login page"
    #res.redirect "/login"
    debug = (if req.query.debug then req.query.debug else config.get('debug'))
    res.render "login.html", {debug: debug}


app.get '/test-api', (req, res) ->
  debug = (if req.query.debug then req.query.debug else config.get('debug'))

  res.render "test-api.html", {rootBase: '/test-api', address: "https://#{config.get('app').host}:#{config.get('app').port}", debug: debug}

  # TODO: authentication
  # if req.isAuthenticated()
  #   debug = (if req.query.debug then req.query.debug else config.get('debug'))
  #   res.render "test-api.html", {rootBase: '/test-api', address: "https://#{config.get('app').host}:#{config.get('app').port}", debug: debug}
  # else
  #   #console.log "Redirect non auth user to login page"
  #   #res.redirect "/login"
  #   debug = (if req.query.debug then req.query.debug else config.get('debug'))
  #   res.render "login.html", {debug: debug}


app.get '/404', (req, res) ->
  res.render "404.html"
