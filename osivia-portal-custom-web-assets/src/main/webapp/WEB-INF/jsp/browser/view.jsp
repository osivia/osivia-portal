<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:resourceURL id="lazyContent" var="dataProviderURL" />


<script type='text/javascript'>
var dataProviderURL = "<%=dataProviderURL %>";

//Lazy loading JSTree for live content browser
$JQry(document).ready(function() {
    $JQry(".jstree-browser").jstree({
    	"core" : {
            "animation" : 0,
            "open_parents" : true
        },
        "json_data" : {
            "ajax" : {
                "type": 'GET',
                "url": function (node) {
                    var url = dataProviderURL;
                    if (node != -1) {
                        var nodeId = node.attr('id');
                        url += "&nodeId=" + nodeId;
                    }
                    return url;
                },
                "success": function (new_data) {
                    return new_data;
                }
            }
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
            	"Root" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/root.png"
                    }
                },
                "Space" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/space.png"
                    }
                },
                "Page" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/page.png"
                    }
                },
                "Workspace" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/workspace.png"
                    }
                },
                "Folder" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/folder.png"
                    }
                },
                "Document" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/document.png"
                    }
                }
            }
        },
    	"plugins" : [ "themes", "json_data", "search", "types" ]
    });
});
    
</script>


<div class="jstree-browser"></div>
