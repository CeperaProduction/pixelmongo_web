
function MessageHandler(){

	let _this = this;

	let msgCounter = 0;

	this.delayTime = 10000;

	function getMessageClass(msg){
		switch(msg.type){
			case 'err': case 'error': return 'message_err';
			case 'warn': return 'message_warn';
			case 'ok': case 'success': return 'message_ok';
			default: return 'message_info';
		}
	}

	function close(message){
		if(!message.showed) return false;
		message.showed = false;
		message.fadeOut(800, function(){
			message.remove();
		});
		return true;
	}

	this.close = function(messageIndex){
		let message = $('#msg_block .message[message-index="'+messageIndex+'"]');
		return close(message);
	}

	function getMsg(msg, type, time) {
		if(typeof msg == 'string'){
			msg = {
				type : 'info',
				text : msg
			}
		}
		if(typeof type == 'string') msg.type = type;
		if(typeof time == 'number') msg.time = time;
		return msg;
	}

	this.showMessage = function(msg, type, time) {
		msg = getMsg(msg, type, time);
		let index = ++msgCounter;
		$('#msg_block').prepend('<div message-index="'+index+'" class="message"></div>')
		let message = $('#msg_block .message[message-index="'+index+'"]');
		message.showed = true;
		message.css({'margin-top':'-200px'});
		message.addClass(getMessageClass(msg));
		message.html(msg.text);
		message.animate({"margin-top":"0px", "opacity":"1"}, 500);
		message.on("click", function(){close(message)});
		let delay = this.delayTime;
		if(msg.time !== undefined && msg.time >= -1) delay = msg.time;
		if(msg.time != -1){
			setTimeout(function(){close(message)}, delay);
		}
		return index;
	};

	this.prepareMessage = function(msg, type, time) {
		msg = getMsg(msg, type, time);
		let cookie = Cookies('message');
		let msgs = JSON.parse(cookie != undefined ? cookie : '[]');
		msgs.push(msg);
		cookie = JSON.stringify(msgs);
		Cookies('message', cookie);
	}


	function loadFromCookies() {
		let cookie = Cookies('message');
		let msgs = JSON.parse(cookie != undefined ? cookie : '[]');
		if(msgs.length > 0) {
			Cookies('message', '[]');
			$(document).ready(function(){
				for(var i = 0; i<msgs.length; i++){
					let msg = msgs[i];
					_this.showMessage(msg);
				}
			});
		}
	}

	loadFromCookies();

}

const messages = new MessageHandler();
