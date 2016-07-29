$JQry(function() {
	var $tasks = $JQry(".tasks").first(),
		reloadUrl = $tasks.data("reload-url"),
		tasksCount = $tasks.data("tasks-count");

	if ($tasks.data("reload")) {
		$JQry.ajax({
			url: reloadUrl
		});
		
		$tasks.data("reload", false);
	}
	
	if (tasksCount !== undefined) {
		updateTasksCounter(tasksCount);
	}
	
	
	// Open tasks modal
	$JQry("button[name='open-tasks']").each(function(index, element) {
		var $element = $JQry(element),
			loaded = $element.data("loaded");
		
		if (!loaded) {
			$element.click(function(event) {
				var $target = $JQry(event.target).closest("button"),
					loadUrl = $target.data("load-url"),
					title = $target.data("title"),
					$modal = $JQry("#osivia-modal");
	
				$modal.data("load-url", loadUrl);
				$modal.data("title", title);
				$modal.data("footer", true);
				
				$modal.modal("show");
			});
			
			$element.data("loaded", true);
		}
	});
	
});


/**
 * Update tasks counter.
 * 
 * @param count tasks count
 */
function updateTasksCounter(count) {
	var $button = $JQry("button[name='open-tasks']"),
		$label = $button.find(".counter .label");
	
	if (count > 0) {
		$label.removeClass("label-default");
		$label.addClass("label-danger");
		$label.text(count);
	} else {
		$label.removeClass("label-danger");
		$label.addClass("label-default");
		$label.text(0);
	}
}
