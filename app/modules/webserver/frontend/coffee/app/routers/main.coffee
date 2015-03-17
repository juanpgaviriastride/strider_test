class App.Routers.Main extends App.Routers.Base

  routes:
    '': 'index'
    'users': 'users'
    'entries': 'entries'
    'follows': 'follows'
    'timeline': 'timeline'

  index: =>
    console.log 'Welcome to index'
  users: =>
    console.log 'Welcome to users'
  entries: =>
    console.log 'Welcome to entries'
  follows: =>
    console.log 'Welcome to follows'
  timeline: =>
    console.log 'Welcome to timeline'
