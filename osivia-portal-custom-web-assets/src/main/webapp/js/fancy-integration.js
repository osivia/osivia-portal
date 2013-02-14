

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
