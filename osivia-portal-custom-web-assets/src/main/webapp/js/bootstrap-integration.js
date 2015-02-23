
// Accessible dropdown menu without Javascript
$JQry(document).ready(function() {
	$JQry("*").removeClass("accessible-dropdown-menu");
});



// Responsive tabs menu & navigation menu
$JQry(document).ready(function() {
	$JQry("[data-toggle=tabs-menu]").click(function() {
		$JQry("#tabs-menu").toggleClass("active");
    });
	
	$JQry("[data-toggle=navigation-menu]").click(function () {
		$JQry("#navigation-menu").toggleClass("active");
	});
});



// Drawer
$JQry(document).ready(function() {
	$JQry("[data-toggle=drawer]").click(toggleDrawer);
});
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
	$drawer.addClass("active");
	
	// Shadowbox
	var shadowbox = document.createElement("div");
	shadowbox.id = "drawer-shadowbox";
	$JQry("body").append(shadowbox);
	var $shadowbox = $JQry(shadowbox);
	$shadowbox.fadeTo(300, 0.6);
	$shadowbox.bind("tap", hideDrawer);
	
	// Toggle button
	$JQry("[data-toggle=drawer]").addClass("active-drawer");
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


// Tooltips initialization
$JQry(document).ready(function() {
	$JQry("[data-toggle=tooltip]").tooltip({container: 'body'});
});


// Popovers initialization
$JQry(document).ready(function() {
	$JQry("[data-toggle=popover]").popover({container: 'body'});
});


// Forms in dropdown menus
$JQry(document).ready(function() {
	$JQry(".dropdown-menu .form").click(function (e) {
		e.stopPropagation();
	});
});

