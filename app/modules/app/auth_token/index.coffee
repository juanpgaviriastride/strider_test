module.exports = class AuthTokenController
  get_or_create: (userId, cb) =>
    cb(null, {token: "123456", user_id: "1"})

  verify: (token, cb) =>
    cb(null, {token: "123456", user_id: "1"})
