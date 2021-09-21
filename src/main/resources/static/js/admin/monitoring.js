
$(function(){

	function sendOrdinary(ordinary){
		ajax.securedAjax({
			type: 'POST',
			url: '/admin/monitoring/ajax/reorder',
			data: 'ids='+encodeURIComponent(ordinary),
			dataType: 'json'
		});
	}

	$('#monitoring-list>tbody').sortable({
        items: 'tr:not(tr:first-child)',
        cursor: 'move',
        axis: 'y',
        dropOnEmpty: false,
        start: function (e, ui) {
            ui.item.addClass('moving');
			ui.item.css({'min-width' : ui.item.width()+'px',
						'min-height' : ui.item.height()+'px'});
        },
        stop: function (e, ui) {
            ui.item.removeClass('moving');
			ui.item.css({'min-width' : '', 'min-height' : ''});
			let ordinary = '';
            $(this).find('.monitoring-row').each(function (index) {
				if(index != 0) ordinary += ',';
				ordinary += $(this).attr('data-mon-id');
            });
			if(ordinary) {
				sendOrdinary(ordinary);
			}
        },
		helper: function(e, tr){
			let os = tr.children();
			let h = tr.clone();
			h.children().each(function(index){
				$(this).width(os.eq(index).width());
			});
			return h;
		},
    });

	$('#monitoring-check a').on('click', function(e){
		let ip = $('input#inputIp').val();
		let port = $('input#inputPort').val();
		let block = $('#monitoring-check');
		let input = block.find('input');
		block.removeClass('ok');
		block.removeClass('fail');
		input.val('Проверяем сервер...');
		ajax.securedAjax({
			type: 'POST',
			url: '/admin/monitoring/ajax/check',
			data: 'ip='+encodeURIComponent(ip)+'&port='+port,
			dataType: 'json',
			success : function(res) {
				if(res.result == 'ok') block.addClass('ok');
				else block.addClass('fail');
				input.val(res.message);
			},
			error : function(){
				block.addClass('fail');
				input.val('Произошла неизвестная ошибка. Попробуйте обновить страницу.');
			}
		});
		e.preventDefault();
		return false;
	});

});