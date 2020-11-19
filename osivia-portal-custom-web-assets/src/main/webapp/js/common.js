$JQry(function() {

    // Image error message
    $JQry("img[data-error-message]").on("error", function (event) {
        var $target = $JQry(event.target);
        var message = $target.data("error-message");
        var $message;
    
        if (message) {
            $message = $JQry(document.createElement("span"));
            $message.text(message);
    
            $target.replaceWith($message);
        }
    });
    
    // if a window is maximized, user loose focus il he enters a keyword
    // so we refresh the page directly on focus
    $JQry("input[data-restore-normal-view-on-focus]").on("focus", function (event) {
    	var $target = $JQry(event.target);
    	if( typeof returnToNormalModeURL !== 'undefined')	{
	        var url = returnToNormalModeURL;

        	var currentUrl_string = window.location.href
        	var currentUrl = new URL(currentUrl_string);
        	// the unset url has already be called, we are already in normal mode
        	// Should never occurs but we must avoid loops on server errors  .....
        	var unset = currentUrl.searchParams.get("unsetMaxMode");
        	if( ! (unset == 'true') )	{
        		var id = $target.attr('id');
        		if( id)
        			url = url + "&_autofocusOnLoading=" + id;
        		else
        			console.warn("no id has been set. Can't autofocus")
        		document.location.href=url; 
         	}

    	}
    });  
    
    // Autofocus on field (at the end)
	var currentUrl = new URL(window.location.href);   
	var autofocus = currentUrl.searchParams.get("_autofocusOnLoading");
	if( autofocus)	{
		 var $target = $JQry("#"+autofocus);
		 $target.focus();
		 var strLength = $target.val().length;
		 $target[0].setSelectionRange(strLength, strLength);
	}

});
