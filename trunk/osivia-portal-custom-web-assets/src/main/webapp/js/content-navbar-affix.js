// Content navbar affix top value
var contentNavbarAffixTop;


$JQry(function() {
	
	$JQry(".content-navbar-affix").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded"),
			$container = $element.closest(".content-navbar-affix-container");

		
		// Navbar height
		$container.css({
			height: $element.outerHeight(true)
		});
		
		
		// Affix
		if (!loaded) {
			updateContentNavbarAffixValues($container);
			
			$element.affix({
				offset: {
					top: function() {
						return contentNavbarAffixTop;
					}
				}
			});
			
			$element.data("loaded", true);
		}
	});
	
});


//Reset navbar height and check affix position on window resizing
$JQry(window).resize(function() {
	$JQry(".content-navbar-affix").each(function(index, element) {
		var $element = $JQry(element),
			$container = $element.closest(".content-navbar-affix-container");

		$container.css({
			height: $element.outerHeight(true)
		});
		
		updateContentNavbarAffixValues($container);
		
		$element.affix("checkPosition");
	});
});


/**
 * Update content navbar affix values.
 * 
 * @param $container affix container
 */
function updateContentNavbarAffixValues($container) {
	if ($JQry(window).width() >= 768) {
		contentNavbarAffixTop = Math.round($container.offset().top);
	} else {
		contentNavbarAffixTop = 1;
	}
}
