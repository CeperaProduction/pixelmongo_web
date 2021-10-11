
$(function(){

	function sendOrdinary(ordinary){
		ajax.securedAjax({
			type: 'POST',
			url: baseUrl+'admin/staff/ajax/reorder',
			data: 'ids='+encodeURIComponent(ordinary),
			dataType: 'json'
		});
	}

	$('#staff-list>tbody').sortable({
        items: 'tr:not(tr:first-child)',
        cursor: 'move',
		handle: '.staff-move',
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
            $(this).find('.staff-row').each(function (index) {
				if(index != 0) ordinary += ',';
				ordinary += $(this).attr('data-staff-id');
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

});