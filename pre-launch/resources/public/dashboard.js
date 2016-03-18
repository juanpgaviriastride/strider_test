console.log("dashboard.js");
/**
** CSV Parser class
**/
CSVParser = function (options){
  this.options = {
    el: null,
    source: null,
    success: null,
    error: null
  }
  _.extend(this.options, options)

  this.$element = $(this.options.el)

  this.$element.change($.proxy(this.onChange, this))

  return this;
}

CSVParser.prototype.onChange = function (event) {
  this.parse();
}


CSVParser.prototype.parse = function () {
  var that = this
  var config = {
    // base config to use for each file
    //delimiter: ",",
    header: false,
    error: function(error){
      if (that.options.error) that.options.error(results);
      that.errors = error
      return
    },
    complete: function(results, file) {
      that.results = results

      if (results.errors.length > 0){
        that.errors = results.errors
      }
      if (that.options.success) 
        that.options.success(results);
      return
    }
  }
  if(userEmail != "")
  {
    var domain = userEmail.split("@")[1];
    if (domain != "webtalk.co" &&
        domain != "webtalk.org" && 
        domain != "nullindustries.co" ){
        config.preview = 1000;
        console.log("limited on: 1000 emails only");
    }else{
        console.log("ulimited on!!!");
    }
  }
  
  this.$element.parse({
    config: config,
    before: function(file, inputElem)
    {
      // executed before parsing each file begins;
      // what you return here controls the flow
      return
    },
    error: function(err, file, inputElem, reason)
    {
      // executed if an error occurs while loading the file,
      // or if before callback aborted for some reason
      console.log("Error while loading: "+err)
    },
    complete: function()
    {
      // executed after all files are complete
    }
  });
}

/**
** GetEmails
**/

GetEmails = function (data) {
  this.options = {
    objects: data,
    emails: []
  }

  this.previuos_email_index = []

  _.bindAll(this,
    'findEmails',
    'readItem',
    'emailMatch'
  )
  // _.extend(this.options, options)
  this.findEmails()
  return this
}

GetEmails.prototype.findEmails = function() {
  this.emails = _.filter(_.map(this.options.objects, this.readItem), this.emailMatch)
  return this.emails
}

GetEmails.prototype.readItem = function(item) {
    var email, index;
  if (this.email_index == undefined){
    index = _.findIndex(item, this.emailMatch);
    if(index >= 0) {
      this.email_index = index;
    }else{
      return ''
    }
  }
    email = item[this.email_index];
  if (!this.emailMatch(email)){
    email = undefined
    for (var i=0; i < this.previuos_email_index; i++){
      if (this.emailMatch(item[this.previuos_email_index[i]])){
        email = item[this.previuos_email_index[i]]
        break;
      }
    }
    if(!email){
      this.previuos_email_index.push(this.email_index);
      this.email_index = _.findIndex(item, this.emailMatch);
      email = item[this.email_index]
    }
    return email
  }else{
    return email
  }
}

GetEmails.prototype.emailMatch = function (item) {
  re = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
  return re.test(item)
}

function bulkEmail(emails){
    var request = {};
    request['emails'] = emails;
    request['custom_message'] = $('#custom_message_field').val();
    console.log("anti forgery");
    console.log("emails: " + emails);
    request['__anti-forgery-token'] = $('#__anti-forgery-token').val();
    console.log(request);
    $.post("/invitation", request, function(data, status){
      $('#modal-invitation').modal()
      $("form#email-form input[type=text]").each(function(){
        $(this).val("");
      });
    });
}

function sendMails(){
    var values = $.map($("form#email-form input[type=text]"), function(input){
	return $(input).val();
    }).filter(function(t){
	return t != null && t != "";
    });
    bulkEmail(values);
}

$(document).ready(function (){
  csv = new CSVParser({
    el: $('#csv-file'),
    success: function(result) {
	emails = new GetEmails(result.data);
	console.log("EMAIL: ", emails.emails);
	bulkEmail(emails.emails);
    },
    error: function(error) {
      console.log(">>>>> SE TOSTO", error)
    }
  })

  $('[data-role="send-invitations"]').click(function (){
    console.log('send invitationts')
    sendMails();
  });

  // paginate invitations
  $.ajax({
    url: '/dashboard/network-detail',
    method: 'get',
    dataType: 'json',
    success: function(data, xhr){
      console.log('JSON:', data)
      invitations = new invitationsCollection(data)
      invitations.sort()
      invitations_view = new InvitationTable({collection: invitations})
    },
    error: function(xhr, errorThrow, errorText) {
      console.log('ERROR:', arguments)
    }
  })
});

function auth() {
  var config = {
    'client_id': '309918129258-fio85ote9d92ld4qpru4k15njebc8nt2.apps.googleusercontent.com',
    'scope': 'https://www.google.com/m8/feeds'
  };
  gapi.auth.authorize(config, function() {
    fetch(gapi.auth.getToken());
  });
}

function fetch(authResult) {

  $.getJSON('https://www.google.com/m8/feeds/contacts/default/full/?access_token=' + 
    authResult.access_token + "&max-results=900&alt=json&callback=?", function(result){
      window.result = result;
      emails= _.reject(result.feed.entry, function(item){
        primary_email =  _.find(item.gd$email, function(email){ return email.primary == "true" })
          if(!primary_email){
            return true;
          }
      });
      data = _.map(emails, function(item){ return item.gd$email[0].address });
      console.log("DATA GOOGLE: ",data);
      bulkEmail(data);
    });

}

function authOutlook(){
    //live.com api
  
  WL.login({
    scope: ["wl.basic", "wl.contacts_emails"]
  }).then(function (response) {
  WL.api({
      path: "me/contacts",
      method: "GET"
    }).then(
      function (response) {
        //your response data with contacts 
        var emailsOutlook = [];
        console.log(response.data);
        $.each(response.data, function(i, val){
          emailsOutlook[i] = val.emails.preferred;
        });
        bulkEmail(emailsOutlook);
      },
      function (responseFailed) {
        console.log("Error signing in api: " + responseFailed);
      }
    );
  },
  function (responseFailed) 
  {
    console.log("Error signing in: " + responseFailed.error_description);
  });
}

$(function() {
  var username = $("#user-name").text()
  $("#modal-user-name").text(username)

  $( "#custom_message_field" ).keyup(function() {
    var textareVal = $(this).val();
    $("#modal-message").text(textareVal);
  });
});


