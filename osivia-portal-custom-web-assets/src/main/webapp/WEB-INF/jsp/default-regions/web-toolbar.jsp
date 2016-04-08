<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>


<c:if test="${empty requestScope['osivia.toolbar.principal']}">
    <c:set var="toolbarDisplayClass" value="visible-xs" />
</c:if>

<div class="toolbar ${toolbarDisplayClass}">
    <div class="navbar navbar-default navbar-fixed-top">
        <div class="container">
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
                <a href="${requestScope['osivia.home.url']}" class="navbar-brand hidden-xs">${requestScope['osivia.header.application.name']}</a>
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
                                    <c:choose>
                                        <c:when test="${empty requestScope['osivia.toolbar.person']}">
                                            <i class="halflings halflings-user"></i>
                                            <span>${requestScope['osivia.toolbar.principal']}</span>
                                        </c:when>
                                        
                                        <c:otherwise>
                                            <img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}" alt="" />
                                            <span>${requestScope['osivia.toolbar.person'].displayName}</span>
                                        </c:otherwise>
                                    </c:choose>

                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" role="menu">
                                    <!-- Logout -->
                                    <li role="presentation">
                                        <a href="#" onclick="logout()" role="menuitem">
                                            <i class="halflings halflings-log-out"></i>
                                            <span><op:translate key="LOGOUT" /></span>
                                        </a>
                                    </li>
                                </ul>
                            </li>
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


<!-- Disconnection modal -->
<div id="disconnection" class="modal fade" data-apps="${op:join(requestScope['osivia.sso.applications'], '|')}" data-redirection="${requestScope['osivia.toolbar.signOutURL']}">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <i class="glyphicons glyphicons-exit"></i>
                <span><op:translate key="LOGOUT_MESSAGE" /></span>
            </div>
        </div>
    </div>
    
    <div class="apps-container hidden"></div>
</div>
