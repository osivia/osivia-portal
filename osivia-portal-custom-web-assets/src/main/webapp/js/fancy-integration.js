

var callbackId = "";
var callbackUrl = "";

/**
* manage callback after closing fancybox
*/
function callback( )	{

	if(callbackId) {
		var divElt = document.getElementById(callbackId);	
	}
	
	
	
	if(divElt) {
		// reload portlet
		//console.log("callback reload portlet " + callbackUrl);
			
		updatePortletContent( divElt, callbackUrl);
		
	}
	// load a new page
	else if (callbackUrlFromEcm) {
		
		var $f = jQuery('.fancybox-iframe');
		
		if($f && currentDocumentId) {

			var redirectUrl = callbackUrlFromEcm.replace('_NEWID_', currentDocumentId);
			
			if(redirectUrl) {
				//console.log("callback load a new page " + redirectUrl);
				
				window.location.replace(redirectUrl);
			}
		}
		
	}
	else {
		// reload full page
		if(!callbackUrl) {
			callbackUrl = document.URL; // if not specified, stay on the current page
		}
		
		//console.log("callback reload full page " + callbackUrl);
		window.location.replace(callbackUrl);

	}
}

var callbackUrlFromEcm = "";
var currentDocumentId = "";
var ecmBaseUrl ="";

/**
* Generic callback params
*/
function setCallbackParams( id, url)	{

	callbackId = id;
	callbackUrl = url;
}

/**
* Specific callback params for ECM conversation
*/
function setCallbackFromEcmParams(url, ecm)	{

	callbackUrlFromEcm = url;
	ecmBaseUrl = ecm;
	
	//setup a callback to handle the dispatched MessageEvent. if window.postMessage is supported the passed
	// event will have .data, .origin and .source properties. otherwise, it will only have the .data property.
	XD.receiveMessage(function(message)
			{
				receiveMessageAction(message);
			}
	 , ecmBaseUrl);
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
	parent.$JQry.fancybox.close();
}

/**
* Switch actions after recieving messages from ECM
*/
function receiveMessageAction(message) {
	
	console.log("message : " + message.data);
	
	if(message.data == 'closeFancyBox') {
		parent.$JQry.fancybox.close();
	}
	else if (message.data.match('currentDocumentId')) {
		currentDocumentId = message.data.replace('currentDocumentId=','');
	}
}
