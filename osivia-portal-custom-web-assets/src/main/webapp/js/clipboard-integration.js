$JQry(document).ready(function() {
	
	// Clipboard initialization
	var clipBoards = new Clipboard("[data-clipboard-target]"); 
	
	if(clipBoards){
	
		clipBoards.on('success', function(event) { 
			// Get message element
			var msgTargetId = $JQry(event.trigger).attr("data-clipboard-message"); 
			var $msgTarget = $JQry(msgTargetId);
			
			$msgTarget.removeClass("visibilty-hidden");
			$msgTarget.fadeOut(2000, function() {
				$msgTarget.addClass("visibilty-hidden");
				$msgTarget.attr("style","");
			});
		});
	
	}
	
});