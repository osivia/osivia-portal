/**
 * Logout.
 */
function logout() {
	var $disconnection = $JQry("#disconnection"),
		apps = $disconnection.data("apps").split("|"),
		redirection = $disconnection.data("redirection"),
		$container = $disconnection.find(".apps-container");
	
	// Modal
	$disconnection.modal({
		backdrop: "static",
		keyboard: false
	});
	$disconnection.modal("show");
	
	// Applications
	for (var i = 0; i < apps.length; i++) {
		$app = $JQry(document.createElement("img"));
		$app.attr("src", apps[i]);
		$container.append($app);
	}
	
	// Portal logout
	window.setTimeout(function() {
		document.location = redirection;
	}, 3000);
}
