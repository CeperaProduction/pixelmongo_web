
$(function(){

	let contents = $('.modal.donate-pack-content');

	contents.each(function(){

		let content = $(this);
		let packId = content.data('donate-pack');
		let options = $('.modal.donate-pack-options[data-donate-pack="'+packId+'"]');

		if(options.length == 0) return;

		let packForm = options.find('form');

		let noOptions = packForm.find('input, select, textarea').not('[type="hidden"]').length == 0;

		let button1 = content.find('.donate-buy-button');

		if(noOptions){
			button1.attr('form', packForm.prop('id'));
		}else{
			button1.on('click', function(){
				content.modal('hide');
				options.modal('show');
			});
		}

		let costBlock = options.find('.donate-pack-cost');
		let initialCost = costBlock.data('pack-cost');
		function updateTokens(){
			let costChange = 0;
			packForm.find('.donate-token-select option:selected[data-token-cost]').each(function(){
				let val = parseInt($(this).data('token-cost'));
				if(val != NaN){
					costChange += val;
				}
			});
			costBlock.text((initialCost+costChange)+'');
		}

		updateTokens();
		$('.donate-token-select select').on('change', updateTokens);

		packForm.on('submit', function(e){
			e.preventDefault();
			let form = $(this);
			let modal = $('.modal.donate-pack-modal.show');
			modal.modal('hide');
			let title = modal.find('.modal-title').text();
			if(!title) title = 'Подтверждение';
			let message = 'Вы уверены что хотите купить данный набор';
			let count = form.find('input[name="count"]').val();
			if(count && count > 1)
				message += ' в количестве '+count+' штук'
			let server = form.find('select[name="server"] option:selected').text();
			if(server)
				message += ' на сервере '+server;
			message += '?';
			confirmation.ask(message, title, function(){
				ajax.forms.sendForm(form, function(res){
					messages.showMessage(res.message, 'ok');
					if(res.data != undefined && res.data.balance !== undefined){
						$('.balance').text(res.data.balance);
					}
				}, function(res){
					messages.showMessage(res.message, 'error');
					modal.modal('show');
				});
			}, function(){
				modal.modal('show');
			});
			return false;
		});

	});

});