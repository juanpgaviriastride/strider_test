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
  this.$element.parse({
    config: {
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
        if (that.options.success) that.options.success(results);
        return
      }
    },
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
    console.log("anti forgery");
    console.log("emails" + emails);
    request['__anti-forgery-token'] = $('#__anti-forgery-token').val();
    $.post("/invitation", request, function(data, status){
	    console.log("data" + data);
	    console.log("status" + status);
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
	console.log("EMAIL:", emails.emails);
	bulkEmail(emails.emails);
    },
    error: function(error) {
      console.log(">>>>> SE TOSTO", error)
    }
  })
});


