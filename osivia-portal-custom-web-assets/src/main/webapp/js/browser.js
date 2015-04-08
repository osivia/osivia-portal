$JQry(document).ready(function() {

	// Fancytree browser
	$JQry(".fancytree.fancytree-browser").each(function() {
		var $this = $JQry(this);
		
		// Lazy loading URL
		var $root = $this.closest(".browser");
		var url = $root.data("lazyloadingurl");

		// Fancytree
		$this.fancytree({
			activeVisible : true,
			extensions : ["glyph"],
			tabbable : false,
			titlesTabbable : true,
			toggleEffect : false,

			source : {
				url : url,
				cache : false
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
			},
			
			collapse : function(event, data) {
				parent.$JQry.fancybox.update();
			},
			
			expand : function(event, data) {
				parent.$JQry.fancybox.update();
			},
			
			lazyLoad : function(event, data) {
				var node = data.node;
				
				// Lazy loading URL
				var $root = node.tree.$div.closest(".browser");
				var url = $root.data("lazyloadingurl");
				
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
	
});
