$JQry(document).ready(function() {

	$JQry(".fancytree.fancytree-default").each(function(index, element) {
		var $element = $JQry(element),
			url = $element.data("lazyloadingurl"),
			options = {
				activeVisible : true,
				clickFolderMode : 2,
				extensions : ["filter", "glyph"],
				tabbable : false,
				titlesTabbable : true,
				toggleEffect : false,

				filter : {
					mode : "hide"
				},

				glyph : {
					map : {
						doc : "glyphicons glyphicons-halflings-file",
						docOpen: "glyphicons glyphicons-halflings-file",
						error: "glyphicons glyphicons-halflings-circle-remove",
						expanderClosed: "glyphicons glyphicons-halflings-square-triangle-right",
						expanderLazy: "glyphicons glyphicons-halflings-square-triangle-right",
						expanderOpen: "glyphicons glyphicons-halflings-square-triangle-down",
						folder: "glyphicons glyphicons-halflings-folder",
						folderOpen: "glyphicons glyphicons-halflings-folder-open",
						loading: "glyphicons glyphicons-halflings-hourglass"
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
                        doc : "glyphicons glyphicons-halflings-file",
                        docOpen: "glyphicons glyphicons-halflings-file",
                        error: "glyphicons glyphicons-halflings-circle-remove",
                        expanderClosed: "glyphicons glyphicons-halflings-square-triangle-right",
                        expanderLazy: "glyphicons glyphicons-halflings-square-triangle-right",
                        expanderOpen: "glyphicons glyphicons-halflings-square-triangle-down",
                        folder: "glyphicons glyphicons-halflings-folder",
                        folderOpen: "glyphicons glyphicons-halflings-folder-open",
                        loading: "glyphicons glyphicons-halflings-hourglass"
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
                        doc : "glyphicons glyphicons-halflings-file",
                        docOpen: "glyphicons glyphicons-halflings-file",
                        error: "glyphicons glyphicons-halflings-circle-remove",
                        expanderClosed: "glyphicons glyphicons-halflings-square-triangle-right",
                        expanderLazy: "glyphicons glyphicons-halflings-square-triangle-right",
                        expanderOpen: "glyphicons glyphicons-halflings-square-triangle-down",
                        folder: "glyphicons glyphicons-halflings-folder",
                        folderOpen: "glyphicons glyphicons-halflings-folder-open",
                        loading: "glyphicons glyphicons-halflings-hourglass"
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
			},
			activeNode;

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


		// Active node
		activeNode = $element.fancytree("getActiveNode");
		if (activeNode) {
			var $selector = activeNode.tree.$div.closest(".selector"),
				$input = $selector.find("input.selector-value"),
				path = activeNode.data.path;

			$input.val(path);
		}
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
                    doc : "glyphicons glyphicons-halflings-file",
                    docOpen: "glyphicons glyphicons-halflings-file",
                    error: "glyphicons glyphicons-halflings-circle-remove",
                    expanderClosed: "glyphicons glyphicons-halflings-square-triangle-right",
                    expanderLazy: "glyphicons glyphicons-halflings-square-triangle-right",
                    expanderOpen: "glyphicons glyphicons-halflings-square-triangle-down",
                    folder: "glyphicons glyphicons-halflings-folder",
                    folderOpen: "glyphicons glyphicons-halflings-folder-open",
                    loading: "glyphicons glyphicons-halflings-hourglass"
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
			var match = (node.title !== undefined) && (node.title.toLowerCase().indexOf(value.toLowerCase()) > -1);
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
