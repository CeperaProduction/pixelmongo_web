//Scrolling elements movement
$(document).ready(function(){
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

$(document).ready(function(){
	ajax.forms.defaultErrorHandler = function(res, xhr, status, error) {
		let text = status+' '+error;
		if(res !== null && res.message !== undefined){
			text = res.message;
		}
		messages.showMessage(text, 'error');
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
	let moving = $('.background-moving');
	if(moving.length){
		let r = 400;
		let a = 0;
		function calc(a, r){
			let dx = Math.cos(a)*r;
			let dy = Math.sin(a)*r;
			return [dx, dy];
		}
		let d = calc(a, r);
		moving.css({
			'background-position-x' : d[0]+'px',
			'background-position-y' : d[1]+'px'
		});
		function animate(){
			a += 0.08;
			let d = calc(a, r);
			moving.animate({
				'background-position-x' : d[0]+'px',
				'background-position-y' : d[1]+'px',
			}, 1000, 'linear', animate);
		}
		animate();
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

