config = require('../config')
redis = require("redis")

redis_options = {
  auth_pass: config.get('redis').options.auth
}

# store = redis.createClient(config.get('redis').port, config.get('redis').host, redis_options)
# pub = redis.createClient(config.get('redis').port, config.get('redis').host, redis_options)
# sub = redis.createClient(config.get('redis').port, config.get('redis').host, redis_options)

store = redis.createClient(config.get('redis').port, config.get('redis').host)
pub = redis.createClient(config.get('redis').port, config.get('redis').host)
sub = redis.createClient(config.get('redis').port, config.get('redis').host)

console.log config.get('redis').port, config.get('redis').host
store.auth(redis_options.auth_pass)
pub.auth(redis_options.auth_pass)
sub.auth(redis_options.auth_pass)

# set store db to 1
store.select(1, () ->
  console.log("Redis Store Connected")
)
store.on("error",  (err) ->
  console.log("Redis Store Error: " + err)
)

pub.on("error",  (err) ->
  console.log("Redis PUB Error: " + err)
)

sub.on("error",  (err) ->
  console.log("Redis SUB Error: " + err)
)

module.exports.store = store
module.exports.pub = pub
module.exports.sub = sub
