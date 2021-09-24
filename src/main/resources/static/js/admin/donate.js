
const donate = new Donate();

function Donate(){

	function initPackEditor(){

		let loading = true;

		let token_name_pattern = /^[a-z0-9_-]+$/;

		let token_used_names = ['player'];

		let tpl_cmd = $("#donate-templates *[data-tpl='command']").html();
		let tpl_backCmd = $("#donate-templates *[data-tpl='backCommand']").html();

		let tpl_token = $("#donate-templates *[data-tpl='token']").html();

		let tpl_tokens = [];

		tpl_tokens['RANDOM_INT'] = $("#donate-templates *[data-tpl='tokenRandomInt']").html();
		tpl_tokens['RANDOM_VALUE'] = $("#donate-templates *[data-tpl='tokenRandomValue']").html();
		tpl_tokens['SELECT_VALUE'] = $("#donate-templates *[data-tpl='tokenSelectValue']").html();

		let token_handlers = [];

		token_handlers['RANDOM_INT'] = commonTokenHandler;
		token_handlers['RANDOM_VALUE'] = variantsTokenHandler;
		token_handlers['SELECT_VALUE'] = variantsTokenHandler;

		let tpl_token_variants = [];

		tpl_token_variants['RANDOM_VALUE'] = $("#donate-templates *[data-tpl='tokenRandomValueVariant']").html();
		tpl_token_variants['SELECT_VALUE'] = $("#donate-templates *[data-tpl='tokenSelectValueVariant']").html();

		function commonTokenHandler(name){
			token_used_names.push(name);
			$(this).find('.donate-token-remove').on('click', function(e){
				let i = token_used_names.indexOf(name);
				if (i !== -1) token_used_names.splice(i, 1);
				removeToken($(this));
				e.preventDefault();
				return false;
			});
		}

		function variantsTokenHandler(name, type, typeText) {
			let variants = this.find('.donate-token-variants');
			let tpl = tpl_token_variants[type];
			tpl = prepareTokenTemplate(tpl, name, type, typeText);

			function removeVariant(button) {
				button.closest('.donate-token-variant').remove();
			}

			function addVariant(){
				variants.append(tpl);
				let variant = variants.children().last();
				variant.find('.donate-token-variant-remove').on('click', function(e){
					removeVariant($(this));
					e.preventDefault();
					return false;
				});
			}

			commonTokenHandler.call(this, name);

			this.find('.donate-token-variant-add').on('click', function(e){
				addVariant();
				e.preventDefault();
				return false;
			});

			if(loading) {
				variants.find('.donate-token-variant-remove').on('click', function(e){
					removeVariant($(this));
					e.preventDefault();
					return false;
				});
			}

		}

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
				$('#donate-timed-settings input').prop('disabled', false);
			}else{
				$('#donate-timed-settings').fadeOut(loading ? 0 : 500);
				mult.prop('disabled', false);
				$('#donate-timed-settings input').prop('disabled', true);
			}
		}

		backAreaSync();
		$('#checkboxTimed').change(backAreaSync);

		function prepareTokenTemplate(tpl, name, type, typeText){
			return tpl.replaceAll('{{token-name}}', name)
					.replaceAll('{{token-type}}', type)
					.replaceAll('{{token-type-text}}', typeText);
		}

		function makeToken() {

			let type = $('#selectTokenType').val();
			let typeText = $('#selectTokenType option:selected').text();
			let name = $('#inputTokenName').val();

			if(!name){
				console.log('Token name required');
				$('#inputTokenName').focus();
				return;
			}

			if(!token_name_pattern.test(name)){
				console.log('Token name doesn\'t match pattern');
				$('#inputTokenName').focus();
				if(messages)
					messages.showMessage('Некорректное имя токена', 'error');
				return;
			}

			if(token_used_names.indexOf(name) !== -1){
				console.log('Token name is already in use');
				$('#inputTokenName').focus();
				if(messages)
					messages.showMessage('Такой токен уже существует в данном наборе', 'error');
				return;
			}

			let tpl = tpl_tokens[type];

			if(!tpl){
				console.log('No template found for token type '+tpl);
				return;
			}

			let handler = token_handlers[type];

			if(!handler){
				console.log('No handler found for token type '+tpl);
				return;
			}

			tpl = tpl_token.replaceAll("{{token-content}}", tpl);

			tpl = prepareTokenTemplate(tpl, name, type, typeText);

			let list = $('.donate-token-list');
			list.append(tpl);

			let tokenBlock = list.children().last();

			handler.call(tokenBlock, name, type, typeText);

			$('#inputTokenName').val('');

		}

		function removeToken(button) {
			button.closest('.donate-token-block').remove();
		}

		$('#donate-token-add').on('click', function(e){
			makeToken();
			e.preventDefault();
			return false;
		});

		$('.donate-token-list .donate-token-block').each(function(){

			let tokenBlock = $(this);

			let type = tokenBlock.attr('data-token-info-type');

			let handler = token_handlers[type];
			if(!handler) return;

			let name = tokenBlock.attr('data-token-info-name');
			let typeText = tokenBlock.attr('data-token-info-type-text');

			handler.call(tokenBlock, name, type, typeText);
		});

		loading = false;
	}

	function initSortables() {

		function sendOrdinary(url, ordinary){
			ajax.securedAjax({
				type: 'POST',
				url: url,
				data: 'ids='+encodeURIComponent(ordinary),
				dataType: 'json'
			});
		}

		function sortableStart(e, ui){
	        ui.item.addClass('moving');
			ui.item.css({'min-width' : ui.item.width()+'px',
						'min-height' : ui.item.height()+'px'});
	    }

		function sortableHelper(e, ui){
			let os = ui.children();
			let h = ui.clone();
			h.children().each(function(index){
				$(this).width(os.eq(index).width());
			});
			return h;
		}

		$('#donate-page-list>tbody').sortable({
	        items: 'tr',
	        cursor: 'move',
			handle: '.donate-page-move',
	        axis: 'y',
	        dropOnEmpty: false,
	        start: sortableStart,
	        stop: function (e, ui) {
	            ui.item.removeClass('moving');
				ui.item.css({'min-width' : '', 'min-height' : ''});
				let ordinary = '';
	            $(this).find('.donate-page-row').each(function (index) {
					if(index != 0) ordinary += ',';
					ordinary += $(this).attr('data-page-id');
	            });
				if(ordinary) {
					sendOrdinary('/admin/donate/pages/reorder', ordinary);
				}
	        },
			helper: sortableHelper
	    });

		$('.donate-page-content').sortable({
	        items: 'div.donate-category-block',
	        cursor: 'move',
			handle: '.donate-category-move',
	        axis: 'y',
	        dropOnEmpty: false,
	        start: sortableStart,
	        stop: function (e, ui) {
	            ui.item.removeClass('moving');
				ui.item.css({'min-width' : '', 'min-height' : ''});
				let ordinary = '';
	            $(this).find('.donate-category-block').each(function (index) {
					if(index != 0) ordinary += ',';
					ordinary += $(this).attr('data-category-id');
	            });
				if(ordinary) {
					let pageTag = $('.donate-page-content').attr('data-page-tag');
					sendOrdinary('/admin/donate/pages/'+pageTag+'/category/reorder', ordinary);
				}
	        },
			helper: sortableHelper
	    });

		$('#donate-pack-list>tbody').sortable({
	        items: 'tr',
	        cursor: 'move',
			handle: '.donate-pack-move',
	        axis: 'y',
	        dropOnEmpty: false,
	        start: sortableStart,
	        stop: function (e, ui) {
	            ui.item.removeClass('moving');
				ui.item.css({'min-width' : '', 'min-height' : ''});
				let ordinary = '';
	            $(this).find('.donate-pack-row').each(function (index) {
					if(index != 0) ordinary += ',';
					ordinary += $(this).attr('data-pack-id');
	            });
				if(ordinary) {
					let pageTag = $('.donate-page-content').attr('data-page-tag');
					let category = $(this).closest('.donate-category-block').attr('data-category-id');
					sendOrdinary('/admin/donate/pages/'+pageTag+'/category/'+category+'/reorder', ordinary);
				}
	        },
			helper: sortableHelper
	    });

	}

	function initServerEditor() {

		$('#donate-key-gen').on('click', function(e){
			ajax.securedAjax({
				type: 'GET',
				url: '/admin/donate/servers/keygen',
				dataType: 'json',
				success : function(res){
					if(res.result == 'ok'){
						$('input#inputKey').val(res.message);
					}
				}
			});
			e.preventDefault();
			return false;
		});

	}

	function initDiscountEditor() {

		let inputPage = $('#inputPage');
		let inputCategory = $('#inputCategory');
		let inputPack = $('#inputPack');

		function getData(url, callback) {

			function handle(res) {
				if(res.result == 'ok') {
					callback(res.data);
				}else{
					messages.showMessage(res.message, 'error');
				}
			}

			$.ajax({
				type : 'GET',
				url : url,
				dataType : 'json',
				success : handle,
				error : function(xhr){
					try{
						let res = JSON.parse(xhr.responseText);
						handle(res);
					}catch(ex){
						console.log(ex);
					}
				}
			});

		}

		function dropSelect(select) {
			select.prop('disabled', true);
			select.find('option[value!=0]').remove();
		}

		inputPage.on('change', function(){
			dropSelect(inputCategory);
			dropSelect(inputPack);
			let val = parseInt($(this).val())

			if(val){
				getData('/admin/donate/discount/categories?page='+val, function(data){
					data.forEach(function(e){
						inputCategory.append('<option value="'+e.id+'">'+e.title+"</option>");
					});
					inputCategory.prop('disabled', false);
				});
			}
		});

		inputCategory.on('change', function(){
			dropSelect(inputPack);
			let val = parseInt($(this).val());

			if(val){
				getData('/admin/donate/discount/packs?category='+val, function(data){
					data.forEach(function(e){
						inputPack.append('<option value="'+e.id+'">'+e.title+"</option>");
					});
					inputPack.prop('disabled', false);
				});
			}
		});

		ajax.forms.bind($('#discountForm'),
			function(res){
				messages.showMessage(res.message, 'ok');
			},
			function(res){
				messages.showMessage(res.message, 'error');
			}
		);

	}

	return {
		initPackEditor,
		initSortables,
		initServerEditor,
		initDiscountEditor
	};

}

