$JQry(function() {
	
	$JQry("#osivia-modal").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.on("show.bs.modal", function(event) {
				var $target = $JQry(event.target),
					$header = $target.find(".modal-header"),
					$footer = $target.find(".modal-footer"),
					$clone = $target.children(".modal-clone"),
					$window = $target.find(".dyna-window"),
					url = $target.data("load-url") + " .partial-refresh-window",
					callbackFunction = $target.data("load-callback-function"),
					callbackFunctionArgs = $target.data("load-callback-function-args"),
					title = $target.data("title"),
					footer = $target.data("footer");
				
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
			});
			
			$element.on("hide.bs.modal", function(event) {
				var $target = $JQry(event.target),
					$window = $target.find(".dyna-window"),
					callbackFunction = $target.data("callback-function"),
					callbackFunctionArgs = $target.data("callback-function-args");
					callbackUrl = $target.data("callback-url");
				
				$window.unbind("load");	
					
				if (callbackFunction) {
					window[callbackFunction](callbackFunctionArgs);
				}
				
				if (callbackUrl) {
					container = null,
					options = {
						requestHeaders : [ "ajax", "true", "bilto" ],
						method : "post",
						onSuccess : function(t) {
							onAjaxSuccess(t, null);
						}
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
			});
			
			$element.on("hidden.bs.modal", function(event) {
				var $target = $JQry(event.target),
					$header = $target.find(".modal-header"),
					$footer = $target.find(".modal-footer"),
					$clone = $target.children(".modal-clone"),
					$window = $target.find(".dyna-window");
				
				// Header
				$header.addClass("hidden");
				$header.find(".modal-title").empty();
				
				// Footer
				$footer.addClass("hidden");
				
				// Body
				$window.empty();
				$clone.children().appendTo($window);
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
					$modal = $JQry("#osivia-modal");
	
				$modal.data("load-url", loadUrl);
				$modal.data("load-callback-function", loadCallbackFunction);
				$modal.data("load-callback-function-args", loadCallbackFunctionArgs);
				$modal.data("callback-function", callbackFunction);
				$modal.data("callback-function-args", callbackFunctionArgs);
				$modal.data("callback-url", callbackUrl);
				$modal.data("title", title);
				$modal.data("footer", footer);

				$modal.modal("show");
			});
			
			$element.data("loaded", true);
		}
	});
	
});
