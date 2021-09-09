$(document).ready(function() {

    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
    });

});

//Scrolling elements movement
$(document).ready(function(){
	let sc = $('#sidebar-content');
	let scp = sc.offset();
	let y = scp.top;
	function apply(){
		let scroll = $(document).scrollTop();
		if(scroll>y){
			sc.addClass('fixed');
		}else if(scroll<=y){
			sc.removeClass('fixed');
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
	ajax.forms.bind($('#logout'), function(res){
		messages.prepareMessage(res.message, 'ok');
		document.location = '/';
	});

});