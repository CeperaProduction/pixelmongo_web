
$(function(){

	if($('#donate-templates')) {

		let tpl_cmd = $("#donate-templates *[data-tpl='command']").html();
		let tpl_backCmd = $("#donate-templates *[data-tpl='backCommand']").html();

		let tpl_token = [];

		tpl_token['RANDOM_INT'] = $("#donate-templates *[data-tpl='tokenRandomInt']").html();

		function addCommand(){
			let list = $('#donate-give.donate-command-list');
			list.append(tpl_cmd);
			list.children().last().find('.donate-cmd-remove').on('click', function(e){
				removeCommand($(this));
				e.preventDefault();
				return false;
			});
		}

		function addBackCommand(){
			let list = $('#donate-back.donate-command-list');
			list.append(tpl_backCmd);
			list.children().last().find('.donate-cmd-remove').on('click', function(e){
				removeCommand($(this));
				e.preventDefault();
				return false;
			});
		}

		function removeCommand(button) {
			button.closest('.donate-command').remove();
		}

		$('.donate-cmd-remove').on('click', function(e){
			removeCommand($(this));
			e.preventDefault();
			return false;
		});

		$('#donate-cmd-add').on('click', function(e){
			addCommand();
			e.preventDefault();
			return false;
		});

		$('#donate-back-cmd-add').on('click', function(e){
			addBackCommand();
			e.preventDefault();
			return false;
		});

		function backAreaSync(){
			let back = $('#checkboxTimed').is(':checked');
			let mult = $('#checkboxCountable');
			if(back) {
				$('#donate-timed-settings').fadeIn(500);
				mult.prop('checked', false);
				mult.prop('disabled', true);
			}else{
				$('#donate-timed-settings').fadeOut(500);
				mult.prop('disabled', false);
			}
		}

		backAreaSync();
		$('#checkboxTimed').change(backAreaSync);



	}

});