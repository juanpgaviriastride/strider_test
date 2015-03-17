class App.Views.TestApi.Users.Form extends Null.Views.Base
  template: JST['app/test_api/users/form.html']

  form: '.signup-form'

  #permission: 'User:create'
  initialize: (options) =>
    super

  events:
    'submit .signup-form': 'saveModel'

  render: () =>
    super
    @

  saveModel: (e) ->
    e.preventDefault()

    data = @getFormInputs $(@form)

    user =
      "email": data.email,
      "username": data.username,
      "password": data.password,

    console.log "User data: ", user
    @collection.create user, {
      success: (model, response) =>
        console.log "Created", model, response
      error: (model, response) =>
        console.log "Error", model, response
      wait: true

    }
