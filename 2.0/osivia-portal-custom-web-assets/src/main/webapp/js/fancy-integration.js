

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
	});
	
	
	
	$JQry(document).ready(function() {
		$JQry(".fancyframe_refresh").fancybox({
	 		'type':'iframe',
	 		'width': 800, 
	 		'height': 600,
	 		'beforeClose'	:	function() {
	           // alert('Closed!');
	            callback();
			}
		});
	});
	
	$JQry(document).ready(function() {
		$JQry(".fancybox_inline").fancybox({
			'titlePosition'     : 'inside',
            'transitionIn'      : 'none',
            'transitionOut'     : 'none'	 		
		});
	});
	
	// Affichage d'une fancybox inline sans titre
	$JQry(document).ready(function() {
		var fancybox_no_title = $JQry(".fancybox-no-title");
		var fntDefined = typeof(fancybox_no_title) != 'undefined';
		var fancybox_inline = $JQry(".fancybox_inline");
		var fDefined = typeof(fancybox_inline) != 'undefined';
		/* Trouver autre critère d'égalité */
		var equals = fancybox_no_title.context == fancybox_inline.context;
		if(fntDefined && fDefined && equals){	
			$JQry(".fancybox_inline").fancybox({
				helpers: { 
			        title: null
			    },
	            'transitionIn'      : 'none',
	            'transitionOut'     : 'none'	 		
			});
		}
	});

