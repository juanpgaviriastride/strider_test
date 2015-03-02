#
# Configuration Manager
#
# This config manager uses nconf to handle hirerchical priority for config sources.
# see: https://github.com/flatiron/nconf
#

path = require 'path'
config = require 'nconf'

## Load args and env variables
config.argv().env()

## load env json file
env = (if config.get('NODE_ENV') then config.get('NODE_ENV') else 'dev')
if env in ['dev', 'test', 'integration','staging', 'prod']
  config.overrides(require("./envs/#{env}"))
else
  env = 'dev'
  config.use('file', {file: path.join(__dirname, "envs/dev.json")})

config.env = env
console.log "Runing in #{config.env} mode"

module.exports = config