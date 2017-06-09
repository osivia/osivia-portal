$JQry(function() {
	$JQry(".bxslider.bxslider-default").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded"),
			pause = $element.data("pause");
		
		if (!loaded) {
			if (pause) {
				pause *= 1000;
			} else {
				pause = 4000;
			}
			
			$element.bxSlider({
				// General
				mode : "horizontal",  // Type of transition between slides
				
				// Controls
				autoControls : true,  // If true, "Start" / "Stop" controls will be added
				
				// Auto
				auto : true,  // Slides will automatically transition
				pause : pause,  // The amount of time (in ms) between each auto transition
				autoHover : true,  // Auto show will pause when mouse hovers over fgtSlider
				autoDelay : 3000  // Time (in ms) auto show should wait before starting
			});
			
			$element.data("loaded", true);
		}
	});
	
});
