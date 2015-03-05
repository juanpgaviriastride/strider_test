HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'

class InvitationsController
  constructor: () ->
    @graph = grex.g
    @gremlin = grex.gremlin

  create_indexes: () =>
    #
    # Wipe graph:
    # g.V.each{g.removeVertex(it)}
    # g.commit()
    #
    # Create index
    # g.makeKey("VertexType").dataType(String.class).indexed(Vertex.class).make()
    # g.commit()
    #
    # Check indexes
    # g.getIndexedKeys(Vertex.class)
    # ==>VertexType
    #
    query = @graph.connect()
    @execute().createKeyIndex(query)

  connect: () =>
    #TODO: move index creation to /lib/db
    grex.createClient(host: "192.168.50.4", graph: "graph", showTypes: yes)

  create: (body, cb) =>
    user_id = body.user_id
    email = body.email

    query = @gremlin()

    query @graph.V("email", email)
    @connect().execute(query, (err, response) =>
      if err
        cb(err, response)
      else
        query = @gremlin()

        user = query.var(@graph.v(user_id))

        invitedUser = yes
        if response.results[0]?
         invitedUser = query.var(@graph.v(response.results[0]._id)) 
        else
          invitedUser = query.var(@graph.addVertex({email: email, VertexType:'invitedUser'}))

        query @graph.addEdge(user, invitedUser, 'invited', {time: Date.now()})
        # query @graph.commit()
        console.log query()
        @connect().execute(query, cb)
     )

  list: (user_id, cb) =>
    query = @gremlin()
    query @graph.v(user_id).out('invited')
    console.log query()
    @connect().execute(query, cb)

  referred: (user_id, cb) =>
    query = @gremlin()
    query @graph.v(user_id).in('referred_by')
    console.log query()
    @connect().execute(query, cb)


module.exports = InvitationsController
