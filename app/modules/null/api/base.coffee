async = require("async")
require 'mongoose-pagination'
url_encode = require('form-urlencoded')

class BaseResource
  # model controler class
  controller_class: {}

  # field to be included is defined just return this fields:
  #   e.g: ['email']
  #   will return _id and email fields only
  #
  # if just want exclude fields add "-":
  #   e.g: ['-password']
  # NOTE: you CAN'T mix both

  fields: []
  limit: 20
  page: 1
  pub_redis: false
  sort_field: 'created'
  sort: -1


  # object that contain the field that should be populated
  # to populate a model field you add field name as key and
  # selected fields of that document as array if that array
  # is empty will populate all the fields. ex:
  # get all fields from user
  # populate: {
  #   'user': []
  # }
  #
  # get just the username and full_name
  # populate: {
  #   'user': ['username', 'full_name']
  # }
  #
  # exclude some fields and get the rest
  # populate: {
  #   'user': ['-password', '-emal']
  # }
  populate: {}

  constructor: (options) ->
    @initialize(options)
  ,

  initialize: (options) =>
    @controller = new @controller_class() if @controller_class
    if options and options.app
      @app = options.app
    @fields = @fields.join(" ")
  ,

  ## GET Detail methods
  get_object: (id, callback) =>
    that = @
    @controller.getOne({_id: id}, @fields, @populate, (error, result) ->
      if typeof callback == "function"
        callback.call(that, error, result)
    )
  ,

  detail: (request, response) =>
    that = @
    @request = request
    @response = response
    id = request.params.id

    @get_object(id, (error, result) ->
      return that.dbError(request, response, error) if error
      that.response_detail(result)
    )
  ,

  response_detail: (model) =>
    if model == null
      @NotFound()
    else
      @response.json(model)
  ,

  ## Get list methods
  query_filter: (request) =>
    if request.query.page
      @page = request.query.page
      delete request.query.page
    if request.query.limit
      @limit = request.query.limit
      delete request.query.limit
    #
    if request.query.sort
      @sort = request.query.sort
      delete request.query.sort

    if request.query.sort_field
      @sort_field = request.query.sort_field
      delete request.query.sort_field

    @params = {}
    for key, value of request.query
      try
        @params[key] = JSON.parse(value)
      catch e
        @params[key] = value
    return @params
  ,

  get_queryset: (callback) =>
    that = @

    @controller.filter(@query_filter(@request), @fields, @populate, @page, @limit, @sort_field, @sort, (error, result) ->
      if typeof callback == "function"
        callback.call(that, error, result)
    )
  ,

  list: (request, response) =>
    that = @
    @request = request
    @response = response

    @get_queryset( (error, result) ->
      return that.dbError(request, response, error) if error
      that.response_list(result)

    )
  ,

  response_list: (queryset) =>
    if queryset.queryset == null
      @NotFound()
    else
      response =
        objects: queryset.queryset
        total: queryset.count
        page: @page
        limit: @limit
        next_page: "#{@request.path}?page=#{parseInt(@page) + 1}&limit=#{parseInt(@limit)}&#{url_encode.encode(@params)}"
        previous_page: (if @page > 1 then "#{@request.path}?page=#{parseInt(@page) - 1}&limit=#{parseInt(@limit)}&#{url_encode.encode(@params)}" else null)
      @response.json(response)
  ,

  # Create Methods
  onCreate: (result, callback) =>
    return callback()
  ,
  create_object: (callback) =>
    that = @
    model = @controller.create(@request.body, @request, (err, result) ->
      return callback.call(that, err, null) if err
      async.series({
        onCreate: (callback) ->
          that.onCreate(result, (error, result) ->
            callback(error, result)
          )
        ,
      }, (err, res) ->
        return callback.call(that, err, res) if err
        that.controller.getOne({_id: result._id}, (error, resu) ->
          return callback.call(that, error, resu) if error
          return callback.call(that, null, resu) if typeof callback == "function"
        )
      )
    )
  ,

  create: (request, response) =>
    that = @
    @request = request
    @response = response
    data = @clean_body_create(request)
    if data
      @request.body = data
    else
      return @UnAuthorized()
    @create_object((error, result) ->
      return that.dbError(request, response, error) if error
      that.response_create(result)
    )
  ,

  clean_body_create: (request) ->
    ## to handle authorization on create
    return request.body
  ,

  response_create: (model) =>
    if model == null
      @BadRequest()
    else
      @response.json(model)
  ,

  # Update Metods
  onUpdate: (result, callback) =>
    return callback()
  ,

  update_object: (id, data, callback) =>
    that = @
    data._id = id
    @controller.updateOne(data, @request, (err, result) ->
      return callback.call(that, err, null) if err
      async.series({
        onCreate: (callback) ->
          that.onUpdate(result, (error, result) ->
            callback(error, result)
          )
        ,
      }, (err, res) ->
        return callback.call(that, err, res) if err
        #that.controller.getOne({_id: result._id}, (error, result) ->
        that.get_object(result._id, (error, result) ->
          return callback.call(that, error, result) if error
          return callback.call(that, null, result) if typeof callback == "function"
        )
      )
    )
  ,

  update: (request, response) =>
    that = @
    @request = request
    @response = response
    id = request.params.id
    data = @clean_body_update(request)
    if data
      @request.body = data
    else
      return @UnAuthorized()
    @update_object(id, @request.body, (error, result) ->
      return that.dbError(request, response, error) if error
      that.response_update(result)
    )
  ,

  clean_body_update: (request) ->
    ## to handle authorization on update
    return request.body
  ,

  response_update: (model) =>
    if model == null
      @BadRequest()
    else
      @response.json(model)
  ,

  # delete
  delete_object: (id, callback) =>
    that = @
    @controller.deleteOne({_id: id}, (err, result) ->
      return callback.call(that, err, null) if err
      return callback.call(that, null, null) unless result
      async.series({
        onDelete: (cb) ->
          that.onDelete(result, () ->
            cb(null, 1)
          )
        ,
      }, (err, res) ->
        return callback.call(that, null, result)
      )
    )
  ,

  delete: (request, response) =>
    that = @
    @request = request
    @response = response
    id = request.params.id

    @delete_object(id, (error, result) ->
      return that.dbError(request, response, error) if error
      that.response_delete(result)
    )
  ,

  onDelete: (model, callback) =>
    return callback.call(@)
  ,

  response_delete: (model) =>
    if model == null
      @BadRequest()
    else
      @response.json(model)
  ,

  # Error handlers
  BadRequest: () =>
    err =
      message: "Bad request #{@controller.model.modelName}",
      name: "BadRequest",
      type: "BadRequest",
      value: "400",
      path: ""

    return @response.status(400).json(err)
  ,

  UnAuthorized: () =>
    err =
      message: "UnAuthorized #{@controller.model.modelName}",
      name: "UnAuthorized",
      type: "UnAuthorized",
      value: "401",
      path: ""

    return @response.status(401).json(err)
  ,

  NotFound: () =>
    err =
      message: "Not found #{@controller.model.modelName}",
      name: "NotFound",
      type: "NotFound",
      value: "404",
      path: ""

    return @response.status(404).json(err)
  ,

  dbError: (req, res, err) =>
    console.log err
    if err.name == "ValidationError"
      @validationError(req, res, err)
    else if err.name == "CastError"
      @castError(req, res, err)
    else if err.name == "MongoError" and err.code in [11000, 11001]
      @conflictError(req, res, err)
    else
      return res.status(500).json(err)
  ,

  validationError: (req, res, err) =>
    return res.status(400).json(err)
  ,

  conflictError: (req, res, err) =>
    return res.status(409).json(err)
  ,

  castError: (req, res, err) =>
    if err.type == "ObjectId"
      err.message = "Invalid ID"
      return res.status(400).json(err)

    return res.status(500).json(err)
  ,

module.exports = BaseResource
