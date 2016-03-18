var recapcha_ok = false;

function onReCapcha(response){
    console.log("the response is " + response);
    recapcha_ok = true;
    $( '#reCaptchaError' ).html( '<div class="error" id="reCaptchaError" style="color:red"></div>' );
    console.log(document.getElementById("stripe-logo-wt").src);
}

function onExpires(response){
    recapcha_ok = false;
}

function onStripeReceive(token){
    $('.spinner-container').addClass('show');
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
    	}else{
            $('.spinner-container').removeClass('show');
            alert("We are sorry, something went wrong, please reload page and start again");
        }
    }).fail(function(result){
        $('.spinner-container').removeClass('show');
        var objError = jQuery.parseJSON(result.responseText);
        alert( objError.message + " Please try again with another card." ); 
        openStripe(); 
    });
}

function openStripe(){
    if($('#user_data_form').valid()){
        if(recapcha_ok){
                console.log("inside openStripe")
        	var data = {
                key: stripe_key,
                image: document.getElementById("stripe-logo-wt").src,
                name: "Webtalk, Inc.",
                description: "$100 Webtalk Credit Purchase",
                token: onStripeReceive,
                amount: "10000",
                locale: "auto"	
        	}
            data.email = $('#email').val();
            StripeCheckout.open(data);
        }else{
            console.log("bad captcha")
            $( '#reCaptchaError' ).html( '<div class="error" id="reCaptchaError" style="display: block;color:red">Please verify you are human!!!</div>' );
        }
    }else{
        console.log("bad fields")
        if(!recapcha_ok){
            $( '#reCaptchaError' ).html( '<div class="error" id="reCaptchaError" style="display: block;color:red">Please verify you are human!!!</div>' );
        }
    }
    
}

$(document).ready(function(){
    $('#btn-continue').click(function(){
        openStripe();
    });

    $("#email").blur(function(){
        if($('#email').valid()){
            userEmail = $('#email').val()
            $.get( "/user/"+userEmail, function( data ) {
                if(data.exists){
                    alert( "Sorry, that email has been already used.");
                    $('#email').val("");
                    $('#inputConfirmEmail').val("");
                    $('#email').focus();
                }
            });
        }
    });
});
