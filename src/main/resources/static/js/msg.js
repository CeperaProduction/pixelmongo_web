//изи меседж принтер
$(document).ready(function(){
	var msg_block = $('#msg_block');
	var cookie = Cookies('message');
	var delayTime = Cookies('msg_fadeout_delay') != undefined ? Cookies('msg_fadeout_delay')*1000 : 10000;
	
	var msgs = JSON.parse(cookie != undefined ? cookie : '{}');
	
	function close(message){
		if(!message.showed) return false;
		message.showed = false;
		message.fadeOut(800, function(){
			message.remove();
		});
		return true;
	}
	
	for(var i = 1; i<msgs.length+1; i++){
		var msg = msgs[i-1];
		msg_block.prepend('<div id="msg_'+i+'" class="message"></div>')
		var message = $('#msg_block #msg_'+i);
		message.showed = true;
		message.css({'margin-top':'-200px'});
		switch(msg.type){
			case 'err':
				message.addClass('message_err');
				break;
			case 'warn':
				message.addClass('message_warn');
				break;
			case 'ok':
				message.addClass('message_ok');
				break;
			default:
				message.addClass('message_info');
				break;
		}
		message.html(msg.msg);
		message.animate({"margin-top":"0px", "opacity":"1"}, 500);
		
		
		message.on("click", function(){close(message)});
		setTimeout(function(){close(message)}, delayTime);
	}
	Cookies('message', cookie = '[]');
	
});