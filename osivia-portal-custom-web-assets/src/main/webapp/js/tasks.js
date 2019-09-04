$JQry(function() {
    var $tasks = $JQry(".tasks").first();
    var tasksCount = $tasks.data("tasks-count");

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
    var $button = $JQry("button[name='open-tasks']");
    var $label = $button.find(".counter .label");

    // Function .data() does not update DOM => CSS rules does not apply
    $button.attr("data-tasks-count", count);

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
    var $tasks = $JQry(".tasks").first();
    var tasksCount = $tasks.data("tasks-count");

    if (tasksCount !== undefined) {
        updateTasksCounter(tasksCount);
    }
}
