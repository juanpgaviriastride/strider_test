module.exports = {
  "debug": true
  "auth_token_secret": "",
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
    "db": ""
  },
  "redis": {
    "host": "localhost",
    "port": 6379,
    "options": {
      "auth": ""
    }
  },
  "mail": {
    "apiUser" : "",
    "apiKey"  : ""
  }
  "titan": {
    "host": "52.11.110.132",
    "graph": "graph",
    "showTypes": yes
  }
}
