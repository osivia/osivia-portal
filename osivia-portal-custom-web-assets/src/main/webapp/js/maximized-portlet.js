$JQry(function() {
	adjustMaximizedPortletHeight();
});


$JQry(window).resize(function() {
	adjustMaximizedPortletHeight();
});


function adjustMaximizedPortletHeight() {
	var $window = $JQry(window),
		$container = $JQry("#maximized .portlet-container"),
		$filler = $container.find(".portlet-filler");
		
	if ($filler.length) {
		var $toolbar = $container.find(".portlet-toolbar"),
			$table = $filler.closest("#maximized .portlet-container .table"),
			toolbarHeight,
			fillerHeight;
					
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
		
		// Update CSS
		$filler.css({
			height: fillerHeight,
			"margin-bottom": toolbarHeight
		});
		
		// Update table CSS
		if ($table.length) {
			var $tableHeader = $table.find(".table-header"),
				$row = $tableHeader.find(".row").first(),
				scrollbarWidth = Math.min(12, Math.round($filler.width() - $filler.children().width()));

			$row.css({
				"padding-right": scrollbarWidth
			});
		}
		
		// Update taskbar container height
		updateTaskbarStyles($JQry(".taskbar-container"));
	}
}
