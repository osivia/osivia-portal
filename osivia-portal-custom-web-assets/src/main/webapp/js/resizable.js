$JQry(function() {

	$JQry("[data-resizable]").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded");

		if (!loaded) {
			$element.resizable({
				distance : 0,
				handles : "e",
				minWidth : ($element.data("min-width") ? $element.data("min-width") : 10),
				maxWidth : ($element.data("max-width") ? $element.data("max-width") : null),
				

				create : function(event, ui) {
					var $target = $JQry(event.target);
					
					$target.css({
						width : $target.outerWidth()
					});
					
					$target.addClass("loaded");
				},

				stop : function(event, ui) {
					var $target = $JQry(event.target),
						originalWidth = ui.originalSize.width,
						width = ui.size.width,
						container = null,
						options = {
							method : "post"
						},
						url = $target.data("save-url"),
						callerId = null;
					
					if ((originalWidth != width) && url) {
						url += "&width=" + $target.outerWidth();
						
						// AJAX request
						directAjaxCall(container, options, url, event, callerId);
					}
				}
			});

			$element.data("loaded", true);
		}
	});

});
