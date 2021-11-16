function Skin3D(){

	let skins = [];
	let prepared = [];

	function prepare(container_id, skinUrl, capeUrl, transparent, skin2dUrl){
		prepared.push({
			container_id : container_id,
			skinUrl : skinUrl,
			capeUrl : capeUrl,
			transparent : transparent,
			skin2dUrl : skin2dUrl,
		});
	}

	function initPrepared(){
		for(let i = prepared.length-1; i >= 0; i--){
			let data = prepared[i];
			try{
				init(data.container_id, data.skinUrl, data.capeUrl, data.transparent, data.skin2dUrl);
			}catch(e){
				console.log(e);
				if(data.skin2dUrl){
					skin3d.skin2d(data.container_id, data.skin2dUrl);
				}
			}
		}
	}

	function init(container_id, skinUrl, capeUrl, transparent, skin2dUrl){
		destroy(container_id);

		let container = document.getElementById(container_id);

		let canvas = document.createElement("canvas");
		canvas.id = container_id+'-canvas';
		canvas.width = container.offsetWidth;
		canvas.height = container.offsetHeight;
		container.prepend(canvas);

		let s3d = skinview3d.FXAASkinViewer;
		if(transparent){
			s3d = skinview3d.SkinViewer;
		}

		let skinViewer = new s3d({
			canvas: canvas,
			width: canvas.width,
			height: canvas.height
		});

		skinViewer.fov = 30;

		skins[container_id] = {
			'id' : container_id,
			'container' : container,
			'canvas' : canvas,
			'viewer' : skinViewer,
			'sizes' : [canvas.width, canvas.height]
		};

		skinViewer.loadSkin(skinUrl).catch((e)=>{
			console.log(e);
		});
		skinViewer.loadCape(capeUrl).catch(()=>{});

		skinViewer.camera.position.z = 70;

		skinViewer.animations.speed = 0.7;
		skinViewer.animations.add(skinview3d.WalkingAnimation);
		skinViewer.animations.add(skinview3d.RotatingAnimation);

		if(!transparent){
			container.className += " fxaa";
		}

		canvas.style.maxWidth = canvas.style.width;
		canvas.style.width = '100%';
		canvas.style.height = 'auto';
		container.style.maxWidth = container.style.width;
		container.style.width = '';
		container.style.height = 'auto';

		if(skin2dUrl){
			canvas.addEventListener('webglcontextlost', function(e) {
				console.log("WebGL context for skin '"+container_id+"' destroyed. Loading skin2d.")
			  	skin2d(container_id, skin2dUrl);
			}, false);
		}
	}

	function destroy(container_id){
		if(skins[container_id] && (skins[container_id].viewer instanceof skinview3d.SkinViewer)){
			skins[container_id].viewer.dispose();
		}
		skins[container_id] = undefined;
	}

	function destroyAll(){
		skins.forEach(function(s,id){
			if(s.viewer instanceof skinview3d.SkinViewer){
				s.viewer.dispose();
			}
			skins[id] = undefined;
		});
	}

	function skin2d(container_id, skinBodyUrl){
		let skin2d = '<div id="skin2d" class="skin2d"><div class="skin" style="background-image: url(\''+skinBodyUrl+'\');"></div></div>';
		document.getElementById(container_id).innerHTML = skin2d;
	}

	document.addEventListener("DOMContentLoaded", function(){
		initPrepared();
	});

	return {
		init, destroy, destroyAll, prepare, skin2d
	}

}

const skin3d = new Skin3D();
