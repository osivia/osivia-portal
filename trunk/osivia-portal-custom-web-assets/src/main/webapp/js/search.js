$JQry(function() {
	
	$JQry("form[role=search]").submit(function(event) {
		var $target = $JQry(event.target),
			$keywords = $target.find("input[name=keywords]"),
			action = $target.attr("action");
		
		action = action.replace("__REPLACE_KEYWORDS__", $keywords.val());
		
		$target.attr("action", action);
	});
	
});
