$JQry(document).ready(function() {
	
	if (!$JQry("html").hasClass("no-touch")) {
		
		$JQry(".toolbar-menu-title").bind("touchstart", function(event) {
			var visibility = $JQry(this).siblings("ul").css("visibility");
			$JQry(".toolbar-menu ul").css("visibility", "");
			
			if ("visible" != visibility) {
				$JQry(this).siblings("ul").css("visibility", "visible");
			}
			
			event.stopPropagation();
		});		
		
		$JQry(document).bind("touchstart", function(event) {
			$JQry(".toolbar-menu ul").css("visibility", "");
		}); 
		
	}
	
});
