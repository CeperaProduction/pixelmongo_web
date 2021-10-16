var monitoring = {

	url : baseUrl+"open/monitoring",

	roundColors : {
		fill : 'rgb(80,240,80)',
		back : 'rgb(255,255,255)'
	},

	started : false,

	refreshTime : 5000,

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
				url: monitoring.url,
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
		let tpl = $('#monitoring-template');
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
			html += '<div data-server="'+server.tag+'">'+monitoring.lineTemplate+'</div>';
			monitoring.printedServers.push(server.tag);
		});
		$('.monitoring').html(html);
		monitoring.data.servers.forEach(function(server, index){
			let mon = $('.monitoring [data-server="'+server.tag+'"]');
			mon.find('[data-server-info="name"]').text(server.name);
			mon.find('[data-server-info="motd"]').text(server.motd);
			mon.find('[data-server-info="description"]').text(server.description);
		});
		monitoring.updateDisplay();
	},

	updateDisplay : function(){
		monitoring.data.servers.forEach(function(server, index){
			let mon = $('.monitoring [data-server="'+server.tag+'"]');
			let status = server.online ? server.currentPlayers+' из '+server.maxPlayers : 'Офлайн';
			mon.find('[data-server-info="status"]').text(status);
			let fill = Math.min(Math.round(server.currentPlayers*100/server.maxPlayers), 100);
			mon.find('[data-server-info="fill-percent"]').css({'width' : fill+'%'});
			mon.find('[data-server-info="fill-percent-invert"]').css({'width' : (100-fill)+'%'});
		});
		let fill = 0;
		if(monitoring.data.summary.maxPlayers>0){
			let online = monitoring.data.summary.currentPlayers;
			let max = monitoring.data.summary.maxPlayers;
			$('.monitor-full-display').html(online+' из '+max);
			fill = Math.max(Math.min(online/max, 1), 0);
		}else{
			$('.monitor-full .monitor-full-display').html('OFF');
		}
		let [bg1, bg2] = monitoring.calcRoundBgs(fill, monitoring.roundColors.fill, monitoring.roundColors.back);
		let round1 = $('.monitor-full #round1.monitoring-round');
		let round2 = $('.monitor-full #round2.monitoring-round');
		round1.css({'background-image' : bg1});
		round2.css({'background-image' : bg2});
	},

	calcRoundBgs : function(fill, fillColor, backColor){
		var bg1 = 'none';
		var bg2 = 'none';
		if(fill == 0){
			bg1 = 'linear-gradient(90deg, '+backColor+' 0%, '+backColor+' 100%)';
		}else if(fill == 0.5){
			bg1 = 'linear-gradient(90deg, '+backColor+' 0%, '+backColor+' 50%, '+fillColor+' 50%, '+fillColor+' 100%)';
		}else if(fill >= 1){
			bg1 = 'linear-gradient(90deg, '+fillColor+' 0%, '+fillColor+' 100%)';
		}else if(fill < 0.5){
			bg1 = 'linear-gradient('+((360 * fill) - 90)+'deg, '+backColor+' 0%, '+backColor+' 50%, '+fillColor+' 50%, '+fillColor+' 100%)';
			bg2 = 'linear-gradient(90deg, '+backColor+' 0%, '+backColor+' 50%, rgba(0,0,0,0) 50%, rgba(0,0,0,0) 100%)';
		}else{
			bg1 = 'linear-gradient('+((360 * fill) - 90)+'deg, '+backColor+' 0%, '+backColor+' 50%, '+fillColor+' 50%, '+fillColor+' 100%)';
			bg2 = 'linear-gradient(270deg, '+fillColor+' 0%, '+fillColor+' 50%, rgba(0,0,0,0) 50%, rgba(0,0,0,0) 100%)';
		}
		return [bg1, bg2];
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
		monitoring.refresh();
		monitoring.vh(()=>{
			if(monitoring.started){
				if (monitoring.vh()){
					if(!monitoring.updateTimerId){
						monitoring.refresh();
						monitoring.updateTimerId = setInterval(monitoring.refresh, monitoring.refreshTime);
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
		monitoring.updateTimerId = setInterval(monitoring.refresh, monitoring.refreshTime);
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