class App.Views.Common.DeleteConfirmation extends Null.Views.Base
  template: JST['app/common/delete_confirmation.html']

  initialize: (options) =>
    super
    @

  events:
    'click [data-role=delete]': 'deleteItem'
    'click [data-role=close]': 'hide'

  render: () =>
    super
    $('.modal', @$el).on 'hidden.bs.modal', @remove
    @show()
    @

  getContext: () =>
    return {model: @model}

  show: () ->
    console.log "SHOW"
    $('.modal', @$el).modal('show')

  hide: () ->
    console.log "HIDE"
    $('.modal', @$el).modal('hide')

  deleteItem: (e) ->
    e.preventDefault()
    console.log "DELETEING!!"
    @model.destroy()
    @hide()
