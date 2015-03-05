$JQry(document).ready(function() {
	
	// Fancytree with links
	$JQry(".fancytree.fancytree-links").fancytree({
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
				checkbox: "halflings halflings-unchecked",
				checkboxSelected: "halflings halflings-check",
				checkboxUnknown: "halflings halflings-share",
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
	});
	
	
	$JQry(".fancytree input[type=text]").keyup(function(event) {
		var $input = $JQry(this);
		var value = $input.val()
		var tree = $input.closest(".fancytree").fancytree("getTree");
		
		if (value === "") {
			tree.clearFilter();
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
	});
	
	
	$JQry(".fancytree button").click(function(event) {
		var $tree = $JQry(this).closest(".fancytree");
		var tree = $tree.fancytree("getTree");
		tree.clearFilter();
		
		var $input = $tree.find("input[type=text]");
		$input.val("");
	});
	
});

