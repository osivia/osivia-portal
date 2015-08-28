var $sliderDefault;

$JQry(document).ready(function() {
	
	if (typeof $sliderDefault === "undefined") {
		$sliderDefault = $JQry(".bxslider.bxslider-default").bxSlider({
			// Controls
			autoControls : true,  // If true, "Start" / "Stop" controls will be added
			
			// Auto
			auto : true  // Slides will automatically transition
		});
	}
	
});
