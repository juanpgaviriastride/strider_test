var recapcha_ok = false;

function onReCapcha(response){
    console.log("the response is" + response);
    recapcha_ok = true;
    $( '#reCaptchaError' ).html( '<div class="error" id="reCaptchaError" style="color:red"></div>' );
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
        alert("We are sorry, something went wronf, please reload page and start again");
    }
    });
}

function openStripe(){
    if($('#user_data_form').valid()){
        if(recapcha_ok){
                console.log("inside openStripe")
        	var data = {
                key: stripe_key,
                image: "/assets/images/webtalk-bubble.png",
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

});
