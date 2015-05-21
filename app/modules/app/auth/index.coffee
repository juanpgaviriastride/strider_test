console.log "AUTH INDEX"
passport = require "passport"
UsersController = require "app/users"

express = require "express"
app = module.exports = express()

app.use require "./passport_setup"

# mount required passport strateges
app.use require "./local"
#app.use require "./google"

app.get '/logout', (req, res) ->
  users = new UsersController();
  if not req.user? then return res.json()
  req.logout()
  res.json({status: 200})


module.exports = app
module.exports.passport = passport
