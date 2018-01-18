$JQry(function() {
	$JQry("body.fixed-layout .portlet-filler").each(function(index, element) {
		var $element = $JQry(element);
		
		if (!$element.closest(".scrollbox.fixed-scrollbox").length) {
			// Portlet filler parents flexbox class
			$element.parentsUntil(".flexbox").addClass("flexbox");
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
	var $window = $JQry(window),
		$portletFiller = $JQry("body.fixed-layout .portlet-filler");
	
	$portletFiller.each(function(index, element) {
		var $element = $JQry(element),
			$table = $element.closest(".table"),
			$tableHeader = $table.find(".table-header"),
			scrollbarWidth;
	
		if ($window.width() >= 768) {
			scrollbarWidth = Math.round($element.innerWidth() - $element.children().outerWidth(true));
		} else {
			scrollbarWidth = 0;
		}
		
		// Update table header
		$tableHeader.find(".row").first().css({
			"padding-right": scrollbarWidth
		});
	});
}
