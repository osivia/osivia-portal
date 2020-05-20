/**
 * Detect portlet change
 * @returns
 */

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
	
	var $bells = $JQry("a.task-bell");
	
	$bells.each(function( index ) {
    	  if (count > 0) {
    		    $JQry(this).addClass("text-warning");
    	    } else {
    	    	$JQry(this).removeClass("text-warning");
    	    }    	  
    });
	
	
    var $glyphs = $JQry("a.task-bell i");
    $glyphs.each(function( index ) {

    	  if (count > 0) {
    		    $JQry(this).removeClass("glyphicons-basic-bell");
    		    $JQry(this).addClass("glyphicons-basic-bell-ringing");
    	    } else {
    	    	$JQry(this).removeClass("glyphicons-basic-bell-ringing");
    	    	$JQry(this).addClass("glyphicons-basic-bell");
    	    }    	  
    });

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
