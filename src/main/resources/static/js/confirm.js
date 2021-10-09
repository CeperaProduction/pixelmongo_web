
function Confirmation(){

	let _this = this;

	this.modalTemplate = `
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

	function ask(text, title, confirmed, declined) {
		let tpl = _this.modalTemplate.replace('{text}', text).replace('{title}', title);
		let modal = $(tpl).appendTo('body');
		if(!title) modal.find('.modal-header').remove();
		let ok = false;
		modal.on('show.bs.modal', function() {
			modal.find('#confirm-button').on('click', function(){
				ok = true;
				modal.modal('hide');
				confirmed();
			});
		});
		modal.on('hidden.bs.modal', function() {
			modal.remove();
			if(!ok && declined) declined();
		});
		modal.modal('show');
	};

	function bindToButton(btn, text, title){
		let form = btn.attr('form');
		if(form) form = $('#'+form);
		else form = btn.parents('form:first');
		btn.on('click', function(e){
			ask(text, title, function(){
				form.submit();
			});
			e.preventDefault();
			return false;
		});
	}

	function bindToLink(btn, text, title){
		let href = btn.attr('href');
		btn.on('click', function(e){
			ask(text, title, function(){
				document.location = href;
			});
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
				ask(text, title, function(){
					eval(callback);
				});
				e.preventDefault();
				return false;
			});
		}else{
			let type = target.prop('nodeName').toLowerCase();
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

	return {
		ask, bindConfirm
	};
}

const confirmation = new Confirmation();

$(function(){

	$('input[type="submit"][data-confirm],button[data-confirm],a[data-confirm]').each(function(){
		confirmation.bindConfirm($(this));
	});

});