$JQry(function() {
	
	$JQry(".notifications-container .alert[data-apart]").each(function(index, element) {
		var $element = $JQry(element);
		var delay = 5000 + (200 * index);
		var timerApart;

		
		timerApart = setTimeout(setApart, delay);

		
		$element.mouseenter(function() {
			// Clear timer
			clearTimeout(timerApart);
		});

		
		$element.mouseleave(function() {
			// Reset timer
			timerApart = setTimeout(setApart, 5000);
		});

		
		/**
		 * Set notification apart.
		 */
		function setApart() {
			var timerShaded;

			$element.addClass("apart");

			timerShaded = setTimeout(close, 3000);

			$element.mouseenter(function() {
				// Clear timer
				clearTimeout(timerShaded);
			});

			$element.mouseleave(function() {
				// Reset timer
				timerShaded = setTimeout(close, 3000);
			});
		}

		
		/**
		 * Close notification.
		 */
		function close() {
			$element.fadeOut(200, function() {
				$element.alert("close");
			});
		}
	});

});
