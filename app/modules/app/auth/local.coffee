console.log "AUTH LOCAL"
passport = require "passport"

express = require "express"
app = module.exports = express()
colors = require "colors"


Users = require("app/users")
#Devices = require("app/devices")
AuthToken = require("app/auth_token")
LocalStrategy = require('passport-local').Strategy
BearerStrategy = require('passport-http-bearer').Strategy

passport.use(new LocalStrategy(
  {
    usernameField: 'email',
    passwordField: 'password',
    passReqToCallback: true,
  },
  (request, email, password, done) ->
    console.log "LOCAL STRATEGY"
    process.nextTick( () ->
      users = new Users()
      query = {email: email}

      users.get_one(query, (err, user) ->
        console.log user
        if err
          done(err)
        if not user
          done(null, false)
        #if not user.verifyPassword(password)
        #  return done(null, false)

        authToken = new AuthToken()
        authToken.get_or_create user.id, (error, result) ->
          if error
            return done(error, null)
          if result is null
            error = new Error("Something went wrong getting the AuthToken")
            error.status = 500
            return done(error,null)
          else
            #user = user.toJSON()
            delete user.hashed_password
            user.token = result.token
            console.log(user)
            return done(null, user)

      )
    )
  )
)

passport.use new BearerStrategy(
  (token, done) ->
    process.nextTick  () ->
      authToken = new  AuthToken()
      authToken.verify token, (error, token) ->
        if error
          return done(error, false)
        if not token
          return done(null, false)

        users = new Users()
        users.get_one {id: token.user_id}, (err, result) ->
          if err
            return done(err, false)
          if not result
            return done(null, false)
          done(null, result)
)


# passport.ensureAuthenticated = (request, response, next) ->
#   return next() if request.isAuthenticated()
#   response.status(401).json({error: 401, message: "Unauthorized"})


# module.exports = passport

app.post '/auth/local', passport.authenticate('local'), (req, res, next) ->
#app.post '/auth/local', (req, res, next) ->
  console.log "LOCAL"
  # stay logged in for 3 weeks (TODO: Initial guess. Move this to a config file or make it variable)
  if req.params?.stay_login?
    req.session.cookie.expires = false
    req.session.cookie.maxAge = 86400000*21

  res.json req.user


app.get '/auth/token', passport.authenticate('bearer', { session: false }), (req, res, next) ->
  res.json req.user
