express = require "express"
http = require "http"
path = require "path"
fs = require "fs"


global.root = __dirname

webserver = require "webserver"
webserver.start()

#services = require "services"
