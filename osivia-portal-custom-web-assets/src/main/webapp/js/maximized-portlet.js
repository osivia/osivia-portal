$JQry(function() {
	var $toolbarCollapse = $JQry("#maximized .portlet-container .portlet-toolbar .collapse");
	
	adjustMaximizedPortletHeight();
	
	
	// Adjust maximized portlet height after collapse event
	$toolbarCollapse.on("shown.bs.collapse", function() {
		adjustMaximizedPortletHeight();
	});
	$toolbarCollapse.on("hidden.bs.collapse", function() {
		adjustMaximizedPortletHeight();
	});
	
});


//Adjust maximized portlet height on window resize
$JQry(window).resize(function() {
	adjustMaximizedPortletHeight();
});


/**
 * Adjust maximized portlet height.
 */
function adjustMaximizedPortletHeight() {
	var $window = $JQry(window),
		$container = $JQry("#maximized .portlet-container"),
		$filler = $container.find(".portlet-filler");
		
	if ($filler.length) {
		var $toolbar = $container.find(".portlet-toolbar"),
			$table = $filler.closest("#maximized .portlet-container .table"),
			toolbarHeight, fillerHeight, scrollbarWidth;
					
		// Compute toolbar height
		if ($toolbar.length) {
			toolbarHeight = Math.round($toolbar.innerHeight());
		} else {
			toolbarHeight = 0;
		}
		
		// Compute filler height
		if ($window.width() >= 768) {
			var margin = $container.outerHeight(true) - $container.outerHeight(false);
			
			fillerHeight = Math.round($window.height() - $filler.offset().top - toolbarHeight - margin);
			fillerHeight = Math.max(fillerHeight, 250);
		} else {
			fillerHeight = "auto";
		}
		
		// Update filler CSS
		$filler.css({
			height: fillerHeight,
			"margin-bottom": toolbarHeight
		});
		
		// Compute scrollbar width
		scrollbarWidth = Math.round($filler.innerWidth() - $filler.children().outerWidth(true));
		
		// Update toolbar CSS
		if ($toolbar.hasClass("adapt-scrollbar")) {
			$toolbar.css({
				"padding-right": scrollbarWidth
			});
		}
		
		// Update table CSS
		if ($table.length) {
			var $tableHeader = $table.find(".table-header"),
				$row = $tableHeader.find(".row").first();

			$row.css({
				"padding-right": scrollbarWidth
			});
		}
		
		// Update taskbar container height
		updateTaskbarStyles($JQry(".taskbar-container"));
	}
}
