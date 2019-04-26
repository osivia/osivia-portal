$JQry(function() {
	var $container = $JQry("#session-reload");

	if( $container.length){
		var urls = $container.data("urls").split("|");

		if ($container.data("reload")) {
			for (var i = 0; i < urls.length; i++) {
				$JQry.ajax({
					global : false, // Disable AJAX waiter
					url : urls[i]
				});
			}

			$container.data("reload", false);
		}
	}
});
