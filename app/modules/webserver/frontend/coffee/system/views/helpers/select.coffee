class System.Views.Helpers.Select extends System.Views.Base
  template: JST['sytem/helpers/select.html']

  initialize: (options) =>
    # need collection and renderOption
    super
    @collection.on 'sync', @addAll, @

  render: () =>
    @collection.fetch()
    @

  addAll: () =>
    @collection.each @addOne

  addOne: (item) =>
    $option = $('<option>')

    option_data = @renderOption(item)
    $option.val(option_data.value)
    $option.html(option_data.text)

    @$el.append $option

  renderOption: (item) =>
    return {
      value: item.id
      text: item.id
    }
