class System.Routers.Base extends Backbone.Router
  initialize: (options) =>
    @bind "all", @_change, @

  deselectNav: () =>
    $("*[data-href]").removeClass('active')

  selectNav: (href) =>
    @deselectNav()
    $("*[data-href='#{href}']").addClass('active')
