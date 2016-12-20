<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op"%>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="stateItems" value="${requestScope['osivia.toolbar.menubar.stateItems']}" />


<div class="toolbar">
    <nav class="navbar navbar-default navbar-fixed-top">
        <h2 class="sr-only">
            <op:translate key="TOOLBAR_TITLE" />
        </h2>

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
                    <c:forEach begin="1" end="${fn:length(stateItems)}" var="count">
                        <c:set var="stateItem" value="${stateItems[fn:length(stateItems) - count]}" />

                        <div class="pull-right">
                            <p class="navbar-text"
                                <c:choose>
                                    <c:when test="${not empty stateItem.tooltip}">title="${stateItem.tooltip}" data-toggle="tooltip" data-placement="bottom"</c:when>
                                    <c:when test="${not empty stateItem.title}">title="${stateItem.title}" data-toggle="tooltip" data-placement="bottom"</c:when>
                                </c:choose>
                            >
                                <span class="${stateItem.htmlClasses}">
                                    <i class="${stateItem.glyphicon}"></i>
                                    <span class="sr-only">${stateItem.title}</span>
                                </span>
                            </p>
                        </div>
                    </c:forEach>

                    <!-- AJAX waiter-->
                    <%-- <div class="pull-right">
                        <p class="navbar-text ajax-waiter">
                            <span class="label label-info">
                                <i class="halflings halflings-refresh"></i>
                                <span class="sr-only"><op:translate key="AJAX_REFRESH" /></span>
                            </span>
                        </p>
                    </div> --%>

                    <!-- Title -->
                    <div class="clearfix">
                        <p class="navbar-text text-overflow">${requestScope['osivia.header.title']}</p>
                    </div>
                </div>


                <!-- Brand -->
                <a href="${requestScope['osivia.home.url']}" class="navbar-brand hidden-xs">${requestScope['osivia.header.application.name']}</a>
            </div>

            <div class="hidden-xs">
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
                            <!-- Help -->
                            <c:if test="${not empty requestScope['osivia.toolbar.helpURL']}">
                                <li>
                                    <a href="${requestScope['osivia.toolbar.helpURL']}" class="navbar-link">
                                        <i class="halflings halflings-question-sign"></i>
                                        <span class="hidden-sm"><op:translate key="HELP" /></span>
                                    </a>
                                </li>
                            </c:if>

                            <!-- Tasks -->
                            <c:if test="${not empty requestScope['osivia.toolbar.tasks.url']}">
                                <c:set var="title"><op:translate key="NOTIFICATION_TASKS" /></c:set>
                                <li>
                                    <button type="button" name="open-tasks" class="btn btn-link navbar-btn" data-target="#osivia-modal"
                                        data-load-url="${requestScope['osivia.toolbar.tasks.url']}" data-load-callback-function="tasksModalCallback"
                                        data-title="${title}" data-footer="true">
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

                            <!-- User bar -->
                            <li>
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <c:choose>
                                        <c:when test="${empty requestScope['osivia.toolbar.person']}">
                                            <i class="halflings halflings-user"></i>
                                            <span class="visible-lg-inline">${requestScope['osivia.toolbar.principal']}</span>
                                        </c:when>
    
                                        <c:otherwise>
                                            <img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}" alt="">
                                            <span class="visible-lg-inline">${requestScope['osivia.toolbar.person'].displayName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" role="menu">
                                    <li class="dropdown-header hidden-lg" role="presentation">${requestScope['osivia.toolbar.person'].displayName}</li>
                                
                                    <!-- User profile -->
                                    <c:if test="${not empty requestScope['osivia.toolbar.myprofile']}">
                                        <li role="presentation">
                                            <a href="${requestScope['osivia.toolbar.myprofile']}" role="menuitem">
                                                <i class="glyphicons glyphicons-nameplate"></i>
                                                <span><op:translate key="MY_PROFILE" /></span>
                                            </a>
                                        </li>
                                    </c:if>

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
                <%-- <div class="nav navbar-nav navbar-right">
                    <p class="navbar-text ajax-waiter">
                        <span class="label label-info">
                            <i class="halflings halflings-refresh"></i>
                            <span><op:translate key="AJAX_REFRESH" /></span>
                        </span>
                    </p>
                </div> --%>
            </div>
        </div>
    </nav>
</div>


<!-- Disconnection modal -->
<div id="disconnection" class="modal fade" data-apps="${op:join(requestScope['osivia.sso.applications'], '|')}"
    data-redirection="${requestScope['osivia.toolbar.signOutURL']}">
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
