class App.Initialize
  debug: 0
  environment: 'development'

  constructor: (options) ->
    @routes = []
    _.extend @, options
    _.extend @, Backbone.Events

  init: =>
    @user = new App.Models.User($.cookies.get 'user')

    @users = new App.Collections.Users
    @users.push @user

    #TODO: setup sockets here
    @onLoad()

  onLoad: =>
    #TODO rename me
    @me = new App.Models.Me($.cookies.get('user'))
    @me_info = new App.Views.Common.Me.Info({el: '[data-role="me-info"]',model: @me})
    
    #TODO: setup authorization
    user = $.cookies.get 'user'

    # routers
    @routers = []
    @routers.push new App.Routers.Common

    switch window.rootBase
      when "/test-api"
        @routers.push new App.Routers.TestApi()
      else
        @routers.push new App.Routers.Main()

    # events
    @events = new App.Events.Events()

    #TODO: Load xmpp chat?
    @onLoaded()

  onLoaded: =>
    Backbone.history.start({pushState: false, root: window.rootBase})


  loadPage: (View, options) =>
    app.currentView.remove() if app.currentView?
    app.currentView = new View options
    app.currentView.render()

$(document).ready ->
  window.app = new App.Initialize
    debug: 3
    environment: 'development'
  app.init()
