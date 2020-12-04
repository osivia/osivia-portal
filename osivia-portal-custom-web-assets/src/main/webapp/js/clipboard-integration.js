$JQry(function() {
	var clipboard = new Clipboard("[data-clipboard-target]");
	
	// Tooltip

	function setTooltip(input, message) {
		input.tooltip('hide')
	    .attr('title', message)
	    .tooltip('show');
	}

	function hideTooltip(input) {
	  setTimeout(function() {
		  input.tooltip('dispose');
	  }, 2500);
	}

	// Clipboard

	clipboard.on('success', function(e) {
	  var $element = $JQry(e.trigger);
	  var message = $element.data("clipboard-message-success");
	  

	  if( message !== undefined)	{
		  var $input = $JQry($element.data("clipboard-target"));
		  setTooltip($input,message);
		  hideTooltip($input);
	  }
	});


	
});
