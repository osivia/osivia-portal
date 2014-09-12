$JQry(document).ready(function() {
	
	// Display links JSTree
	$JQry(".jstree-links").jstree({
		"core" : {
			"animation" : 0,
			"open_parents" : true
		},
		"themes" : {
			"theme" : "default",
			"dots" : false,
			"icons" : true
		},
		"search" : {
			"case_insensitive" : true,
			"show_only_matches" : true
		},
		"types" : {
			"types" : {
				"page" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/page.png"
					}
				},
				"template" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/template.png"
					}
				},
				"space" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/space.png"
					}
				},
				"pageOnline" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/published_doc.png"
					}
				},
				"pageOffline" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/live_doc.png"
					}
				}
			}
		},
		"plugins" : [ "themes", "html_data", "search", "types" ]
	});
	$JQry(".jstree-links").bind(
		"loaded.jstree", function(evt, data) {
			var prefix = this.id;
			
			// Open portal node
			if (undefined != portalId) {
				var portalNode = $JQry("#" + prefix + portalId);
				$JQry(this).jstree("open_node", portalNode);
			}
			
			// Open current parent page node
			if (undefined != currentPageId) {
				// Get current page node
				var currentPageNode = $JQry("#" + prefix + currentPageId);
				if (undefined != currentPageNode) {
					// Get current parent page node and open it to display current page node
					var currentParentNode = currentPageNode.parents("li").first();
					$JQry(this).jstree("open_node", currentParentNode);
				}
			}
		}
	);
	
	
	
	// Display links JSTree for Sitemap
	$JQry(".jstree-links-sitemap").jstree({
		"core" : {
			"animation" : 0,
			"open_parents" : true,
			"initially_loaded" : ["rootSitemap"],
			"initially_open" : ["rootSitemap"]
		},
		"themes" : {
			"theme" : "default",
			"dots" : false,
			"icons" : true
		},
		"search" : {
			"case_insensitive" : true,
			"show_only_matches" : true
		},
		"types" : {
			"types" : {
				"page" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/page.png"
					}
				},
				"template" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/template.png"
					}
				},
				"space" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/space.png"
					}
				},
				"pageOnline" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/published_doc.png"
					}
				},
				"pageOffline" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/live_doc.png"
					}
				}
			}
		},
		"plugins" : [ "themes", "html_data", "search", "types" ]
	});

	
	// Unique selector JSTree
	$JQry(".jstree-select-unique").jstree({
		"core" : {
			"animation" : 0,
			"open_parents" : true
		},
		"themes" : {
			"theme" : "default",
			"dots" : false,
			"icons" : true
		},
		"search" : {
			"case_insensitive" : true,
			"show_only_matches" : true
		},
		"ui" : {
			"select_limit" : 1
		},
		"types" : {
			"types" : {
				"page" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/page.png"
					}
				},
				"template" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/template.png"
					}
				},
				"space" : {
					"icon" : {
						"image" : "/osivia-portal-custom-web-assets/images/jstree/space.png"
					}
				}
			}
		},
		"plugins" : [ "themes", "html_data", "search", "ui", "types" ]
	});
	$JQry(".jstree-select-unique").bind(
		"loaded.jstree", function(evt, data) {
			if ($JQry(this).hasClass("locked")) {
				$JQry(this).jstree("lock");
			} else {
				var prefix = this.id;
				
				// Open portal node
				if (undefined != portalId) {
					var portalNode = $JQry("#" + prefix + portalId);
					$JQry(this).jstree("open_node", portalNode);
				}
				
				// Select node
				var firstNode = $JQry(this).find("li").first();
				$JQry(this).jstree("select_node", firstNode);
			}
		}
	);
	$JQry(".jstree-select-unique").bind(
		"select_node.jstree", function(evt, data) {
			var selection = $JQry(this).jstree("get_selected")[0];
			var formulaire = $JQry(this).parents("form");
			var input = formulaire.find("input[name=" + this.id + "]")[0];
			if ((selection != null) && (input != null)) {
				var prefix = this.id;
				var nodeId = selection.id;
				var pageId = nodeId.substr(prefix.length, nodeId.length);				
				input.value = pageId;				
			}
		}
	);

});

function jstreeOpenAll() {
	$JQry(".jstree").jstree("open_all", -1, false);
}

function jstreeClearSearch() {
	$JQry(".jstree").jstree("clear_search");
}

function jstreeSearch(id, request) {
	$JQry("#" + id).jstree("search", request, false);
}

function jstreeDeselectAll(id) {
	$JQry("#" + id).jstree("deselect_all");
	
	// Suppression du contenu du champ hidden associé à l'arbre
	var formulaire = $JQry("#" + id).parents("form");
	var inputSelect = $JQry(formulaire).find("input[name=" + id + "]")[0];
	if (inputSelect != null) {
		inputSelect.value = "";
	}
}

function jstreeToggleLock(id, enable) {
	if (enable) {
		jstreeDeselectAll(id);
		$JQry("#" + id).jstree("lock");		
	} else {
		$JQry("#" + id).jstree("unlock");
	}
}

function jstreeCancelRename(id) {
	$JQry("#" + id).jstree("unlock");
	$JQry("#" + id).jstree("deselect_all");
	
	var formulaire = $JQry("#" + id).parents("form");
	var inputSelect = $JQry(formulaire).find("input[name=" + id + "]")[0];
	var inputOldDisplayName = $JQry(formulaire).find("input[name='oldDisplayName']")[0];
	if ((inputSelect != null) && (inputOldDisplayName != null)) {
		var selections = $JQry("#" + id).find("li[title=" + inputSelect.value + "]");
		$JQry("#" + id).jstree("rename_node", selections, inputOldDisplayName.value);
		inputSelect.value = "";
	}	
}
