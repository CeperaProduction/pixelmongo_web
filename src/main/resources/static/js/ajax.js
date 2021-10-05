function Ajax(){

	function securedAjax(settings) {
		let token = $("meta[name='_csrf']").attr("content");
	    let header = $("meta[name='_csrf_header']").attr("content");
		let ex = {
			beforeSend: function(req) {
				req.setRequestHeader(header, token);
			}
		};
		$.ajax({...settings, ...ex});
	}

	function AjaxForms(){

		let _this = this;

		var errorHandlers = [];

		this.defaultErrorHandler = function(res, xhr, status, error){
			console.log('Form send error');
			console.log('Status: '+status);
			console.log('Error data: '+error);
			console.log("Response: ");
			console.log(res);
		}

		this.defaultHandler = function(res, xhr, status){
			console.log("Form send success");
			console.log("Response: ");
			console.log(res);
		}

		var getErrorHandler = function(result, providedHandler){
			if(providedHandler !== null && providedHandler !== undefined)
				return providedHandler;
			let handler = errorHandlers[result];
			if(handler === null || handler === undefined)
				return _this.defaultErrorHandler;
			return handler;
		}

		this.setErrorHandler = function(result, handler){
			errorHandlers[result] = handler;
		}

		this.sendForm = function(form, successHandler, failHandler){
			if(successHandler == undefined){
				successHandler = this.defaultHandler;
			}
			let data = '';
			let dataParams = {};
			if(form.prop('enctype').startsWith('multipart/')){
				data = new FormData(form[0]);
				dataParams.contentType = false;
				dataParams.processData = false;
			}else{
				data = form.serialize();
			}
			let method = form.prop('method');
			if(!method) method = 'GET';
			let action = form.prop('action');
			if(!action) action = '';
			securedAjax({...dataParams, ...{
				type: method,
				url: action,
				data: data,
				dataType: 'json',
				success: function(res, status, xhr){
					if(res.result == 'ok'){
						successHandler.call(form, res, xhr, status);
					}else{
						getErrorHandler(res.result, failHandler)
							.call(form, res, xhr, status, res.message);
					}
				},
				error: function(xhr, status, error){
					try{
						let res = JSON.parse(xhr.responseText);
						if(res.result == 'ok') {
							successHandler.call(form, res, xhr, status);
						}else{
							getErrorHandler(res.result, failHandler)
								.call(form, res, xhr, status, res.message);
						}
					}catch(ex){
						console.log(ex);
						_this.defaultErrorHandler
							.call(form, xhr.responseText, xhr, status, error);
					}
				}
			}});
		}

		this.bind = function(form, success, err) {
			form.submit(function(){
				let f = $(this);

				let callback = function(){};
				let fail = undefined;

				if(success == undefined){
					let callbackStr = f.attr('ajax');
					if(callbackStr){
						callback = function(res, xhr, status){
							eval(callbackStr);
						};
					}
				}else{
					callback = success;
				}

				if(err == undefined){
					let failStr = f.attr('ajax-fail');
					if(failStr) {
						fail = function(res, xhr, status, error){
							eval(failStr);
						};
					}
				}else{
					fail = err;
				}

				_this.sendForm(f, callback, fail)
				return false;
			});
		}

		$(document).ready(function(){
			_this.bind($('form[ajax]'));
		});

	}

	let forms = new AjaxForms();


	return {
		securedAjax,
		forms
	}

}

const ajax = new Ajax();