$JQry(function() {
	
	$JQry("input[data-change-submit], textarea[data-change-submit], select[data-change-submit]").each(function(index, element) {
		var $element = $JQry(element);

		if (!$element.data("loaded")) {
			$element.change(function(event) {
				var $target = $JQry(event.target);
				var $submit = $JQry("#" + $target.data("change-submit"));
				
				$submit.click();
			});
			
			$element.data("loaded", true);
		}
	});
	
});
