$JQry(function() {
	
	$JQry("#osivia-modal").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.on("show.bs.modal", function(event) {
				var $target = $JQry(event.target),
                    $dialog = $target.find(".modal-dialog"),
					$header = $dialog.find(".modal-header"),
					$footer = $dialog.find(".modal-footer"),
					$clone = $target.children(".modal-clone"),
					$window = $dialog.find(".dyna-window"),
					//url = $target.data("load-url") + " .partial-refresh-window",
                    url = $target.data("load-url"),
					callbackFunction = $target.data("load-callback-function"),
					callbackFunctionArgs = $target.data("load-callback-function-args"),
					title = $target.data("title"),
					footer = $target.data("footer"),
                    size = $target.data("size");

				// Header
				if (title) {
					$header.find(".modal-title").text(title);
					$header.removeClass("hidden");
				}
				
				// Footer
				if (footer) {
					$footer.removeClass("hidden");
				}
				
				// Body
				$window.children().clone().appendTo($clone);
				$window.load(url, function() {
					if (callbackFunction) {
						window[callbackFunction](callbackFunctionArgs);
					}
				});

				// Size
                if ((size === "lg") || (size === "large")) {
                    $dialog.addClass("modal-lg");
                } else if ((size === "sm") || (size === "small")) {
                    $dialog.addClass("modal-sm");
                }
			});

			$element.on("hide.bs.modal", function(event) {
				var $target = $JQry(event.target),
					$window = $target.find(".dyna-window"),
					callbackFunction = $target.data("callback-function"),
					callbackFunctionArgs = $target.data("callback-function-args"),
					callbackUrl = $target.data("callback-url");
				
				$window.unbind("load");	
					
				if (callbackFunction) {
					window[callbackFunction](callbackFunctionArgs);
				}
				
				if (callbackUrl) {
					var container = null,
                        options = {
                            method : "post"
                        },
                        eventToStop = null,
                        callerId = null;
				
					directAjaxCall(container, options, callbackUrl, eventToStop, callerId);
				}
				
				$target.removeData("load-url");
				$target.removeData("load-callback-function");
				$target.removeData("load-callback-function-args");
				$target.removeData("callback-function");
				$target.removeData("callback-function-args");
				$target.removeData("callback-url");
				$target.removeData("title");
				$target.removeData("footer");
				$target.removeData("size");
			});
			
			$element.on("hidden.bs.modal", function(event) {
				var $target = $JQry(event.target),
                    $dialog = $target.find(".modal-dialog"),
					$header = $dialog.find(".modal-header"),
					$footer = $dialog.find(".modal-footer"),
					$clone = $target.children(".modal-clone"),
					$window = $dialog.find(".dyna-window");
				
				// Header
				$header.addClass("hidden");
				$header.find(".modal-title").empty();
				
				// Footer
				$footer.addClass("hidden");
				
				// Body
				$window.empty();
				$clone.children().appendTo($window);

				// Size
                $dialog.removeClass("modal-lg modal-sm");
			});
			
			$element.data("loaded", true);
		}
	});
	
	
	$JQry("[data-target='#osivia-modal']").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.click(function(event) {
				var $target = $JQry(event.target).closest("a, button"),
					loadUrl = $target.data("load-url"),
					loadCallbackFunction = $target.data("load-callback-function"),
					loadCallbackFunctionArgs = $target.data("load-callback-function-args"),
					callbackFunction = $target.data("callback-function"),
					callbackFunctionArgs = $target.data("callback-function-args"),
					callbackUrl = $target.data("callback-url"),
					title = $target.data("title"),
					footer = $target.data("footer"),
                    size = $target.data("size"),
					$modal = $JQry("#osivia-modal");
	
				$modal.data("load-url", loadUrl);
				$modal.data("load-callback-function", loadCallbackFunction);
				$modal.data("load-callback-function-args", loadCallbackFunctionArgs);
				$modal.data("callback-function", callbackFunction);
				$modal.data("callback-function-args", callbackFunctionArgs);
				$modal.data("callback-url", callbackUrl);
				$modal.data("title", title);
				$modal.data("footer", footer);
				$modal.data("size", size);

				$modal.modal("show");
			});
			
			$element.data("loaded", true);
		}
	});
	
	
	$JQry("#osivia-modal [data-close-modal=true]").each(function(index, element) {
		var $modal = $JQry("#osivia-modal");
		
		$modal.modal("hide");
	});
	
});
