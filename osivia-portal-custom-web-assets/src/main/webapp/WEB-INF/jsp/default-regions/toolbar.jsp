<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="internationalization" prefix="is"%>


<div class="toolbar">
    <nav class="navbar navbar-default navbar-fixed-top">
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
                    <div>
                        <p class="navbar-text text-overflow">${requestScope['osivia.header.title']}</p>
                    </div>
                </div>
            </div>
                
            <div class="collapse navbar-collapse">
                <c:choose>
                    <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                        <ul class="nav navbar-nav navbar-right">
                            <!-- Login -->
                            <li>
                                <a href="${requestScope['osivia.toolbar.loginURL']}" class="navbar-link">
                                    <i class="halflings halflings-log-in"></i>
                                    <span><is:getProperty key="LOGIN" /></span>
                                </a>
                            </li>
                        </ul>
                    </c:when>
    
                    <c:otherwise>
                        <!-- Administration -->
                        <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false" />
    
                        <!-- User links -->
                        <ul class="nav navbar-nav navbar-right">

                            <!-- Tasks -->
                            <c:if test="${not empty requestScope['osivia.toolbar.tasks.url']}">
                                <c:set var="title"><op:translate key="NOTIFICATION_TASKS" /></c:set>
                                <li>
                                    <button type="button" name="open-tasks" class="btn btn-link navbar-btn" data-load-url="${requestScope['osivia.toolbar.tasks.url']}" data-load-callback-function="tasksModalCallback" data-title="${title}">
                                        <i class="glyphicons glyphicons-bell"></i>
                                        <span class="sr-only">${title}</span>
                                        
                                        <span class="counter small">
                                            <c:choose>
                                                <c:when test="${requestScope['osivia.toolbar.tasks.count'] gt 0}">
                                                    <span class="label label-danger">${requestScope['osivia.toolbar.tasks.count']}</span>
                                                </c:when>
                                                
                                                <c:otherwise>
                                                    <span class="label label-default">0</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </button>
                                </li>
                            </c:if>                        
                        
                        
                            <!-- User name -->
                            <li>
                                <p class="navbar-text">
                                    <i class="halflings halflings-user"></i>
                                    <span>${requestScope['osivia.toolbar.principal'].name}</span>
                                </p>
                            </li>
                            
                            <!-- Logout -->
                            <li>
                                <a href="${requestScope['osivia.toolbar.signOutURL']}" class="navbar-link">
                                    <i class="halflings halflings-log-out"></i>
                                    <span><is:getProperty key="LOGOUT" /></span>
                                </a>
                            </li>
    
                            <!-- Refresh -->
                            <li>
                                <a href="${requestScope['osivia.toolbar.refreshPageURL']}" class="navbar-link">
                                    <i class="halflings halflings-repeat"></i>
                                    <span><is:getProperty key="REFRESH_PAGE" /></span>
                                </a>
                            </li>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
</div>
