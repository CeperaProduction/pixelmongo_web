var monitoring = {
	
	started : false,
	
	lineTemplate : '',
	
	printedServers : [],
	
	data : {
		servers : {},
		summary : {
			currentPlayers : 0,
			maxPlayers : 0
		}
	},
	
	updateTimerId : 0,
	
	loadData : function(callback){
		$.ajax({
				type: "GET",
				url: "/monitoring",
				success: function(res){
					if(res.result == 'ok'){
						let currentPlayers = 0;
						let maxPlayers = 0;
						res.data.forEach(function(server, index){
							if(server.online){
								currentPlayers += server.currentPlayers;
								maxPlayers += server.maxPlayers;
							}
						});
						monitoring.data.servers = res.data;
						monitoring.data.summary.currentPlayers = currentPlayers;
						monitoring.data.summary.maxPlayers = maxPlayers;
						callback();
					}else{
						console.log('Monitoring request error. Server response:');
						console.log(res);
					}
				}
			});
	},
	
	initTemplate : function(){
		let tpl = $('#monitoring_template');
		if(tpl){
			//monitoring.lineTemplate = tpl.prop('outerHTML')+'';
			monitoring.lineTemplate = tpl.html()+'';
			tpl.remove();
		}
	},
	
	printTemplates : function(){
		console.log('Print monitoring templates');
		let html = '';
		monitoring.printedServers = [];
		monitoring.data.servers.forEach(function(server, index){
			html += '<div server="'+server.tag+'">'+monitoring.lineTemplate+'</div>';
			monitoring.printedServers.push(server.tag);
		});
		$('.monitoring').html(html);
		monitoring.data.servers.forEach(function(server, index){
			let mon = $('.monitoring [server="'+server.tag+'"]');
			mon.find('[server_data="name"]').text(server.name);
			mon.find('[server_data="motd"]').text(server.motd);
			mon.find('[server_data="description"]').text(server.description);
		});
		monitoring.updateDisplay();
	},
	
	updateDisplay : function(){
		monitoring.data.servers.forEach(function(server, index){
			let mon = $('.monitoring [server="'+server.tag+'"]');
			let status = server.online ? server.currentPlayers+' из '+server.maxPlayers : 'Офлайн';
			mon.find('[server_data="status"]').text(status);
			let fill = Math.min(Math.round(server.currentPlayers*100/server.maxPlayers), 100);
			mon.find('[server_data="fill_percent"]').css({'width' : fill+'%'});
		});
		if(monitoring.data.summary.maxPlayers>0){
			let online = monitoring.data.summary.currentPlayers;
			let max = monitoring.data.summary.maxPlayers;
			$('.monitor_full_display').html(online+'<br> из <br>'+max);
			let height = Math.round(100-(online*100/max));
			height = Math.max(Math.min(height, 100), 0);
			$('.monitor_full .monitor_scale').css({'height' : height+'%'});
		}else{
			$('.monitor_full .monitor_full_display').html('OFF<br><div style="font-size: 20px;">*</div>');
			$('.monitor_full .monitor_scale').css({'height' : '100%'});
		}
	},
	
	checkTemplates : function(){
		let dataTags = [];
		monitoring.data.servers.forEach(function(server, index){
			dataTags.push(server.tag);
		});
		if(dataTags.length != monitoring.printedServers.length) return false;
		for(let i = 0; i < dataTags.length; i++){
			if(monitoring.printedServers[i] != dataTags[i]) return false;
		}
		return true;
	},
	
	refresh : function(){
		monitoring.loadData(function(){
			if(!monitoring.checkTemplates())
				monitoring.printTemplates();
			else
				monitoring.updateDisplay();
		});
	},
	
	vh : (function(){
		var stateKey, eventKey, keys = {
			hidden: "visibilitychange",
			webkitHidden: "webkitvisibilitychange",
			mozHidden: "mozvisibilitychange",
			msHidden: "msvisibilitychange"
		};
		for (stateKey in keys) {
			if (stateKey in document) {
				eventKey = keys[stateKey];
				break;
			}
		}
		return function(c) {
			if (c) document.addEventListener(eventKey, c);
			return !document[stateKey];
		}
	})(),
	
	start : function(){
		monitoring.initTemplate();
		let refreshTime = !isNaN(Cookies.get('mon_rel_delay')) ? Cookies.get('mon_rel_delay')*1000 : 20000;
		monitoring.refresh();
		monitoring.vh(()=>{
			if(monitoring.started){
				if (monitoring.vh()){
					if(!monitoring.updateTimerId){
						monitoring.refresh();
						monitoring.updateTimerId = setInterval(monitoring.refresh, refreshTime);
						console.log('Continue monitoring');
					}
				}else{
					if(monitoring.updateTimerId){
						clearInterval(monitoring.updateTimerId);
						monitoring.updateTimerId = 0;
						console.log('Monitoring paused');
					}
				}
			}
		});
		monitoring.updateTimerId = setInterval(monitoring.refresh, refreshTime);
		monitoring.started = true;
		console.log('Monitoring started');
		
	},
	
	stop : function(){
		clearInterval(monitoring.updateTimerId);
		monitoring.started = false;
		console.log('Monitoring stoped');
	}
	
}

monitoring.start();