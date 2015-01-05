
var callbackId = "";
var callbackUrl = "";

var callbackUrlFromEcm = "";
var currentDocumentId = "";
var live = "";
var notificationKey = "";
var ecmBaseUrl = "";
//var liveEditLink = "";


/**
 * manage callback after closing fancybox
 */
function callback( )	{
	if (callbackId) {
		var divElt = document.getElementById(callbackId);	
	}

	if (divElt) {
		// reload portlet
		updatePortletContent( divElt, callbackUrl);
		
	} else if (callbackUrlFromEcm) {
		// load a new page
		var $f = jQuery('.fancybox-iframe');
		
		if ($f && currentDocumentId) {
			var redirectUrl = callbackUrlFromEcm.replace('_NEWID_', currentDocumentId);
			redirectUrl = redirectUrl.replace('_LIVE_', live);
			redirectUrl = redirectUrl.replace('_NOTIFKEY_', notificationKey);
//			if(liveEditLink){
//				redirectUrl = callbackUrlFromEcm.replace('_LIVEEDIT_', liveEditLink);
//			}
			
			if (redirectUrl) {
				window.location.replace(redirectUrl);
			}
		}
		
	} else {
		// reload full page
		if (!callbackUrl) {
			// if not specified, stay on the current page
			callbackUrl = document.URL; 
		}
		
		window.location.replace(callbackUrl);
	}
}


/**
 * Generic callback params
 */
function setCallbackParams( id, url) {
	callbackId = id;
	callbackUrl = url;
}

/**
 * Specific callback params for ECM conversation
 */
function setCallbackFromEcmParams(url, ecm)	{
	callbackUrlFromEcm = url;
	ecmBaseUrl = ecm;
	
	// setup a callback to handle the dispatched MessageEvent. if
	// window.postMessage is supported the passed
	// event will have .data, .origin and .source properties. otherwise, it will
	// only have the .data property.
	XD.receiveMessage(function(message) {
			receiveMessageAction(message);
		}, ecmBaseUrl);
}


function asyncUpdatePortlet(windowId, url)	{
 	 var divElt = document.getElementById(windowId);

	 if( divElt != null) {
		 // reload portlet
		 updatePortletContent( divElt, url);
	 } else {
		 // reload full page
		 window.location.replace(url);
	 }
}



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
		},
		helpers : {
            title: {
                type: 'outside',
                position: 'top'
            }
        }
	});
	

	$JQry(".fancybox_inline").fancybox({
		openEffect : 'none',
    	closeEffect	: 'none',
    	helpers : {
    		title : null
    	}
    });
	
	$JQry(".fancybox_inline_title").fancybox({
		openEffect : 'none',
    	closeEffect	: 'none',
    	helpers : {
            title: {
                type: 'outside',
                position: 'top'
            }
        },
        beforeShow : function() {
        	var originalTitle = $JQry(this.element).data("original-title");
        	if (originalTitle) {
        		this.title = originalTitle;
        	}
        }
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

    $JQry(".fancybox.thumbnail").fancybox({
    	type        : 'image',
    	openEffect	: 'elastic',
    	closeEffect	: 'elastic',

    	helpers : {
    		title : {
    			type : 'inside'
    		}
    	}
    });
    
    $JQry(".fancybox_video.thumbnail").fancybox({
    	openEffect	: 'elastic',
    	closeEffect	: 'elastic',
    	
    	aspectRatio : true,
        scrolling   : 'no',

    	afterShow	: function() {
    		var $video = $JQry(this.href).find("video");
    		$video[0].play();
    	}
    });
    
});


function closeFancybox() {
	parent.$JQry.fancybox.close();
}

/**
 * Switch actions after recieving messages from ECM
 */
function receiveMessageAction(message) {
	console.log("Receive message : " + message.data);
	
	if (message.data == 'closeFancyBox') {
		parent.$JQry.fancybox.close();
	} else if (message.data.match('currentDocumentId=')) {
		currentDocumentId = message.data.replace('currentDocumentId=','');
	} else if (message.data.match('live=')) {
		if (message.data.replace('live=', '') === 'true') {
			live = "fancyLive";
		} else {
			live = "fancyProxy";
		}
	} else if (message.data.match('notificationKey=')) {
		notificationKey = message.data.replace('notificationKey=','');
	} 
//	else if(message.data.match('liveEditLink=')){
//		liveEditLink = message.data.replace('liveEditLink=','');
//	}
}



// Affichage d'une fancybox inline sans titre
$JQry(document).ready(function() {
	var fancybox_no_title = $JQry(".fancybox-no-title");
	var fntDefined = typeof(fancybox_no_title) != 'undefined';
	var fancybox_inline = $JQry(".fancybox_inline");
	var fDefined = typeof(fancybox_inline) != 'undefined';
	/* Trouver autre critère d'égalité */
	var equals = fancybox_no_title.context == fancybox_inline.context;
	if (fntDefined && fDefined && equals){	
		$JQry(".fancybox_inline").fancybox({
			helpers: { 
		        title: null
		    },
            'transitionIn'      : 'none',
            'transitionOut'     : 'none'	 		
		});
	}
});

