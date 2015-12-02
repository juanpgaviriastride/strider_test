var recapcha_ok = false;

function onReCapcha(response){
    console.log("the response is" + response);
    recapcha_ok = true;
}

function onStripeReceive(token){
    console.log("received from stripe");
    console.log(token);
    var request = {};
    request['__anti-forgery-token'] = $('#__anti-forgery-token').val();
    request['payload'] = JSON.stringify(token);
    $(document.body).addClass("loading");
    
    $.post("/stripe/customer", request, function(result, status){
        console.log("saved in backend");
	console.log(result);
	console.log(status);
	if(status == "success"){
	    $('#user_data_form').submit();
	    //$(document.body).removeClass("loading");
	}
    });
}

function openStripe(){
    console.log("inside openStripe")
    if(recapcha_ok){
	var data = {
        key: stripe_key,
        image: "/assets/images/webtalk-bubble.png",
        name: "Webtalk, Inc.",
        description: "$100 Webtalk Credit Purchase",
        token: onStripeReceive,
        amount: "10000",
        locale: "auto"	
	}
    data.email = $('#user_email').val();
	StripeCheckout.open(data);
    }else{
	alert("Please enter reCAPTCHA");
    }
    
}

$(document).ready(function(){
    $('#btn-continue').click(function(){
	openStripe();
    });

});
