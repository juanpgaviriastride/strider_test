class System.Collections.Base extends Backbone.Collection
  sync: System.Sync
  parse: (results) ->
    @page = parseInt(results.page)
    @limit = parseInt(results.limit)
    @next_page = results.next_page
    @previous_page = results.previous_page
    return results.objects
  ,
  resetPaging: () ->
    @page = 1
    @limit = undefined
  ,

  nextPage: (options) ->
    unless options
      options = {}

    @filters.page = @page + 1
    @filters.limit = @limits
    options.data = @filters
    options.remove = false
    @fetch(options)
  ,

  filtrate: (data) ->
    unless data
      data = {data:{}}
    @filters = data.data
    @filters.page = @page
    @filters.limit = @limit
    @fetch(data)
  ,
