class System.Views.Base extends Backbone.View
  # tempalte example
  # template: JST['app/test/test.html']

  initialize: (options) =>
    super
    @options = options
    @template = @options.template if @options?.template?
    @__appendedViews = new Backbone.ChildViewContainer()

  getContext: () =>
    return {}
  ,

  render: () =>
    @$el.html(@template(@getContext()))
    return @
  ,
  removeAll: () =>
    @undelegateEvents()
    @unbind()
    @$el.remove()
    @__appendedViews.call('removeAll')
  ,

  remove: () =>
    @undelegateEvents()
    @unbind()
    @$el.html("")
  ,
  #
  __add: (view, destination = '', fn = 'append') =>
    $destination = @__detectDestination destination
    @__appendedViews.add view
    view._parent = @

    $destination[fn] view.$el

    if $('body').find($destination).length
      view.trigger 'ready', view
      view.rendered = yes
    else
      @on 'ready', =>
        view.trigger 'ready', view
        view.rendered = yes

    view.on 'remove', =>
      index =  _.indexOf @__appendedViews, view
      if index > -1
        @__appendedViews.splice index, 1
    @

  __detectDestination: (destination) =>
    destination or= ''
    if destination instanceof jQuery
      $destination = destination
    else
      $destination = if destination and @find(destination).length > 0 then @find(destination) else @$el
    $destination

  prependView:(view, destination = '') =>
    @__add view, destination, 'prepend'

  appendView: (view, destination = '') =>
    @__add view, destination

  getFormInputs: (element, exclude) =>
    unless element
      element = $("form", @$el)
    unless exclude
      exclude = []

    form_elements = {}

    _.each($("input[type=text]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=checkbox]:checked", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=radio]:checked", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=file]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=password]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=date]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=time]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("input[type=email]", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("textarea", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    _.each($("select", element), (input) =>
      return if $(input).attr("id") in exclude
      form_elements[$(input).attr("id")] = $(input).val()
    )

    return form_elements
  ,

  formErrors: (element, errors) =>
    $(".has-error", element).removeClass("has-error")
    $(".error-msg", element).remove()
    _.each(errors, (msg,id) =>
      $("##{id}", element).parents(".form-group").addClass("has-error")
      $("##{id}", element).parents(".form-group").children(".control-label").after("<label class='control-label error-msg'> (#{msg})</label>")
    )
  ,

  # Dragging events from child to parent
  fire: (name, params...) =>
    proxyEvent = new ProxyEvent @, name
    @_fireEvent proxyEvent, params...

  _fireEvent: (proxyEvent, params...)=>
    @trigger proxyEvent.name, proxyEvent, params...

    unless proxyEvent.isStopped()
      @_parent?._fireEvent proxyEvent, params...



_.extend System.Views.Base, Backbone.Events

# Proxy jQuery methods
_.each ['find', 'addClass', 'attr', 'removeClass', 'fadeIn', 'fadeOut', 'effect', 'css', 'append', 'prepend', 'prop', 'is', 'blur'], (method) =>
  System.Views.Base::[method] = (args...) ->
    @$el[method].apply @$el, args
