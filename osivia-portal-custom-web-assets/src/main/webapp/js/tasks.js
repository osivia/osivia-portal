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


/**
 * Tasks modal callback.
 */
function tasksModalCallback() {
	var $tasks = $JQry(".tasks").first(),
		tasksCount = $tasks.data("tasks-count");

	if (tasksCount !== undefined) {
		updateTasksCounter(tasksCount);
	}
}
