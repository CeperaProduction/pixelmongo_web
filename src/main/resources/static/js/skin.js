function Skin3D(){

	let skins = [];

	function init(container_id, skinUrl, capeUrl, transparent){
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
			'viewer' : skinViewer
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

	return {
		init, destroy, destroyAll
	}

}

const skin3d = new Skin3D();
