HTTPStatus = require "http-status"
config = require('../../../config')
grex = require 'grex'

class UserController
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
    grex.createClient(config.get('titan'))

  detail: (id, cb) =>
    query = @gremlin()
    query  @graph.v(id) #use introspection for type
    # query @graph.commit()
    console.log query()
    @connect().execute(query, cb)

  create: (body, cb) =>
    query = @gremlin()
    query @graph.V("email", body.email)
    @connect().execute(query, (err, response) =>
      if err
        cb(err, response)
      else
        console.log(response)
        query = @gremlin()
        if response.results[0]?
          user = response.results[0]
          if user.VertexType == 'user'
            return cb(err, response)
          else
            # This logic needs to be moved to its own store procedure
            id = user._id
            query @graph.v(id).in('invited')
            @connect().execute(query, (err, response) =>
              if err
               return cb(err, response)
              else
                query = @gremlin()
                network = response.results
                g_user = query.var(@graph.v(id)) # i0
                referral = query.var(@graph.v(network.pop()._id))
                query @graph.v(id).setProperty("VertexType", 'user')
                query @graph.v(id).setProperty("name", body.name)
                query "g.v(#{id}).in('invited').each { g.addEdge(i0, it, 'follow') }" # g_user follow connection
                query "g.v(#{id}).in('invited').each { g.addEdge(it, i0, 'follow') }" # connection follow g_user
                query @graph.addEdge(g_user, referral, 'referred_by')
                query @graph.v(id)
                console.log query()
                @connect().execute(query, cb)
            )
        else
          query  @graph.addVertex({name: body.name, email: body.email, VertexType:'user'}) #use introspection for type
        # query @graph.commit()
          console.log query()
          @connect().execute(query, cb)
    )

  list: (cb) =>
    query = @gremlin()
    query @graph.V("VertexType", "user")
    console.log query()
    @connect().execute(query, cb)

module.exports = UserController