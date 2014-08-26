var $JQry = jQuery.noConflict();


// Accessible dropdown menu without Javascript
$JQry(document).ready(function($) {
	$("*").removeClass("accessible-dropdown-menu");
});



// Responsive tabs menu & navigation menu
$JQry(document).ready(function($) {
	$("[data-toggle=tabs-menu]").click(function() {
		$("#tabs-menu").toggleClass("active");
    });
	
	$("[data-toggle=navigation-menu]").click(function () {
		$("#navigation-menu").toggleClass("active");
	});
});



// Drawer
$JQry(document).ready(function($) {
	$("[data-toggle=drawer]").click(toggleDrawer);
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



// Tooltips initialization
$JQry(document).ready(function($) {
	$("[data-toggle=tooltip]").tooltip({container: 'body'});
});

