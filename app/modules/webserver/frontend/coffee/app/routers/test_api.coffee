class App.Routers.TestApi extends App.Routers.Base

  routes:
    '': 'users'
    'entries': 'entries'
    'follows': 'follows'
    'timeline': 'timeline'

  # Link component to reuse
  # TODO: Use react
  $a = $("<a>")
  $a.attr('href', "")
  $a.data('role', 'route')

  users: =>
    @selectNav('users')
    $a.html("Users")
    $('[data-role="breadcrum"]').html($a)
    app.loadPage App.Views.TestApi.Users.Index, {el: "[data-role=main]"}

  entries: =>
    console.log 'Welcome to entries'
  follows: =>
    console.log 'Welcome to follows'
  timeline: =>
    console.log 'Welcome to timeline'
