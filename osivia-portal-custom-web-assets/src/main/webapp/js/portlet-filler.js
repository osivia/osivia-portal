$JQry(function() {
	var $portletFiller = $JQry("body.fixed-layout .portlet-filler");
	
	if (!$portletFiller.closest(".scrollbox.fixed-scrollbox").length) {
		// Portlet filler parents flexbox class
		$portletFiller.parentsUntil(".flexbox").addClass("flexbox");
	}
	
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
	var $window = $JQry(window),
		$portletFiller = $JQry("body.fixed-layout .portlet-filler");
	
	$portletFiller.each(function(index, element) {
		var $element = $JQry(element),
			width = Math.round($element.innerWidth() - $element.children().outerWidth(true)),
			$table = $element.closest(".table"),
			$tableHeader = $table.find(".table-header");
		
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
