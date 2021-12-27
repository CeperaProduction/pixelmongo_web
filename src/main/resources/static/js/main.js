
function post(url, data){
    let form = $('<form></form>');
    form.attr("method", "post");
    form.attr("action", url);

    $.each(data, function( key, value ) {
        let field = $('<input></input>');
        field.attr("type", "hidden");
        field.attr("name", key);
        field.attr("value", value);
        form.append(field);
    });
    $(form).appendTo('body').submit();
}

$(function () {
	$('[data-toggle="tooltip"]').tooltip();
	bsCustomFileInput.init();

	let pr = '0px';

	$('.modal').on('show.bs.modal', function(){
		let body = $('body');
		if(body.hasClass('modal-open')){
			pr = body.css('padding-right');
		}
	});

	$('.modal').on('shown.bs.modal', function(){
		let body = $('body');
		if(!body.hasClass('modal-open')){
			body.addClass('modal-open');
			body.css({'padding-right' : pr});
		}
	});
})

//Scrolling elements movement
$(function(){
	let menu = $('.menu');
	let container = $('.menu-container');
	if(menu.length && container.length){
		function apply(){
			let y = container.offset().top;
			let scroll = $(document).scrollTop();
			if(scroll>y){
				menu.addClass('fixed');
			}else if(scroll<=y){
				menu.removeClass('fixed');
			}
		}
		$(document).on('scroll', apply);
		$(window).on('resize', apply);
		apply();
	}
});

$(function(){
	ajax.forms.defaultErrorHandler = function(res, xhr, status, error) {
		let text;
		if(xhr.status == 403){
			text = 'Ошибка доступа. Попробуйте обновить страницу.';
		}else{
			text = status+' '+error;
		}
		if(res != null && res.result !== undefined){
			text = res.message;
			switch(res.result){
				case 'validation_error':
					if(Array.isArray(res.data)){
						res.data.forEach.forEach(function(msg){
							messages.showMessage(msg, 'error');
						});
					}else if(typeof res.data == 'object'){
						Object.values(res.data).forEach(function(msg){
							messages.showMessage(msg, 'error');
						});
					}else{
						messages.showMessage(text, 'error');
					}
					break;
				default:
					messages.showMessage(text, 'error');
					break;
			}

		}else{
			messages.showMessage(text, 'error');
		}

	}
	ajax.forms.defaultHandler = function(res) {
		messages.showMessage(res.message, 'ok');
	}
});

$(document).ready(function(){

	function msg(res, type){
		messages.prepareMessage(res.message, type);
	}

	ajax.forms.bind($('#login-form'), function(res){
		msg(res, 'ok');
		document.location.reload();
	});

	ajax.forms.bind($('#register-form'), function(res){
		msg(res, 'ok');
		document.location = baseUrl;
	});
});

$(function(){

	function msg(res){
		messages.prepareMessage(res.message, res.result == 'ok' ? 'ok' : 'error');
	}

	ajax.forms.bind($('#skin-form'), function(res){
		if(res.data != undefined && res.data.forEach != undefined){
			res.data.forEach.forEach(function(subRes){
				msg(subRes);
			});
		}else if(typeof res.data == 'object'){
			Object.values(res.data).forEach(function(subRes){
				msg(subRes);
			});
		}else{
			msg(res);
		}
		document.location.reload();
	});

	ajax.forms.bind($('#skin-delete-form, #cape-delete-form, #skin-buy-form, #cape-buy-form, #unban-form, #promocode-form'), function(res){
		msg(res);
		document.location.reload();
	});

	ajax.forms.bind($('#pay-form'), function(res){
		if(res.data != undefined && res.data.location != undefined){
			if(res.data.data != undefined){
				post(res.data.location, res.data.data)
			}else{
				document.location = res.data.location;
			}
		}else{
			messages.showMessage(res.message, res.result == 'ok' ? 'ok' : 'error');
		}
	});

	ajax.forms.bind($('#mail-confirm-form'), function(res){
		messages.showMessage(res.message, res.result == 'ok' ? 'info' : 'error');
	});

	let confirmBtn = $('#button-mail-confirm');
	if(confirmBtn.length && userMail){

		$('#inputEmail').on('input', function(){
			let val = $(this).val();
			confirmBtn.prop('disabled', val != userMail);
		});

	}
});

$(function(){
	let moving = $('.background-moving');
	if(moving.length){
		let r = 400;
		let a = 0;
		function calc(a, r){
			let dx = Math.cos(a)*r;
			let dy = Math.sin(a)*r;
			return [dx, dy];
		}
		setInterval(function(){
			a += 0.08;
			let d = calc(a, r);
			moving.each(function(){
				this.style.backgroundPosition = d[0]+'px '+d[1]+'px';
			});
		}, 1000);
	}
});

$(function(){
	$('.menu .navbar-brand').on('click', function(e){
		$('html, body').animate({scrollTop:0}, 500, 'swing');
		$(this).blur();
		e.preventDefault();
		return false;
	});
});

$(function(){
	let modalId = window.location.hash;
	if(modalId.length > 1){
		$(modalId+".modal").modal('show');
	}
});

