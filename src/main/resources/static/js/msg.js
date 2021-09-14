
function MessageHandler(){

	let _this = this;

	let msgCounter = 0;

	let displayCounter = 0;

	this.delayTime = 10000;

	this.template = '{text}';

	this.append = false;

	function getMessageClass(msg){
		switch(msg.type){
			case 'err': case 'error': return 'message_err';
			case 'warn': return 'message_warn';
			case 'ok': case 'success': return 'message_ok';
			default: return 'message_info';
		}
	}

	this.fadeIn = function(message, displayCounter) {
		if(displayCounter == 0){
			message.css({'margin-top':'-200px', "opacity":"0"});
			message.animate({"margin-top":"0px", "opacity":"1"}, 500);
		}else{
			message.css({"opacity":"0"});
			message.animate({"opacity":"1"}, 250);
		}
	}

	this.fadeOut = function(message, displayCounter, innerCallback) {
		message.fadeOut(800, innerCallback);
	}

	function close(message){
		if(!message.showed) return false;
		message.showed = false;
		_this.fadeOut(message, displayCounter, function(){
			message.remove();
			--displayCounter;
			if(displayCounter < 0) displayCounter = 0;
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
		let tpl = this.template.replaceAll('{text}', msg.text);
		tpl = '<div message-index="'+index+'" class="message">'+tpl+'</div>';
		if(this.append){
			$('#msg_block').append(tpl);
		}else{
			$('#msg_block').prepend(tpl);
		}
		let message = $('#msg_block .message[message-index="'+index+'"]');
		message.showed = true;
		this.fadeIn(message, displayCounter);
		++displayCounter;
		message.addClass(getMessageClass(msg));
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
		let cookie = $.cookie('message');
		let msgs = JSON.parse(cookie != undefined ? cookie : '[]');
		msgs.push(msg);
		cookie = JSON.stringify(msgs);
		$.cookie('message', cookie, { 'path': '/' });
	}


	function loadFromCookies() {
		let cookie = $.cookie('message');
		let msgs = JSON.parse(cookie != undefined ? cookie : '[]');
		if(msgs.length > 0) {
			$.cookie('message', '[]', { 'path': '/' });
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
