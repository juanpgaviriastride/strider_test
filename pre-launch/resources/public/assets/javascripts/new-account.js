function onStripeReceive(token){
    console.log("received from stripe");
    console.log(token);
    request = {};
    request['__anti-forgery-token'] = $('#__anti-forgery-token').val();
    request['payload'] = JSON.stringify(token);
    
    $.post("/stripe/customer", request, function(result, status){
        console.log("saved in backend");
	console.log(result);
	console.log(status);
	if(status == "success"){
	    $('#user_data_form').submit();
	}
    });
}

function openStripe(){
    var data = {
        key: stripe_key,
        image: "/assets/images/webtalk-bubble.png",
        name: "Webtalk, Inc.",
        description: "2 widgets",
        token: onStripeReceive,
        amount: "10000",
        locale: "auto"	
    }
    data.email = $('#user_email').val();
    StripeCheckout.open(data);
}

$(document).ready(function(){
    $('#stripe').click(function(){
	openStripe();
    });

});
