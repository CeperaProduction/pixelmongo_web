
//ajax with csrf tocken
function securedAjax(settings) {
	let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
	let ex = {
		beforeSend: function(req) {
			req.setRequestHeader(header, token);
		}
	};
	$.ajax({settings, ex});
}

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

//Login form
$(document).ready(function(){
	let errorHandler = function(xhr,status,error){
		console.log('Auth error.');
		console.log('Status: '+status);
		console.log('Error data: ');
		console.log(error);
	}
	$('#login_form').submit(function(){
		let form = $(this);
		$.ajax({
			type: 'POST',
			url: form.attr('action'),
			data: form.serialize(),
			dataType: 'json',
			success: function(res, status, xhr){
				if(res.result == 'ok'){
					//alert(JSON.stringify(res));
					document.location = '/';
				}else{
					errorHandler(xhr, status, res);
				}
			},
			error: errorHandler
		});
		return false;
	});
});

//Register form
$(document).ready(function(){
	let errorHandler = function(xhr,status,error){
		console.log('Register error.');
		console.log('Status: '+status);
		console.log('Error data: ');
		console.log(error);
	}
	$('#register_form').submit(function(){
		let form = $(this);
		$.ajax({
			type: 'POST',
			url: form.attr('action'),
			data: form.serialize(),
			dataType: 'json',
			success: function(res, status, xhr){
				if(res.result == 'ok'){
					//alert(JSON.stringify(res));
					document.location = '/';
				}else{
					errorHandler(xhr, status, res);
				}
			},
			error: errorHandler
		});
		return false;
	});
});

