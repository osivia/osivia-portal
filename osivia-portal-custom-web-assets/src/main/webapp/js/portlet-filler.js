$JQry(function() {
	$JQry("body.vh-100 .portlet-filler").each(function(index, element) {
		var $element = $JQry(element);
		
		if (!$element.closest(".scrollbox.fixed-scrollbox").length) {
			// Portlet filler parents flexbox class
			$element.parentsUntil(".flex-grow-1").addClass("flex-grow-1");
		}
	});
	
	// Update scrollbar width
	updateScrollbarWidth();
});


// Update scrollbar width on window resize
$JQry(window).resize(function() {
	updateScrollbarWidth();
});


/**
 * Update scrollbar width.
 */
function updateScrollbarWidth() {
	var $portletFiller = $JQry("body.vh-100 .portlet-filler");
	
	$portletFiller.each(function(index, element) {
		var $element = $JQry(element);
		var width = Math.round($element.innerWidth() - $element.children().outerWidth(true));
		var $table = $element.closest(".table");
		var $tableHeader = $table.find(".table-header");
		
		if ($element.hasClass("hidden-scrollbar")) {
			// Force scrollbar display
			$element.css("overflow-y", "scroll");
			
			// Update scrollbar width
			width = Math.round($element.innerWidth() - $element.children().outerWidth(true));
			
			// Update negative margin for hidden scrollbar
			$element.css("margin-right", -width);
		}
		
		// Update table header
		$tableHeader.find(".row").first().css({
			"padding-right": width
		});
	});
}
