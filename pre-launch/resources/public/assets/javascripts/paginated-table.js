var invitations;

var InvitationTable = Backbone.View.extend({
  initialize: function (){
    Backbone.View.prototype.initialize.call(this)
    _.bindAll(this,
      'render',
      'nextPage',
      'goTo'
    )
    this.size = 5
    this.page = 1

    this.rows = []

    this.pages_view = new pagerView({
      parent: this,
      collection: this.collection,
      el: $('[data-role="pager-view"]')
    })

    this.render()
    return this
  },

  render: function (){

    this.goTo(this.page)
    this.controlls()

    return this
  },

  controlls: function (){
    this.pages_view.render()
  },

  getContext: function () {
    return
  },

  nextPage: function (){

  },

  prePage: function () {

  },

  goTo: function (page) {
    this.cleanRows()
    this.page = page
    this.controlls()
    var index = (parseInt(page) - 1) * this.size
    for (var i =0; i < this.size; i++){
      if (this.collection.at(index + i)){
        var model = this.collection.at(index + i)
        var row = new InvitationItemView({model: model})
        this.rows.push(row)
      }else{
        break;
      }
    }
  },

  addRow: function() {

  },

  cleanRows: function() {
    _.each(this.row, function (row){
      row.remove()
    })
    $('[data-role="table-col-name"]').html('')
    $('[data-role="table-col-date"]').html('')
    $('[data-role="table-col-status"]').html('')

  }
})

var InvitationItemView = Backbone.View.extend({
  initialize: function (){
    Backbone.View.prototype.initialize.call(this)
    _.bindAll(this,
      'render',
      'getContext'
    )
    this.$name = null
    this.$date = null
    this.$status = null
    this.render()
    return this
  },

  render: function (){
    this.$name = _.template($('#table-col-name-template').html())(this.getContext())
    this.$date = _.template($('#table-col-date-template').html())(this.getContext())
    this.$status = _.template($('#table-col-status-template').html())(this.getContext())

    $('[data-role="table-col-name"]').append(this.$name)
    $('[data-role="table-col-date"]').append(this.$date)
    $('[data-role="table-col-status"]').append(this.$status)

    return this
  },

  getContext: function () {
    return {model: this.model}
  }
});

var pagerView = Backbone.View.extend({
  initialize: function (options){
    Backbone.View.prototype.initialize.call(this)
    _.bindAll(this,
      'render',
      'getContext',
      'addAll',
      'addPage',
      'addMorePage'
    )
    this.parent = options.parent
    this.pages_to_show = 3

    return this
  },

  events: {
    'click [data-role=first]': 'goToFirst',
    'click [data-role=last]': 'goToLast'
  },

  render: function (){
    this.page = this.parent.page
    this.size = this.parent.size
    this.pages = Math.ceil(this.collection.length/this.size)

    this.$el.html(_.template($('#table-pager-template').html())(this.getContext()))
    this.addAll()
    return this
  },

  getContext: function () {
    return {}
  },

  goToFirst: function(event) {
    event.preventDefault();
    this.parent.goTo(1)
  },

  goToLast: function(event) {
    event.preventDefault();
    this.parent.goTo(this.pages)
  },

  addAll: function () {
    var pages = []
    var more_beging = false
    var mode_end = false
    if (this.collection.lenght <= this.size){
      pages.push(1)
    }else if (this.pages <= this.pages_to_show){
      $('[data-role="first"]', this.$el).hide()
      $('[data-role="last"]', this.$el).hide()
      _.each(_.range(this.pages), function(page){
        pages.push(page+1)
      })
    }else{
      start_range = _.range(1, this.pages_to_show + 1)
      end_range = _.range(this.pages - this.pages_to_show +1, this.pages + 1)

      if (_.indexOf(start_range, this.page) >= 0){
        $('[data-role="first"]', this.$el).hide()
        $('[data-role="last"]', this.$el).show()
        more_beging = false
        mode_end = true
        _.each(start_range, function(page){
          pages.push(page)
        })

      }else if (_.indexOf(end_range, this.page) >= 0) {
        $('[data-role="first"]', this.$el).show()
        $('[data-role="last"]', this.$el).hide()
        more_beging = true
        mode_end = false
        _.each(end_range, function(page){
          pages.push(page)
        })
      }else{
        $('[data-role="first"]', this.$el).show()
        $('[data-role="last"]', this.$el).show()
        more_beging = true
        mode_end = true
        _.each(_.range(this.page, this.page + this.pages_to_show), function(page){
          pages.push(page)
        })
      }
    }
    if (more_beging) {
      this.addMorePage(_.first(pages) - 1)
    }
    _.each(pages, this.addPage)
    if (mode_end) {
      this.addMorePage(_.last(pages) + 1)
    }
  },

  addPage: function (page) {
    page = new pageView({
      page: page,
      parent: this
    })
    $('[data-role="pages-items"]', this.$el).append(page.render().el)
  },

  addMorePage: function (page) {
    page = new morePageView({
      page: page,
      parent: this
    })
    $('[data-role="pages-items"]', this.$el).append(page.render().el)
  },

  goTo: function(page) {
    this.parent.goTo(page)
  }

});


var pageView = Backbone.View.extend({
  tagName: 'li',

  initialize: function (options){
    Backbone.View.prototype.initialize.call(this)
    _.bindAll(this,
      'render',
      'getContext',
      'onClickHandler'
    )
    this.page = options.page
    this.parent = options.parent

    return this
  },

  events: {
    'click': 'onClickHandler'
  },

  render: function (){
    this.$el.html(_.template($('#table-page-template').html())(this.getContext()))
    return this
  },

  getContext: function () {
    return {page: this.page }
  },

  onClickHandler: function (event) {
    event.preventDefault()
    this.parent.goTo(this.page)
  }
});


var morePageView = pageView.extend({
  tagName: 'li',

  render: function (){
    this.$el.html(_.template($('#table-more-page-template').html())(this.getContext()))
    return this
  },

});
