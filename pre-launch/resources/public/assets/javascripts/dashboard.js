function sendMails(){
    console.log("inside send emails");
    var values = $.map($("form#email-form input[type=text]"), function(input){
	return $(input).val();
    }).filter(function(t){
	return t != null && t != "";
    });

    var request = {};
    request['emails'] = values;
    request['__anti-forgery-token'] = $('#__anti-forgery-token').val();
    console.log("forgery token");
    console.log(request['__anti-forgery-token']);

    $.post("/invitation", request, function(data, status){
	console.log("data" + data);
	console.log("status" + status);
	$("form#email-form input[type=text]").each(function(){
	    $(this).val(""); 
	});
    });
}


