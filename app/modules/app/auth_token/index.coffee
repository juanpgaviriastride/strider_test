module.exports = class AuthTokenController
  get_or_create: (userId, cb) =>
    cb(null, {token: "eown5ZpexLDSOjIN6FZDyBfUcVYRQB98vEmGuNvqRbVvdrR0wijGdog5rG2BetjpTE6msZ7gXWpqDPi6YIWAnHKEPAenJwKV0dhN7gYLk6bEWwxI79WSDTsMKH1hNNsB", user_id: "1"})

  verify: (token, cb) =>
    cb(null, {token: "eown5ZpexLDSOjIN6FZDyBfUcVYRQB98vEmGuNvqRbVvdrR0wijGdog5rG2BetjpTE6msZ7gXWpqDPi6YIWAnHKEPAenJwKV0dhN7gYLk6bEWwxI79WSDTsMKH1hNNsB", user_id: "1"})
