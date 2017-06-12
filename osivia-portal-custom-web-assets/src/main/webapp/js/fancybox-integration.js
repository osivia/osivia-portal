var callbackId = "",
    callbackUrl = "",
    callbackUrlFromEcm = "",
    currentDocumentId = "",
    live = "",
    notificationKey = "",
    ecmBaseUrl = "",
    postMessageInitialized = false;


$JQry(function() {

	$JQry(".fancyframe").fancybox({
		closeClickOutside : false,
		iframe : {
			css : {
				width : "1200px",
			}
		}
	});

	$JQry(".fancyframe_refresh").fancybox({
		closeClickOutside : false,
		iframe : {
			scrolling : true,
			css : {
				width : "1200px"
			}
		},
		beforeClose : function(instance, slide) {
			callback();
		}
	});

});



$JQry(document).on("onInit.fb", function(event, instance, slide) {
	// Close dropdown
	$JQry(".dropdown.open .dropdown-toggle").dropdown("toggle");
	// Destroy tooltip
	$JQry("body > .tooltip").remove();
});


/**
 * Manage callback after closing fancybox.
 */
function callback() {
    if (callbackId) {
        var divElt = document.getElementById(callbackId);
    }

    if (divElt) {
        // reload portlet
        updatePortletContent(divElt, callbackUrl);

        // Reinit callback id & URL
        callbackId = "";
        callbackUrl = "";
    } else if (callbackUrlFromEcm) {
        // load a new page
        var $f = jQuery('.fancybox-iframe');

        if ($f && currentDocumentId) {
            var redirectUrl = callbackUrlFromEcm.replace('_NEWID_',
                    currentDocumentId);
            redirectUrl = redirectUrl.replace('_LIVE_', live);
            redirectUrl = redirectUrl.replace('_NOTIFKEY_', notificationKey);

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
 * Generic callback params.
 */
function setCallbackParams(id, url) {
    callbackId = id;
    callbackUrl = url;
}


/**
 * Specific callback params for ECM conversation.
 */
function setCallbackFromEcmParams(url, ecm) {
    callbackUrlFromEcm = url;
    ecmBaseUrl = ecm;

    // Setup a callback to handle the dispatched MessageEvent. 
    // If window.postMessage is supported the passed event will have .data, .origin and .source properties. otherwise, it willonly have the .data property.
    if (!postMessageInitialized) {
        XD.receiveMessage(function(message) {
            receiveMessageAction(message);
        }, ecmBaseUrl);
        postMessageInitialized = true;
    }
}


function asyncUpdatePortlet(windowId, url) {
    var divElt = document.getElementById(windowId);

    if (divElt != null) {
        // reload portlet
        updatePortletContent(divElt, url);
    } else {
        // reload full page
        window.location.replace(url);
    }
}


/**
 * Close fancybox.
 */
function closeFancybox() {
    parent.$JQry.fancybox.getInstance().close();
}


/**
 * Switch actions after recieving messages from ECM
 */
function receiveMessageAction(message) {
    console.log("Receive message : " + message.data);

    if (message.data == 'closeFancyBox') {
    	closeFancybox();
    } else if (message.data.match('currentDocumentId=')) {
        currentDocumentId = message.data.replace('currentDocumentId=', '');
    } else if (message.data.match('live=')) {
        if (message.data.replace('live=', '') === 'true') {
            live = "fancyLive";
        } else {
            live = "fancyProxy";
        }
    } else if (message.data.match('notificationKey=')) {
        notificationKey = message.data.replace('notificationKey=', '');
    }
}