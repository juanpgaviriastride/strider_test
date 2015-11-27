function sendMails(){
    console.log("inside send emails");
    var values = $.map($("form#email-form input[type=text]"), function(input){
	return $(input).val();
    }).filter(function(t){
	return t != null && t != "";
    });

    $.post("/invites", {"emails": values}, function(data, status){
	$("form#email-form input[type=text]").each(function(){
	    $(this).val(""); 
	});
    });
}


