
$JQry(document).ready(function() {
	
	// Accessible dropdown menu without Javascript
	$JQry("*").removeClass("accessible-dropdown-menu");

	
	// Tooltips initialization
	$JQry("[data-toggle=tooltip]").tooltip({container: 'body'});
	// Popovers initialization
	$JQry("[data-toggle=popover]").popover({container: 'body'});
	
	
	// Forms in dropdown menus
	$JQry(".dropdown-menu .form").click(function (e) {
		e.stopPropagation();
	});


// Content navbar affix
	$JQry(".content-navbar-affix").each(function(index, element) {
		var $element = $JQry(element),
			$container = $element.closest(".content-navbar-affix-container");

		
		// Navbar height
		$container.css({
			height: $element.outerHeight(true)
		});
		
		
		// Affix
		$element.affix({
			offset: {
				top: function() {
					if (document.body.clientWidth >= 768) {
						return Math.round($container.offset().top);
					} else {
						return 0;
					}
				}
			}
		});
	});
	
	
	// Comments
	$JQry(".comments .collapse").on("show.bs.collapse", function(event) {
		$JQry(".comments .collapse.in").not(event.target).collapse("hide");
	});
	
	
	// Modal
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
});


// Drawer
function toggleDrawer() {	
	var $drawer = $JQry("#drawer");
	if ($drawer.hasClass("active")) {
		// Hide
		hideDrawer();
	} else {
		// Show
		showDrawer();
	}
}
function showDrawer() {
	var $drawer = $JQry("#drawer");
	if ($drawer.length > 0) {
		$drawer.addClass("active");
		
		// Shadowbox
		if ($JQry("#drawer-shadowbox").length == 0) {
			var shadowbox = document.createElement("div");
			shadowbox.id = "drawer-shadowbox";
			$JQry("body").append(shadowbox);
			var $shadowbox = $JQry(shadowbox);
			$shadowbox.fadeTo(300, 0.6);
			$shadowbox.bind("tap", hideDrawer);
		}
		
		// Toggle button
		$JQry("[data-toggle=drawer]").addClass("active-drawer");
	}
}
function hideDrawer() {
	var $drawer = $JQry("#drawer");
	$drawer.removeClass("active");
	
	// Shadowbox
	var $shadowbox = $JQry("#drawer-shadowbox");
	$shadowbox.fadeTo(300, 0, function() {
		$shadowbox.remove();
	});
	
	// Toggle button
	$JQry("[data-toggle=drawer]").removeClass("active-drawer");
}
$JQry(window).on("swiperight", function(event) {
	if (event.swipestart.coords[0] < 50) {
		showDrawer();
	}
});
$JQry(window).on("swipeleft", function(event) {
	hideDrawer();
});


// Drawer toolbar
function showDrawerSearch() {
	var $toolbar = $JQry("#drawer-toolbar");
	var $search = $toolbar.find(".drawer-toolbar-search");
	
	$search.addClass("active");
}
function hideDrawerSearch() {
	var $toolbar = $JQry("#drawer-toolbar");
	var $search = $toolbar.find(".drawer-toolbar-search");
	
	$search.removeClass("active");
}
