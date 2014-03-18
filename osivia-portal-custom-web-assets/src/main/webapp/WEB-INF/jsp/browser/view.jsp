<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="internationalization" prefix="is" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:resourceURL id="lazyContent" var="dataProviderURL" />

<c:set var="namespace"><portlet:namespace /></c:set>


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
                "live" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/live_doc.png"
                    }
                },
                "published" : {
                    "icon" : {
                        "image" : "/osivia-portal-custom-web-assets/images/jstree/published_doc.png"
                    }
                }
            }
        },
    	"plugins" : [ "json_data", "themes", "search", "types" ]
    });
});

</script>


<input type="text" onkeyup="jstreeSearch('${namespace}-jstree', this.value)" class="filter" placeholder="<is:getProperty key="JSTREE_FILTER" />" />
<div id="${namespace}-jstree" class="jstree-browser"></div>
