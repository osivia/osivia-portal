$JQry(document).ready(function() {
	
	// Fancytree with links
	$JQry(".fancytree.fancytree-links").each(function(index, element) {
		var $element = $JQry(element),
			url = $element.data("lazyloadingurl"),
			options = {
				activeVisible : true,
				extensions : ["filter", "glyph"],
				tabbable : false,
				titlesTabbable : true,
				toggleEffect : false,
				
				filter : {
					mode : "hide"
				},
				
				glyph : {
					map : {
						doc : "glyphicons glyphicons-file",
						docOpen: "glyphicons glyphicons-file",
						error: "halflings halflings-exclamation-sign",
						expanderClosed: "glyphicons glyphicons-collapse text-primary-hover",
						expanderLazy: "glyphicons glyphicons-collapse text-primary-hover",
						expanderOpen: "glyphicons glyphicons-expand text-primary-hover",
						folder: "glyphicons glyphicons-folder-closed",
						folderOpen: "glyphicons glyphicons-folder-open",
						loading: "halflings halflings-hourglass text-info"
					}
				},
				
				activate : function(event, data) {
					var node = data.node;
					if (node.data.href) {
						if (node.data.target) {
							window.open(node.data.href, node.data.target);
						} else {
							window.location.href = node.data.href;
						}
					}
				}
			};
		
		if (url !== undefined) {
			// Source URL
			options["source"] = {
				url : url,
				cache : false
			};
			
			// Lazy loading
			options["lazyLoad"] = function(event, data) {
				var node = data.node;

				data.result = {
					url : url,
					data : {
						"path" : node.data.path
					},
					cache : false
				};
			}
		}

		// Fancytree
		$element.fancytree(options);	
	});
	
	
	// Fancytree with selector with optional lazy loading
	$JQry(".fancytree.fancytree-selector").each(function(index, element) {
		var $element = $JQry(element),
			url = $element.data("lazyloadingurl"),
			options = {
				activeVisible : true,
				autoActivate : false,
				clickFolderMode : 1,
				extensions : ["filter", "glyph"],
				tabbable : false,
				titlesTabbable : false,
				toggleEffect : false,
				
				filter : {
					mode : "hide"
				},
				
				glyph : {
					map : {
						doc : "glyphicons glyphicons-file",
						docOpen: "glyphicons glyphicons-file",
						error: "halflings halflings-exclamation-sign text-error",
						expanderClosed: "glyphicons glyphicons-collapse",
						expanderLazy: "glyphicons glyphicons-collapse",
						expanderOpen: "glyphicons glyphicons-expand",
						folder: "glyphicons glyphicons-folder-closed",
						folderOpen: "glyphicons glyphicons-folder-open",
						loading: "halflings halflings-hourglass text-info"
					}
				},
				
				activate : function(event, data) {
					var $selector = data.tree.$div.closest(".selector"),
						$input = $selector.find("input.selector-value"),
						path = data.node.data.path;
					
					$input.val(path);
				},
				
				click : function(event, data) {
					if (data.targetType == "expander") {
						return true;
					} else {
						return data.node.data.acceptable;
					}
				}
			};
		
		if (url !== undefined) {
			// Source URL
			options["source"] = {
				url : url,
				cache : false
			};
			
			// Lazy loading
			options["lazyLoad"] = function(event, data) {
				var node = data.node;

				data.result = {
					url : url,
					data : {
						"path" : node.data.path
					},
					cache : false
				};
			}
		}

		// Fancytree
		$element.fancytree(options);
	});
	
	
	// Fancytree browser
	$JQry(".fancytree.fancytree-browser").each(function(index, element) {
		var $element = $JQry(element),
			url = $element.data("lazyloadingurl");

		// Fancytree
		$element.fancytree({
			activeVisible : true,
			extensions : ["glyph"],
			tabbable : false,
			titlesTabbable : false,
			toggleEffect : false,

			source : {
				url : url,
				cache : false
			},
			
			glyph : {
				map : {
					doc : "glyphicons glyphicons-file",
					docOpen: "glyphicons glyphicons-file",
					error: "halflings halflings-exclamation-sign",
					expanderClosed: "glyphicons glyphicons-collapse text-primary-hover",
					expanderLazy: "glyphicons glyphicons-collapse text-primary-hover",
					expanderOpen: "glyphicons glyphicons-expand text-primary-hover",
					folder: "glyphicons glyphicons-folder-closed",
					folderOpen: "glyphicons glyphicons-folder-open",
					loading: "halflings halflings-hourglass text-info"
				}
			},
			
			activate : function(event, data) {
				var node = data.node;
				if (node.data.href) {
					if (node.data.target) {
						window.open(node.data.href, node.data.target);
					} else {
						window.location.href = node.data.href;
					}
				}
			},
			
			click : function(event, data) {
				if (data.targetType == "expander") {
					return true;
				} else {
					return data.node.data.acceptable;
				}
			},

			lazyLoad : function(event, data) {
				var node = data.node;

				data.result = {
					url : url,
					data : {
						"path" : node.data.path
					},
					cache : false
				};
			}
		});
	});
	
	
	$JQry(".fancytree input[type=text]").keyup(filterTree);
	// Mobile virtual event
	$JQry(".fancytree input[type=text]").on("input", filterTree);
	
	
	$JQry(".fancytree button").click(function(event) {
		var $target = $JQry(event.target),
			$tree = $target.closest(".fancytree"),
			tree = $tree.fancytree("getTree"),
			$filter = $tree.find("input[type=text]"),
			expand = $filter.data("expand");
			
		$filter.val("");
		
		clearFilter(tree, expand);
	});
	
	
	// Fancybox checkbox toggle
	$JQry("input[type=checkbox][data-toggle=fancytree]").change(function(event) {
		var $checkbox = $JQry(this),
		    checked = $checkbox.is(":checked"),
	        $formGroup = $checkbox.closest(".form-group"),
	        $selector = $formGroup.find("input.selector-value"),
	        $tree = $formGroup.find(".fancytree"),
	        tree = $tree.fancytree("getTree"),
	        $filter = $tree.find("input[type=text]");

		if (checked) {
			$tree.fancytree("disable");
			$selector.val("");
			tree.activateKey(false);
		} else {
			$tree.fancytree("enable");
		}
		
		$selector.prop("disabled", checked);
		$filter.prop("disabled", checked);
	});
	
});


/**
 * Filter tree.
 * 
 * @param event event
 */
function filterTree(event) {
	var $target = $JQry(event.target),
		value = $target.val(),
		tree = $target.closest(".fancytree").fancytree("getTree"),
		expand = $target.data("expand");

	if (value === "") {
		clearFilter(tree, expand);
	} else {
		tree.filterNodes(function(node) {
			var match = (node.title.toLowerCase().indexOf(value.toLowerCase()) > -1);
			if (match) {
				node.makeVisible({
					noAnimation : true,
					scrollIntoView : false
				});
			}
			return match;
		}, false);
	}
}


/**
 * Clear tree filter.
 * 
 * @param tree tree
 * @param expand expand tree indicator
 */
function clearFilter(tree, expand) {
	tree.clearFilter();
	
	tree.visit(function(node) {
		if (!node.data.retain) {
			node.setExpanded(expand == true);
		}
		return true;
	});
}
