$(document).ready(function() {

	bsCustomFileInput.init();

    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
    });

});

//Scrolling elements movement
$(document).ready(function(){
	let sc = $('#sidebar-container');
	let scp = sc.offset();
	let y = scp.top;
	sc.addClass('fixed');
	function apply(){
		let scroll = $(document).scrollTop();
		if(scroll>y){
			sc.css({"padding-top" : "0px"});
		}else if(scroll<=y){
			sc.css({"padding-top" : (y-scroll)+"px"});
		}
	}
	$(document).scroll(apply);
	apply();
});

$(document).ready(function(){
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
	ajax.forms.defaultHandler = function(res, xhr, status) {
		messages.showMessage(res.message, 'ok');
	}
	ajax.forms.bind($('#logout'), function(res){
		messages.prepareMessage(res.message, 'ok');
		document.location = '/';
	});

});