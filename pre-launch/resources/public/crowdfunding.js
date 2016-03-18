//= require vendors/bootstrap.min
//= require vendors/trie
//= require vendors/helper
//= require vendors/jqueryValidation
//= require vendors/selectize
//= require vendors/hello.all


console.log("crowdfunding.js");

var Webtalk = {
  helpers: {}
};

Webtalk.helpers.loginValidation = function (element){
  $(element).validate({
    rules: {
      email: {
        required: true,
        email: true
      },
      password: {
        required: true,
        minlength: 5,
      }
    }
  });
}

Webtalk.helpers.resetValidation = function (element){

  $(element).validate({

    rules: {
      password: {
        required: true,
        minlength: 5
      },
      inputConfirmPassword: {
        required: true,
        minlength: 5,
        equalTo: "#password"
      }
    }
  });
}

Webtalk.helpers.closeAlert = function (clickClose){
  $(clickClose).click(function() {
    $(this).parents(".alert").slideUp(300);
  });
}

Webtalk.helpers.recoveryValidation = function (element){
  $(element).validate({
    email: {
      required: true,
      email: true
    }
  });
}

window.fbAsyncInit = function() {
  FB.init({
    appId      : '1550122381929419',
    xfbml      : true,
    version    : 'v2.5'
  });
};

(function(d, s, id){
      var js, fjs = d.getElementsByTagName(s)[0];
      if (d.getElementById(id)) {return;}
      js = d.createElement(s); js.id = id;
      js.src = "https://connect.facebook.net/en_US/sdk.js";
      fjs.parentNode.insertBefore(js, fjs);
    }(document, 'script', 'facebook-jssdk')
);

Webtalk.helpers.pagerDropdown = function(pagerClick){
  $(pagerClick).click(function(){
    $(pagerClick).toggleClass('open');
  });
}

Webtalk.helpers.newAccountValidation = function (element){
  $(element).validate({
    rules: {
      password: {
        required: true,
        minlength: 5
      },
      inputConfirmPassword: {
        required: true,
        minlength: 5,
        equalTo: "#password"
      },
      inputConfirmEmail: {
        required: true,
        equalTo: "#email"
      }
    }
  });
}

Webtalk.helpers.ShowCsvHelp = function(){
  $(".js-show-help").click(function() {
    $(this).parents(".import-upload-file__import").slideUp(300);
    $(this).parents(".import-upload-file__import").siblings().slideDown(300);
  });
}

Webtalk.helpers.HideCsvHelp = function(){
  $(".js-hide-help").click(function() {
    $(this).parents(".import-upload-file__help").slideUp(300);
    $(this).parents(".import-upload-file__help").siblings().slideDown(300);
  });
}

Webtalk.helpers.noCopyPaste = function(email, password){
  $(email).bind("cut copy paste", function(e){
    e.preventDefault();
  });
  $(password).bind("cut copy paste", function(e){
    e.preventDefault();
  });
}

var recapcha_ok = false;

function onReCapcha(response){
    console.log("the response is" + response);
    recapcha_ok = true;
}

Webtalk.helpers.selectSkin = function(element){
  $(element).selectize({
    readOnly: true,
    onDelete: function() { return false }
  });
}

function login(network){
  hello( network ).login().then(function(){
    // Get Profile
    return hello( network ).api('me');
  }).then(function(p){
    document.getElementById('login').innerHTML = "<img src='"+ p.thumbnail + "' width=24/>Connected to "+ network+" as " + p.name;
  }).then(null, function(e){
    console.error(e);
  });
}

hello.init({
  yahoo: "dj0yJmk9TERadHYyeE5qOG5aJmQ9WVdrOU1uSkdNbGRSTjJVbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xYQ--"
}, {
  redirect_uri: 'https://staging.webtalk.co/',
  oauth_proxy: "http://proxy-env.us-west-1.elasticbeanstalk.com/oauthproxy/",
  scope:"friends"
});


Webtalk.helpers.selectSkin(".js-select");
Webtalk.helpers.noCopyPaste(".js-nocopypaste");
Webtalk.helpers.pagerDropdown(".js-pager-dropdown");
Webtalk.helpers.closeAlert(".js-close-alert");
Webtalk.helpers.loginValidation(".js-loginValidation");
Webtalk.helpers.resetValidation(".js-resetValidation");
Webtalk.helpers.recoveryValidation(".js-recoveryValidation");
Webtalk.helpers.newAccountValidation(".js-newAccountValidation");
Webtalk.helpers.ShowCsvHelp();
Webtalk.helpers.HideCsvHelp();
