function onStripeReceive(token){
    console.log("received from stripe");
    console.log(token);
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
