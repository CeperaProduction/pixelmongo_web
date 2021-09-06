jQuery(document).ready(function(){

jQuery('.cpmodal').fadeOut(0).attr({'hidden':'false'});

jQuery('.cpmodal_open').each(function(){
	jQuery(this).click(function(){
		cpmodalWindow(jQuery(this).attr('id').slice(1));
		});});

function cpmodalWindow (mod_window_id){

	
	var backgr = "<div id=\"cpmodal_back\" style=\"position: fixed; margin-left: -10%; margin-top: -20%; background: #000000; opacity: 0.7; width: 150%; height: 140%; z-index:5;\">1</div>";
	jQuery('body').prepend(backgr);
	
	var modalWindow = jQuery('#'+mod_window_id);
	
	var modalBack = jQuery('#cpmodal_back');
	modalBack.click(function(){
		jQuery('#cpmodal_back').fadeOut(450, function(){ modalBack.remove()});
		jQuery('.cpmodal').fadeOut(300, function(){jQuery('#cpmodal_close').remove()});
	});

	modalBack.fadeIn(900);
	modalWindow.fadeIn(300, function(){
		modalWindow.prepend("<div id=\"cpmodal_close\" title=\"Закрыть окно\"></div>");
		jQuery('#cpmodal_close').click(function(){
			jQuery('#cpmodal_back').fadeOut(450, function(){ modalBack.remove()});
			jQuery('.cpmodal').fadeOut(300, function(){jQuery('#cpmodal_close').remove()});
		});});};
});		