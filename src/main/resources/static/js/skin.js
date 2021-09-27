function Skin3D(){

	let skins = [];

	function init(container_id, skinUrl, capeUrl){
		destroy(container_id);

		container = document.getElementById(container_id);
		skinViewer = new skinview3d.SkinViewer(container, {
			width: container.offsetWidth+container.offsetWidth/5,
			height: container.offsetHeight
		});
		skins[container_id] = {
			'id' : container_id,
			'container' : container,
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
