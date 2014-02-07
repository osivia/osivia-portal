/**
 * Toggle commands display.
 * 
 * @param id commands container identifier
 */
function toggleCommands(id) {
	var component = document.getElementById(id).component;	
	var selection = component.getSelection();
	if (selection.length == 0) {
		$JQry(".administration-command-off").css("display", "inline");
		$JQry(".administration-command-on").css("display", "none");
	} else {
		$JQry(".administration-command-off").css("display", "none");
		$JQry(".administration-command-on").css("display", "inline");
	}
}