/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/


var currentSubmit;
var fancyContainerDivId = null;


function onAjaxSuccess(t, callerId, multipart, popState) {
	var resp;

	if (multipart) {
		resp = t;
	} else if (t.responseText != "") {
		try {
			eval("resp =" + t.responseText + ";");
		} catch (e) {
			window.location.reload();
			return;
		}
	}
	
	if (resp.type == "update_markup") {
		// Destroy tooltip
		$JQry("body > .tooltip").remove();
		
		// Iterate all changes
		for (var id in resp.fragments) {
			var matchingElt = document.getElementById(id);

			// Different than 1 is not good
			if ((matchingElt != null) && (id != callerId)) {
				var dstContainer = document.getElementById(id);
				if (dstContainer != null) {
					// Get markup fragment
					var markup = resp.fragments[id];

					// Create a temporary element and paste the innerHTML in it
					var srcContainer = document.createElement("div");

					// Insert the markup in the div
					new Insertion.Bottom(srcContainer, markup);

					// Copy the region content
					copyInnerHTML(srcContainer, dstContainer,
							"dyna-window-content")
				} else {
					// Should log that somewhere
				}
			} else {
				// New window : create in the target region
				
				var markup = resp.fragments[id];


				// Create a temporary element and paste the innerHTML in it
				var srcContainer = document.createElement("div");

				// Insert the markup in the div
				new Insertion.Bottom(srcContainer, markup);

				// Copy the region content
				createInnerHTML(srcContainer,"dyna-window-content")				

			}
		}
		
		

		// update view state
		if (resp.view_state != null) {
			view_state = resp.view_state;
		} else {
			alert("No view state");
		}
		

		if( popState === undefined)	{
			var stateObject = {
					url: resp.url
			};			
			history.pushState(stateObject, "", resp.url);
		}
		
		// Call jQuery.ready() events
		$JQry(document).ready();
	}

	if (resp.type == "update_page") {
		document.location = resp.location;
	}
}


function sendData(action, windowId, fromPos, fromRegionId, toPos, toRegionId,
		refUri) {
	var options = {
		requestHeaders : [ "ajax", "true", "bilto" ],
		method : "post",
		postBody : "action=" + action + "&windowId=" + windowId + "&fromPos="
				+ fromPos + "&fromRegion=" + fromRegionId + "&toPos=" + toPos
				+ "&toRegion=" + toRegionId + "&refUri=" + refUri,
		onSuccess : function(t) {
			onAjaxSuccess(t, null);
		},
		on404 : function(t) {
			alert("Error 404: location " + t.statusText + " was not found.");
		},
		onFailure : function(t) {
			alert("Error " + t.status + " -- " + t.statusText);
		},
		onLoading : function(t) {
		}
	};
	// LBI : gestion de DND en mode CMS
	new Ajax.Request(commandPrefix + "/cmsAjax", options);
}


function snapshot() {
	// Find draggable regions
	var regions_on_page = $$(".dnd-region");

	// Save current state in the DOM itself
	for ( var i = 0; i < regions_on_page.length; i++) {
		var regionDiv = regions_on_page[i]
		for ( var j = 0; j < regionDiv.childNodes.length; j++) {
			var child = regionDiv.childNodes[j];
			child["regionId"] = regionDiv.id;
			child["pos"] = j;
		}
	}
}


// Check that the URL starts with the provided prefix
function isURLAccepted(url) {
	if (url.indexOf("&action=f") != -1) {
		// Pas d'ajax pour les ressources
		return false;
	} else if (url.indexOf("&action=b") != -1) {
		// Pas d'ajax pour les ressources
		return false;
	}

	var indexOfSessionId = server_base_url.indexOf(";jsessionid");
	if (indexOfSessionId > 0) {
		server_base_url = server_base_url.substring(0, indexOfSessionId
				- ";jsessionid".length - 1);
	}

	var scheme = "";
	if (url.indexOf("http://") == 0) {
		scheme = "http://";
	} else if (url.indexOf("https://") == 0) {
		scheme = "https://";
	}
	if (scheme) {
		var indexOfSlash = url.indexOf("/", scheme.length);		
		if (indexOfSlash < 0) {
			return false;
		} else {
			var path = url.substring(indexOfSlash);
			if (path.indexOf(server_base_url) != 0) {
				return false;
			}
		}
	} else if (url.indexOf(server_base_url) != 0) {
		return false;
	}

	//
	return true;
}

window.onpopstate = function(event) {
	if (event.state) {
			var options = new Object();
			options.method = "get";
			options.asynchronous = true;
			directAjaxCall(null, options, event.state.url, event, null);
	}

}


function directAjaxCall(container, options, url, eventToStop, callerId) {

	// Setup headers
	var headers = [ "ajax", "true" ],
    	$container = $JQry(container),
	    $ajaxShadowbox,
	    $ajaxWaiter = $JQry(".ajax-waiter");
	
	if (eventToStop == null) {
		$ajaxShadowbox = $container.find(".ajax-shadowbox.window-ajax-shadowbox");
	} else {
		$eventTarget = $JQry(eventToStop.target);
		ajaxShadowbox = $eventTarget.closest("[data-ajax-shadowbox]").data("ajax-shadowbox");
		
		if (ajaxShadowbox == null) {
			$ajaxShadowbox = $container.find(".ajax-shadowbox.window-ajax-shadowbox");
		} else {
		    $ajaxShadowbox = $JQry(ajaxShadowbox);
		}
	}
	

	// Add the view state value
	if (view_state != null) {
		headers.view_state = view_state;
	}

	// note : we don't convert query string to prototype parameters as in the case
	// of a post, the parameters will be appended to the body of the query which
	// will lead to a non correct request

	// Complete the ajax request options
	options.requestHeaders = headers;

	// Waiter
    $ajaxShadowbox.addClass("in");
	$ajaxWaiter.delay(200).addClass("in");


	// Url for the first page of the Ajax sequence
	if( popStateUrl != null)	{
	
		var stateObject = {
			url: popStateUrl
		};
	
		console.log("replaceState");
		history.replaceState(stateObject, "", document.location);
		
		popStateUrl = null;
	}
	
	
	
	var popState;
    if ((eventToStop != null) && (eventToStop.type === "popstate")) {
		popState = true;
	}
	
    
	options.onSuccess = function(t) {
	    $ajaxShadowbox.removeClass("in");
		$ajaxWaiter.clearQueue();
		$ajaxWaiter.removeClass("in");

		onAjaxSuccess(t, callerId, null, popState);
	};

	

    if ((eventToStop != null) && !(eventToStop.type === "popstate")) {
		Event.stop(eventToStop);
	}

	
	new Ajax.Request(url, options);

}


function ajaxCallWithoutSelfRefresh(caller, form) {
	var container = Element.up(caller, "div.dyna-window");	
	var options = new Object();
	options.method = "post";
	options.asynchronous = false;
	options.postBody = Form.serialize(form);
	var url = form.action;
	var callerId = container.firstChild.id;
	
	// Ajax call
	directAjaxCall(container, options, url, null, callerId);
}


// Explicits Ajax calls from portlet
function updatePortletContent(item, url) {
	var ajaxCall = true;

	var container = Element.up(item, "div.dyna-window");
	if (container == null) {
		ajaxCall = false;
	}

	if (!item.hasClassName("ajax-link") && (item.hasClassName("no-ajax-link") || (Element.up(item, ".no-ajax-link") != null))) {
		ajaxCall = false;
	}

	if (ajaxCall) {
		// Set URL
		var options = new Object();

		// We have a get
		options.method = "get"

		// We don't block
		options.asynchronous = true;

		directAjaxCall(container, options, url, null, null);
	} else {
		document.location = url;
	}
}


function bilto(event) {
	// Locate the div container of the window
	var source = Event.element(event);

	//PIA : desactivation des liens en ajax
	if (!source.classList.contains("ajax-link")
			&& ((Element.up(source, ".no-ajax-link") != null) || source.hasClassName("no-ajax-link"))) {
		return;
	}

	// Container
    var container = null;
	if (fancyContainerDivId != null)
		container = document.getElementById(fancyContainerDivId);
	if (container == null)
		container = Element.up(source, "div.dyna-window");	    

	// We found the window
	if (container != null) {
		//
		var options = new Object();
		var url;

		//it should work, but not necessary to treat mode changes in AJAX
		if ((Element.up(source, "div.portlet-mode-container") != null)
				&& (Element.up(source, "div.portlet-mode-container").className == "portlet-mode-container")) {
			return;
		}

		// if unknow source (IMG, SPAN, ...) , search the ancestor 'A'
		if ((source.nodeName != "A") && (source.nodeName != "INPUT") && (source.nodeName != "BUTTON")) {
			source = Element.up(source, "A");
			if (source == null)
				return;
		}

		//
		if (source.nodeName == "A") {
			// Check we can handle this URL
			if (isURLAccepted(source.href)) {
				// Set URL
				url = source.href;

				// We have a get
				options.method = "get"

				// We don't block
				options.asynchronous = true;
			}
		} else if ((source.nodeName == "INPUT" || source.nodeName == "BUTTON")
				&& (source.type == "submit" || source.type == "image")) {
			// Find enclosing form
			var current = source.parentNode;
			while (current.nodeName != 'FORM' && current.nodeName != 'BODY') {
				current = current.parentNode;
			}

			// Check we have a form and use it
			if (current.nodeName == 'FORM') {
                if ($JQry(current).find(".mce-tinymce").length) {
                    // Trigger TinyMCE save
                    tinymce.triggerSave();
                }


				var enctype = current.enctype;
				// We don't handle file upload for now
				if (enctype != "multipart/form-data") {
					// Check it is a POST
					if (current.method.toLowerCase() == "post") {
						// Check we can handle this URL
						if (isURLAccepted(current.action)) {
							// Set URL
							url = current.action;

							// Set the specified enctype
							options.enctype = enctype;
							options.asynchronous = true;
							options.method = "post"
							options.postBody = Form.serialize(current, {
								'hash' : false,
								'submit' : event.findElement().name
							});
						}
					}
				} else {
			        event.preventDefault();
			 
			        var $form = $JQry(current);
			        var formdata = (window.FormData) ? new FormData($form[0]) : null;
			        var data;
			        if (formdata != null) {
			        	formdata.append("hash", false);
			        	formdata.append(event.findElement().name, event.findElement().value);
			        	data = formdata;
			        } else {
			        	data = $form.serialize();
			        }
			 
			        $JQry.ajax({
			            url: current.action,
			            method: "post",
			            headers: {"ajax": true},
			            contentType: false, // obligatoire pour de l'upload
			            processData: false, // obligatoire pour de l'upload
			            dataType: "json",
			            data: formdata,
			            success: function(data, status, xhr) {
			            	onAjaxSuccess(data, null, true);
			            }
			        });
				}
			}
		}

		// Handle links here
		if (url != null) {
			directAjaxCall(container, options, url, event, null);
		}
		
		if( fancyContainerDivId)
			parent.jQuery.fancybox.close();
	}
}


/*
 * Copy the inner content of two zones of the provided containers.
 * The zone are found using the css class names. The operation
 * will succeed only if there is exactly one zone in each container.
 */
function copyInnerHTML(srcContainer, dstContainer, className) {
	var srcs = srcContainer.select("." + className);
	if (srcs.length == 1) {
		var src = srcs[0];

		//
		var dsts = dstContainer.select("." + className);
		if (dsts.length == 1) {
			var dst = dsts[0];

			// Remove existing non attribute children in destination
			var dstChildren = dst.childNodes;
			var copy = new Array();
			for ( var i = 0; i < dstChildren.length; i++) {
				var dstChild = dstChildren.item(i);
				if (dstChild.nodeType != 2) {
					copy[i] = dstChildren.item(i);
				}
			}
			for ( var i = 0; i < copy.length; i++) {
				Element.remove(copy[i]);
			}

			// Move src non attribute children to the destination
			while (src.hasChildNodes()) {
				var srcChild = src.firstChild;
				if (srcChild.nodeType != 2) {
					dst.appendChild(srcChild);

				} else {
					src.removeChild(srcChild);
				}
			}
		} else {
			// Should log that somewhere but
		}
	} else {
		// Should log that somewhere
	}
}

/*
 * Copy the inner content into the destination region
* This could occur if window is dynamically created
 */
function createInnerHTML(srcContainer, className) {
	var srcs = srcContainer.select("." + className);
	if (srcs.length == 1) {
		var src = srcs[0];
		var region = src.readAttribute("data-region-target");

		if( region != null)	{
			var dst = $(region);

			// Move src non attribute children to the destination
			while (srcContainer.hasChildNodes()) {
				var srcChild = srcContainer.firstChild;
				if (srcChild.nodeType != 2) {
					dst.appendChild(srcChild);

				} else {
					src.removeChild(srcChild);
				}
			}

		Event.observe(dst.firstChild, "click", bilto);
		}
 
	} else {
		// Should log that somewhere
	}
}


function footer() {
	//
	var WindowMoveObserver = Class.create();
	WindowMoveObserver.prototype = {
		initialize : function(element) {
			this.element = $(element);
		},
		onStart : function() {
		},
		onEnd : function() {
			var elt = Draggables.activeDraggable.element;

			// LBI - var injected by the footer
			var windowId = cmsPath;

			var fromRegionId = elt["regionId"];
			var fromPos = elt["pos"];

			// Doing the snapshot after move will give us the new region and pos of the window
			snapshot();
			var toRegionId = elt["regionId"];
			var toPos = elt["pos"];

			// Ajaxmode : The refURI is under 2 DIV elements
			var refUri = document.getElementById(toRegionId).childNodes[toPos].children[0].children[0].children[0].id;

			// recalcul pour igonrer les regions EMPTY
			// qui ne sont pas des fragments
			var emptyToRegionId =  toRegionId.substring( 7);
			var emptyWindowsTo = $(toRegionId).select('#'+  emptyToRegionId + "_PIA_EMPTY") ;
			if ( emptyWindowsTo.length > 0)
				toPos = toPos -1;

			var emptyFromRegionId =  fromRegionId.substring( 7);
			var emptyWindowsFrom = $(fromRegionId).select('#'+  emptyFromRegionId + "_PIA_EMPTY") ;
			if ( emptyWindowsFrom.length > 0)
				fromPos = fromPos -1;


		


			// Perform request
			sendData("windowmove", windowId, fromPos, fromRegionId, toPos,
					toRegionId, refUri);

		}
	};

	// Find the draggable regions
	var regions_on_page = $$(".dnd-region");
	// This is the main cause of https://jira.jboss.org/jira/browse/JBPORTAL-2047
	// for some reson, the prototype.js double dollar sign (which is the equivalent of getElementsByClassName)
	// is the only function that will give us a proper handle for the "drop" part to work
	// TODO - if more problems continue with DnD, this may be the root of the problem
	// var regions_on_page = document.getElementsByClassName("dnd-region");

	// Create draggable regions
	for ( var i = 0; i < regions_on_page.length; i++) {
		var region = regions_on_page[i];
		if (typeof Sortable != 'undefined') {
			Sortable.create(region, {
				dropOnEmpty : true,
				handle : "dnd-handle",
				tag : "div",
				containment : regions_on_page,
				constraint : false,
				hoverclass : "dnd-droppable"
			});
		}
	}

	//
	if (typeof Draggables != 'undefined') {
		Draggables.addObserver(new WindowMoveObserver());
	}
	//
	snapshot();

	// Find the dyna portlets
	var portlets_on_page = $$(".partial-refresh-window");

	// Add listener for the dyna windows on the dyna-window element
	// and not async-window as this one will have its markup replaced
	for ( var i = 0; i < portlets_on_page.length; i++) {
		var portlet = Element.up(portlets_on_page[i]);
		Event.observe(portlet, "click", bilto);
	}



}