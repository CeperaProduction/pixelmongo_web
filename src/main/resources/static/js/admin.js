$(document).ready(function() {

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