<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>


<c:set var="appsLogout" value="${requestScope['osivia.sso.applications']}" />
<c:set var="searchPlaceholder"><op:translate key="SEARCH_PLACEHOLDER" /></c:set>
<c:set var="logoutMessage"><op:translate key="LOGOUT_MESSAGE" /></c:set>


<script type="text/javascript">

// Popover
$JQry("#logout-popover").popover({
    container : "body"
});


function callLogout() {
    var $popover = $JQry("#logout-popover");
    
    // Disabled dropdown menu
    var $menu = $popover.parent();
    $menu.addClass("disabled");
    $menu.find("[data-toggle=dropdown]").addClass("disabled");
    
    // Popover
    $popover.popover("show");
    
    var delog = "";

    <c:forEach var="app" items="${appsLogout}" varStatus="status" >
        delog += "<img src='${app}' />";
    </c:forEach>

    $("disconnection").update(delog);
    window.setTimeout("portalLogout()", 3000)
}

function portalLogout() {
    document.location = "${requestScope['osivia.toolbar.signOutURL']}";
}

</script>


<div class="toolbar">
    <div class="navbar navbar-default navbar-fixed-top">
        <div class="container-fluid">
            <div class="navbar-header">
                <div class="visible-xs">
                    <!-- Menu -->
                    <button type="button" onclick="toggleDrawer()" data-toggle="drawer" class="btn btn-link navbar-btn pull-left">
                        <span>
                            <i class="halflings halflings-menu-hamburger"></i>
                            <i class="halflings halflings-arrow-right"></i>
                        </span>
                    </button>
                    
                    <!-- State items -->
                    <c:forEach var="stateItem" items="${requestScope['osivia.toolbar.menubar.stateItems']}">
                        <div class="pull-right">
                            <p class="navbar-text"
                                <c:if test="${not empty stateItem.title}">title="${stateItem.title}" data-toggle="tooltip" data-placement="bottom"</c:if>
                            >
                                <span class="${stateItem.htmlClasses}">
                                    <i class="${stateItem.glyphicon}"></i>
                                </span>
                            </p>
                        </div>
                    </c:forEach>

                    <!-- AJAX waiter-->
                    <div class="pull-right">
                        <p class="navbar-text ajax-waiter">
                            <span class="label label-info">
                                <i class="halflings halflings-refresh"></i>
                            </span>
                        </p>
                    </div>

                    <!-- Title -->
                    <div class="clearfix">
                        <p class="navbar-text text-overflow">${requestScope['osivia.header.title']}</p>
                    </div>
                </div>
                
                
                <!-- Brand -->
                <a href="${requestScope['osivia.home.url']}" class="navbar-brand hidden-xs"><op:translate key="BRAND" /></a>
            </div>
                
            <div class="collapse navbar-collapse">
                <c:choose>
                    <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                        <ul class="nav navbar-nav navbar-right">
                            <!-- Login -->
                            <li>
                                <a href="${requestScope['osivia.toolbar.loginURL']}" class="navbar-link">
                                    <i class="halflings halflings-log-in"></i>
                                    <span><op:translate key="LOGIN" /></span>
                                </a>
                            </li>
                        </ul>
                    </c:when>
    
                    <c:otherwise>
                        <!-- Administration -->
                        <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false" />
    
                        <!-- User links -->
                        <ul class="nav navbar-nav navbar-right">                        
                            <!-- User bar -->
                            <li>
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}" />
                                    <span>${requestScope['osivia.toolbar.person'].displayName}</span>
                                    <span class="caret"></span>
                                </a>
                                
                                <div id="logout-popover" data-content="${logoutMessage}" data-placement="bottom"></div>
                                
                                <ul class="dropdown-menu" role="menu">
                                    <li role="presentation">
                                        <a role="menuitem" href="${requestScope['osivia.toolbar.myprofile']}">
                                            <i class="glyphicons glyphicons-nameplate"></i>
                                            <span><op:translate key="MY_PROFILE" /></span>
                                        </a>
                                    </li>
                                    <li role="presentation">
                                        <a role="menuitem" href="#" onclick="callLogout()">
                                            <i class="halflings halflings-log-out"></i>
                                            <span><op:translate key="LOGOUT" /></span>
                                        </a>
                                    </li>
                                </ul>
                            </li>

                            <!-- Help -->
                            <c:if test="${requestScope['osivia.toolbar.helpURL'] != null}">
	                            <li>
	                                <a href="${requestScope['osivia.toolbar.helpURL']}" class="navbar-link">
	                                    <i class="halflings halflings-question-sign"></i>
	                                    <span class="hidden-sm"><op:translate key="HELP" /></span>
	                                </a>
	                            </li>
                            </c:if>                           
                        </ul>
                    </c:otherwise>
                </c:choose>
                
                
                <!-- AJAX waiter-->
                <div class="nav navbar-nav navbar-right">
                    <p class="navbar-text ajax-waiter">
                        <span class="label label-info">
                            <i class="halflings halflings-refresh"></i>
                            <span><op:translate key="AJAX_REFRESH" /></span>
                        </span>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Technical div for applications disconnection -->
<div id="disconnection" class="hidden"></div>
