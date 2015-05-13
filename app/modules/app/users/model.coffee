relational = require "../../webserver/managers/relational"

class User extends relational.Model
  tableName: 'user'

  verifyPassword: (password) =>
    console.log "VerifyPassword"
    return true



module.exports = User
