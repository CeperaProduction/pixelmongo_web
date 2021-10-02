

$(function(){
	var c = $('canvas#error-background');
	if(c.length){
		c = c.get(0);
		let letters = Array(128).join(1).split('');;
		let scale = 1;
		let body = $('body');
		function resize(){
			c.width  = body.innerWidth();
  			c.height = body.innerHeight()-5;
			let nscale = Math.ceil(c.width / 1280);
			if(nscale != scale){
				scale = nscale;
				letters = Array(128*scale).join(1).split('');
			}
		}
		function draw() {
			var width  = c.width;
			var height = c.height;
			c.getContext('2d').fillStyle='rgba(0,0,0,.05)';
			c.getContext('2d').fillRect(0,0,width,height);
			c.getContext('2d').fillStyle='#54a6ff';
			letters.map(function(y_pos, index){
				text = String.fromCharCode(65+Math.random()*33);
				let x_pos = index * 10;
				c.getContext('2d').fillText(text, x_pos, y_pos);
				letters[index] = (y_pos > (380 + Math.random() * 5e3)*scale) ? 0 : y_pos + 10;
			});
		};
		resize();
		$(window).on('resize', resize);
		setInterval(draw, 33);
	}
});

