(function(){var t,e;t=this.jQuery||window.jQuery,e=t(window),t.fn.stick_in_parent=function(i){var s,n,o,r,a,l,h,c,d,u,p;for(null==i&&(i={}),p=i.sticky_class,a=i.inner_scrolling,u=i.recalc_every,d=i.parent,c=i.offset_top,h=i.spacer,n=i.bottoming,null==c&&(c=0),null==d&&(d=void 0),null==a&&(a=!0),null==p&&(p="is_stuck"),s=t(document),null==n&&(n=!0),o=function(i,o,r,l,f,g,m,v){var b,y,_,w,x,C,k,T,S,D,$,I;if(!i.data("sticky_kit")){if(i.data("sticky_kit",!0),x=s.height(),k=i.parent(),null!=d&&(k=k.closest(d)),!k.length)throw"failed to find stick parent";if(b=_=!1,($=null!=h?h&&i.closest(h):t("<div />"))&&$.css("position",i.css("position")),T=function(){var t,e,n;return!v&&(x=s.height(),t=parseInt(k.css("border-top-width"),10),e=parseInt(k.css("padding-top"),10),o=parseInt(k.css("padding-bottom"),10),r=k.offset().top+t+e,l=k.height(),_&&(b=_=!1,null==h&&(i.insertAfter($),$.detach()),i.css({position:"",top:"",width:"",bottom:""}).removeClass(p),n=!0),f=i.offset().top-(parseInt(i.css("margin-top"),10)||0)-c,g=i.outerHeight(!0),m=i.css("float"),$&&$.css({width:i.outerWidth(!0),height:g,display:i.css("display"),"vertical-align":i.css("vertical-align"),"float":m}),n)?I():void 0},T(),g!==l)return w=void 0,C=c,D=u,I=function(){var t,d,y,S;return!v&&(y=!1,null!=D&&(--D,0>=D&&(D=u,T(),y=!0)),y||s.height()===x||T(),y=e.scrollTop(),null!=w&&(d=y-w),w=y,_?(n&&(S=y+g+C>l+r,b&&!S&&(b=!1,i.css({position:"fixed",bottom:"",top:C}).trigger("sticky_kit:unbottom"))),f>y&&(_=!1,C=c,null==h&&("left"!==m&&"right"!==m||i.insertAfter($),$.detach()),t={position:"",width:"",top:""},i.css(t).removeClass(p).trigger("sticky_kit:unstick")),a&&(t=e.height(),g+c>t&&!b&&(C-=d,C=Math.max(t-g,C),C=Math.min(c,C),_&&i.css({top:C+"px"})))):y>f&&(_=!0,t={position:"fixed",top:C},t.width="border-box"===i.css("box-sizing")?i.outerWidth()+"px":i.width()+"px",i.css(t).addClass(p),null==h&&(i.after($),"left"!==m&&"right"!==m||$.append(i)),i.trigger("sticky_kit:stick")),_&&n&&(null==S&&(S=y+g+C>l+r),!b&&S))?(b=!0,"static"===k.css("position")&&k.css({position:"relative"}),i.css({position:"absolute",bottom:o,top:"auto"}).trigger("sticky_kit:bottom")):void 0},S=function(){return T(),I()},y=function(){return v=!0,e.off("touchmove",I),e.off("scroll",I),e.off("resize",S),t(document.body).off("sticky_kit:recalc",S),i.off("sticky_kit:detach",y),i.removeData("sticky_kit"),i.css({position:"",bottom:"",top:"",width:""}),k.position("position",""),_?(null==h&&("left"!==m&&"right"!==m||i.insertAfter($),$.remove()),i.removeClass(p)):void 0},e.on("touchmove",I),e.on("scroll",I),e.on("resize",S),t(document.body).on("sticky_kit:recalc",S),i.on("sticky_kit:detach",y),setTimeout(I,0)}},r=0,l=this.length;l>r;r++)i=this[r],o(t(i));return this}}).call(this);