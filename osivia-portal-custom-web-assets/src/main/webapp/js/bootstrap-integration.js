
$JQry(document).ready(function() {
	
	// Accessible dropdown menu without Javascript
	$JQry(".accessible-dropdown-menu").removeClass("accessible-dropdown-menu");

	
	// Tooltips initialization
	$JQry("[data-toggle=tooltip]").tooltip({container: 'body'});
	$JQry(document).click(function(event) {
		$JQry("[data-toggle=tooltip]").tooltip("hide");
	});
	
	// Popovers initialization
	$JQry("[data-toggle=popover]").popover({container: 'body'});
	
	
	// Forms in dropdown menus
	$JQry(".dropdown-menu .form").click(function (event) {
		event.stopPropagation();
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
	var $toolbar = $JQry("#drawer-toolbar"),
		$search = $toolbar.find(".drawer-toolbar-search");
	
	$search.addClass("active");
}
function hideDrawerSearch() {
	var $toolbar = $JQry("#drawer-toolbar"),
		$search = $toolbar.find(".drawer-toolbar-search");
	
	$search.removeClass("active");
}
