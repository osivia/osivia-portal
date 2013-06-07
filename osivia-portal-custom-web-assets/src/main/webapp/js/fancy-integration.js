

var callbackId = "";
var callbackUrl = "";

function callback( )	{

	var divElt = document.getElementById(callbackId);
	
	
	if( divElt != null)
		// reload portlet
		updatePortletContent( divElt, callbackUrl);
	else
		// reload full page
		window.location.replace(callbackUrl);

    
}

function setCallbackParams( id, url)	{

	callbackId = id;
	callbackUrl = url;
}


function asyncUpdatePortlet( windowId, url)	{
 	var divElt = document.getElementById(windowId);

	 if( divElt != null)
			// reload portlet
			updatePortletContent( divElt, url);
		else
			// reload full page
			window.location.replace(url);
}




var $JQry = jQuery.noConflict();

$JQry(document).ready(function() {
	
	$JQry(".fancyframe").fancybox({
 		'type':'iframe',
 		'width': 800, 
 		'height': 600
	});

	$JQry(".fancyframe_refresh").fancybox({
 		'type':'iframe',
 		'width': 800, 
 		'height': 600,
 		'beforeClose' : function() {
            callback();
		}
	});

	$JQry(".fancybox_inline").fancybox({
		'titlePosition'     : 'inside',
        'transitionIn'      : 'none',
        'transitionOut'     : 'none'
	});
	
	$JQry(".fancybox_inline_jstree").fancybox({
		'titlePosition'     : 'inside',
        'transitionIn'      : 'none',
        'transitionOut'     : 'none',
        'beforeLoad'		: function() {
        	jstreeOpenAll();
        	jstreeClearSearch();
        }
	});

    $JQry(".fancybox_inline_tabs").fancybox({
        'titlePosition' :   'outside',
        'transitionIn'	:	'elastic',
	    'transitionOut'	:	'elastic',
	    'speedIn'		:	600, 
	    'speedOut'		:	200, 
	    'overlayShow'	:	true
    });
    
});


function closeFancybox() {
	parent.jQuery.fancybox.close();
}

