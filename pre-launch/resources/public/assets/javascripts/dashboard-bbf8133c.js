function bulkEmail(t){var e={};e.emails=t,console.log("anti forgery"),console.log("emails"+t),e["__anti-forgery-token"]=$("#__anti-forgery-token").val(),$.post("/invitation",e,function(){$("#modal-invitation").modal(),$("form#email-form input[type=text]").each(function(){$(this).val("")})})}function sendMails(){var t=$.map($("form#email-form input[type=text]"),function(t){return $(t).val()}).filter(function(t){return null!=t&&""!=t});bulkEmail(t)}CSVParser=function(t){return this.options={el:null,source:null,success:null,error:null},_.extend(this.options,t),this.$element=$(this.options.el),this.$element.change($.proxy(this.onChange,this)),this},CSVParser.prototype.onChange=function(){this.parse()},CSVParser.prototype.parse=function(){var t=this;this.$element.parse({config:{header:!1,error:function(e){t.options.error&&t.options.error(results),t.errors=e},complete:function(e){t.results=e,e.errors.length>0&&(t.errors=e.errors),t.options.success&&t.options.success(e)}},before:function(){},error:function(){},complete:function(){}})},GetEmails=function(t){return this.options={objects:t,emails:[]},this.previuos_email_index=[],_.bindAll(this,"findEmails","readItem","emailMatch"),this.findEmails(),this},GetEmails.prototype.findEmails=function(){return this.emails=_.filter(_.map(this.options.objects,this.readItem),this.emailMatch),this.emails},GetEmails.prototype.readItem=function(t){var e,i;if(void 0==this.email_index){if(i=_.findIndex(t,this.emailMatch),!(i>=0))return"";this.email_index=i}if(e=t[this.email_index],this.emailMatch(e))return e;e=void 0;for(var s=0;s<this.previuos_email_index;s++)if(this.emailMatch(t[this.previuos_email_index[s]])){e=t[this.previuos_email_index[s]];break}return e||(this.previuos_email_index.push(this.email_index),this.email_index=_.findIndex(t,this.emailMatch),e=t[this.email_index]),e},GetEmails.prototype.emailMatch=function(t){return re=/^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i,re.test(t)},$(document).ready(function(){csv=new CSVParser({el:$("#csv-file"),success:function(t){emails=new GetEmails(t.data),console.log("EMAIL:",emails.emails),bulkEmail(emails.emails)},error:function(t){console.log(">>>>> SE TOSTO",t)}}),$('[data-role="send-invitations"]').click(function(){console.log("send invitationts"),sendMails()}),$.ajax({url:"/dashboard/network-detail",method:"get",dataType:"json",success:function(t){console.log("JSON:",t),invitations=new Backbone.Collection(t),invitations_view=new InvitationTable({collection:invitations})},error:function(){console.log("ERROR:",arguments)}})});