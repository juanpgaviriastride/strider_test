HTTPStatus = require "http-status"
NotModifiedError = require("app/errors").NotModifiedError

module.exports = exports = (schema, options) ->
  # check if document was modified before save
  # add versioning
  schema.pre 'save', (next) ->
    unless @isModified()
      #console.log "NOT MODIFIED!!!"
      next new NotModifiedError()
    @increment()
    next()

  schema.statics.updateOne = (data, callback) ->
    revision = Number data.__v
    id = String data._id
#    console.log "schema-helper updateOne : ", id, data.__v, data
    query = {_id: id}
    query.__v = revision if revision
    @findOne query, (err, doc) ->
#      console.log "schema-helper findOne doc : ", err, doc
      err = err ? {}
      unless doc
        err.status = 426
        return callback(err, doc)

      delete data._id
      delete data.__v

#      console.log " === new data === ", data
      doc.set data
      doc.save (err, saved_doc) ->
        if err instanceof NotModifiedError
          callback null, doc
        else
          callback err, saved_doc

  schema.statics.deleteOne = (query, callback) ->
#    console.log "schema-helper deleteOne : ", query
    @findOne query, (err, doc) ->
      if err
        callback err, {}
      else
        doc.remove (err, result) ->
          callback err, result
