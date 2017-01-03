$JQry(function() {
	var $container = $JQry("#session-reload"),
		url = $container.data("url");

	if ($container.data("reload")) {
		$JQry.ajax({
			global: false, // Disable AJAX waiter
			url: url
		});
		
		$container.data("reload", false);
	}
	
});
