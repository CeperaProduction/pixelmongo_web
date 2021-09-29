$(function(){

	let modalTemplate = `
	<div class="modal fade" id="modal-confirm" tabindex="-1" role="dialog">
	    <div class="modal-dialog modal-dialog-centered">
	        <div class="modal-content">
	            <div class="modal-header">
	                {title}
	            </div>
	            <div class="modal-body">
	                {text}
	            </div>
	            <div class="modal-footer">
	                <button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
	                <a id="confirm-button" class="btn btn-danger">Подтвердить</a>
	            </div>
	        </div>
	    </div>
	</div>
	`;

	$('input[type="submit"][data-confirm],button[data-confirm],a[data-confirm]').each(function(){
		bindConfirm($(this));
	});

	function createModal(confirm, text, title) {
		let tpl = modalTemplate.replace('{text}', text).replace('{title}', title);
		let modal = $(tpl).appendTo('body');
		if(!title) modal.find('.modal-header').remove();
		modal.on('show.bs.modal', function() {
			modal.find('#confirm-button').on('click', confirm);
		});
		modal.on('hidden.bs.modal', function() {
			modal.remove();
		});
		modal.modal({
			show : true
		});
	};

	function bindToButton(btn, text, title){
		let form = btn.attr('form');
		if(!form) form = btn.parents('form:first');
		btn.on('click', function(e){
			createModal(function(){
				form.submit();
			}, text, title);
			e.preventDefault();
			return false;
		});
	}

	function bindToLink(btn, text, title){
		let href = btn.attr('href');
		btn.on('click', function(e){
			createModal(function(){
				document.location = href;
			}, text, title);
			e.preventDefault();
			return false;
		});
	}

	function bindConfirm(target){
		let text = target.attr("data-confirm");
		let title = target.attr("data-confirm-title");
		let callback = target.attr("data-confirm-callback");
		if(callback){
			target.on('click', function(e){
				createModal(function(){
					eval(callback);
				}, text, title);
				e.preventDefault();
				return false;
			});
		}else{
			let type = target.prop('nodeName').toLowerCase();
			console.log(type);
			switch(type){
				case 'input': case 'button':
					bindToButton(target, text, title);
					break;
				case 'a':
					bindToLink(target, text, title);
					break;
			}
		}

	}

});