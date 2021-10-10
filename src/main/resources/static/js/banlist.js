
$(function(){

	let updateIndex = 0;

	let url = baseUrl+"banlist/"

	let uploadTemplate = '<a class="proof-upload" data-toggle="tooltip" data-placement="bottom" title="Загрузить доказательства" href="#"><i class="fa fa-upload" /></a>';
	let showTemplate = '<a class="proof-show" data-toggle="tooltip" data-placement="bottom" title="Доказательства" href="#"><i class="fa fa-file" /></a>';
	let deleteTemplate = '<a class="proof-delete" data-toggle="tooltip" data-placement="bottom" title="Удалить доказательства" href="#"><i class="fa fa-times" /></a>';

	let uploadModalTemplate = `
		<div class="modal fade" id="modal-ban-proof-upload" tabindex="-1" role="dialog">
		    <div class="modal-dialog modal-dialog-centered">
		        <div class="modal-content">
		            <div class="modal-header">
		                <h5 class="modal-title">Загрузка доказательств</h5>
				      	<button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
				        	<span aria-hidden="true">
				        		<i class="fa fa-times"></i>
							</span>
				      	</button>
		            </div>
		            <div class="modal-body">

						<form id="ban-proof-upload-form" action="{form_url}" method="post" enctype="multipart/form-data">
				      		<div class="form-group">
				      			<div class="custom-file">
								    <input type="file" class="custom-file-input" id="inputProof" name="file" accept=".png,.jpg">
								    <label class="custom-file-label" for="inputProof" data-browse="Выбрать">
								    	Выберите файл
								    </label>
								</div>
								<small class="form-text text-muted">
									Доступные форматы: .png, .jpg. Максимальный вес: 4Мб
								</small>
				      		</div>
				      	</form>

		            </div>
		            <div class="modal-footer">
		                <button form="ban-proof-upload-form" type="submit" class="btn btn-primary btn-block">Загрузить</button>
		            </div>
		        </div>
		    </div>
		</div>
		`;

	let showModalTemplate = `
		<div class="modal fade" id="modal-ban-proof" tabindex="-1" role="dialog">
		    <div class="modal-dialog modal-dialog-centered">
		        <div class="modal-content">
		            <div class="modal-header">
		                <h5 class="modal-title">Просмотр доказательств</h5>
						<div>
					      	<button type="button" class="close" data-dismiss="modal" aria-label="Закрыть">
					        	<span aria-hidden="true">
					        		<i class="fa fa-times"></i>
								</span>
					      	</button>
							<button id="modal-proof-expand" type="button" class="close" aria-label="Увеличить">
					        	<span aria-hidden="true">
					        		<i class="fa fa-expand"></i>
								</span>
					      	</button>
						</div>
		            </div>
		            <div class="modal-body">
						<div class="ban-proof-container">
							<img src="{proof_url}" class="ban-proof" />
						</div>
		            </div>
		        </div>
		    </div>
		</div>
		`;

	function showUploadModal(banId, proof){

		let tpl = uploadModalTemplate.replace('{form_url}', url+banId);
		let modal = $(tpl);

		modal.appendTo($('body'));

		let form = modal.find('#ban-proof-upload-form');

		form.on('submit', function(e){
			e.preventDefault();
			ajax.forms.sendForm(form, function(res){
				messages.showMessage(res.message, 'ok');
				if(res.data != undefined){
					proof.data('ban-proof', res.data.location);
					updateIndex++;
					updateProof(proof);
				}
				modal.modal('hide');
			});
			return false;
		});

		form.find('.custom-file-input').on('change', function(){
			var file = $(this).val().split('\\').pop();
            $(this).next('.custom-file-label').html(file);
		})

		modal.on('hidden.bs.modal', function() {
			modal.remove();
		});
		modal.modal('show');

	}

	function showProofModal(url){
		let tpl = showModalTemplate.replace('{proof_url}', url);
		let modal = $(tpl);

		modal.appendTo($('body'));

		modal.on('hidden.bs.modal', function() {
			modal.remove();
		});

		modal.find($('#modal-proof-expand')).on('click', function(e){
			e.preventDefault;
			modal.find('.modal-dialog').toggleClass('modal-xl');
			return false;
		});

		modal.modal('show');
	}

	function deleteProof(banId, proof){

		function success(res){
			messages.showMessage(res.message, 'info');
			proof.removeAttr('data-ban-proof');
			proof.removeData('ban-proof');
			updateIndex++;
			updateProof(proof);
		}

		function error(res){
			messages.showMessage(res.message, 'error');
		}

		ajax.securedAjax({
			type: 'DELETE',
			url: url+banId,
			dataType: 'json',
			success: function(res){
				if(res.result == 'ok'){
					success(res);
				}else{
					error(res);
				}
			},
			error: function(xhr){
				try{
					let res = JSON.parse(xhr.responseText);
					if(res.result == 'ok') {
						success(res);
					}else{
						error(res);
					}
				}catch(ex){
					console.log(ex);
					error({result:'error', message: 'error'});
				}
			}
		});

	}

	function updateProof(proof){

		let id = proof.data('ban');
		let url = proof.data('ban-proof');
		let canUpload = proof.data('ban-proof-upload');
		let canDelete = proof.data('ban-proof-delete');

		proof.html('');

		if(url){

			if(updateIndex > 0) url+= '?t='+updateIndex;

			let show = $(showTemplate+'');
			show.appendTo(proof);
			show.on('click', function(e){
				e.preventDefault();
				showProofModal(url);
				return false;
			});

			if(canDelete){

				let del = $(deleteTemplate+'');
				del.appendTo(proof);
				del.on('click', function(e){
					e.preventDefault();
					deleteProof(id, proof);
					return false;
				});

			}

		}else{

			if(canUpload){

				let upload = $(uploadTemplate+'');
				upload.appendTo(proof);
				upload.on('click', function(e){
					e.preventDefault();
					showUploadModal(id, proof);
					return false;
				});

			}

		}

		proof.find('*[data-toggle="tooltip"]').tooltip();

	}

	$('.ban-proofs').each(function(){
		updateProof($(this));
	});

});