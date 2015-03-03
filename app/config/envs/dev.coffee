module.exports = {
  "debug": true
  "auth_token_secret": "R",
  "app": {
    "baseUrl": "http://localhost:3000/"
    "host": "localhost",
    "port": 3000
  },
  "media_root": 'modules/webserver/public/userdata',
  "media_url": 'userdata',
  "mongo": {
    "host": "192.168.50.4",
    "port": 27017,
    "db": "webtalk_dev"
  },
  "redis": {
    "host": "192.168.50.4",
    "port": 6379,
    "options": {
      "auth": ""
    }
  },
  "mail": {
    "apiUser" : "",
    "apiKey"  : ""
  },
  "titan": {
    "host": "192.168.50.4",
    "graph": "graph",
    "showTypes": yes
  }

}
