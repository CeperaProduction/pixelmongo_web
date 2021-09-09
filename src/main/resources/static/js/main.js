
//Side blocks
$(document).ready(function(){
	let b_auth = false;
	let b_monitor = false;
	let auth = $(".auth");
	let monitor = $(".monitor");

	$(".auth .open").click(function(){
		b_auth = !b_auth;
		auth.css({'margin-left' : b_auth ? '-160px' : ''});
	});

	$(".monitor .open").click(function(){
		b_monitor = !b_monitor;
		monitor.css({'margin-left' : b_monitor ? '160px' : ''});
	});
});

//Scrolling elements movement
$(document).ready(function(){
	let menu = $('.menu');
	let sides = $('.side_containers');
	let menuPos = menu.offset();
	let sidesPos = sides.offset();
	let y = menuPos.top+8;
	let sidesOffset = sidesPos.top - y;
	function apply(){
		let scroll = $(document).scrollTop();
		if(scroll>y){
			menu.addClass('fixed');
			sides.addClass('fixed');
			sides.css({'top' : sidesOffset+'px'});
		}else if(scroll<=y){
			menu.removeClass('fixed');
			sides.removeClass('fixed');
			sides.css({'top' : ''});
		}
	}
	$(document).scroll(apply);
	apply();
});

$(document).ready(function(){
	ajax.forms.defaultErrorHandler = function(res, xhr, status, error) {
		let text = status+' '+error;
		if(res !== null && res.message !== undefined){
			text = res.message;
		}
		messages.showMessage(text, 'error');
	}
	ajax.forms.defaultHandler = function(res, xhr, status) {
		messages.showMessage(res.message, 'ok');
	}
});

$(document).ready(function(){

	function msg(res, type){
		messages.prepareMessage(res.message, type);
	}

	ajax.forms.bind($('#login_form'), function(res){
		msg(res, 'ok');
		document.location.reload();
	});

	ajax.forms.bind($('#register_form'), function(res){
		msg(res, 'ok');
		document.location = '/';
	});
});

