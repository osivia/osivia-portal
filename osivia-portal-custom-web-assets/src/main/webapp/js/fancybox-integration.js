var callbackId = "",
	callbackUrl = "",
	callbackUrlFromEcm = "",
	currentDocumentId = "",
	live = "",
	notificationKey = "",
	ecmBaseUrl = "",
	postMessageInitialized = false;


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


$JQry(function() {

	// Fancyframe
	$JQry(".fancyframe").fancybox({
		"type" : "iframe",
		"width" : 800,
		"height" : 600,
 		helpers: {
 			overlay: {
 				closeClick: false
 			}
 		}
	});

	// Fancyframe with callback
	$JQry(".fancyframe_refresh").fancybox({
		"type" : "iframe",
		"width" : 800,
		"height" : 600,
		"beforeClose" : function() {
			callback();
		},
		helpers : {
			title : {
				type : "outside",
				position : "top"
			},
			overlay : {
				closeClick: false,
				locked : false
			}
		},
		beforeShow : function() {
			var originalTitle = $JQry(this.element).data("original-title");
			if (originalTitle) {
				this.title = originalTitle;
			}
		}
	});

	// Fancybox inline
	$JQry(".fancybox_inline").fancybox({
		openEffect : 'none',
		closeEffect : 'none',
		helpers : {
			title : null
		}
	});

	// Fancybox inline with title
	$JQry(".fancybox_inline_title").fancybox({
		openEffect : 'none',
		closeEffect : 'none',
		helpers : {
			title : {
				type : 'outside',
				position : 'top'
			}
		},
		beforeShow : function() {
			var originalTitle = $JQry(this.element).data("original-title");
			if (originalTitle) {
				this.title = originalTitle;
			}
		}
	});

	// Fancybox for thumbnails
	$JQry(".fancybox.thumbnail").fancybox({
		type : "image",
		openEffect : "elastic",
		closeEffect : "elastic",
		helpers : {
			title : {
				type : "inside"
			}
		},
		afterLoad : function() {
			var $element = $JQry(this.element), title = $element.data("title");

			if (title) {
				$outer = $JQry(document.createElement("div"));

				$inner = $JQry(document.createElement("div"));
				$inner.addClass("text-center text-overflow");
				$inner.text(title);
				$inner.appendTo($outer);

				this.title = $outer.html();
			}
		}
	});
	
	// Fancybox for video thumbnails
	$JQry(".fancybox_video.thumbnail").fancybox({
		openEffect : "elastic",
		closeEffect : "elastic",

		aspectRatio : true,
		scrolling : "no",

		afterShow : function() {
			var $video = $JQry(this.href).find("video");

			$video[0].play();
		}
	});

	
	// Fancybox inline form update
	$JQry(".fancybox_inline[data-input-name][data-input-value]").click(function(event) {
		var $this = $JQry(this),
			name = $this.data("input-name"),
			value = $this.data("input-value"),
			$target = $JQry($this.attr("href")),
			$input;
		
		if ($target !== undefined) {
			$input = $target.find("input[name=" + name + "]");
			$input.val(value);
		}
	});
	
});


/**
 * Close fancybox.
 */
function closeFancybox() {
	parent.$JQry.fancybox.close();
}


/**
 * Switch actions after recieving messages from ECM
 */
function receiveMessageAction(message) {
	console.log("Receive message : " + message.data);

	if (message.data == 'closeFancyBox') {
		parent.$JQry.fancybox.close();
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
