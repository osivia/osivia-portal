<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>



<div class="${empty requestScope['osivia.toolbar.principal'] ? 'd-none' : ''}">
    <nav class="navbar navbar-expand-sm bg-light">
        <h2 class="visually-hidden">
            <op:translate key="TOOLBAR_TITLE" />
        </h2>
    
        <div class="container-fluid">
            <%--Brand--%>
            <a href="${requestScope['osivia.home.url']}" class="navbar-brand">
                <span>${requestScope['osivia.header.application.name']}</span>
            </a>

            <button type="button" class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#toolbar-collapse">
                <i class="glyphicons glyphicons-basic-menu"></i>
            </button>

            <div id="toolbar-collapse" class="collapse navbar-collapse">
                <%--Administration--%>
                <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false" />

                <c:if test="${not empty requestScope['osivia.toolbar.principal']}">
                    <ul class="navbar-nav ms-sm-auto">
                        <%--Help--%>
                        <c:if test="${not empty requestScope['osivia.toolbar.helpURL']}">
                            <li class="nav-item">
                                <a href="${requestScope['osivia.toolbar.helpURL']}" class="nav-link">
                                    <i class="glyphicons glyphicons-basic-circle-question"></i>
                                    <span class="visually-hidden"><op:translate key="HELP" /></span>
                                </a>
                            </li>
                        </c:if>

                        <%--User menu--%>
                        <li class="nav-item dropdown">
                            <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
                                <c:choose>
                                    <c:when test="${empty requestScope['osivia.toolbar.person']}">
                                        <i class="glyphicons glyphicons-basic-user"></i>
                                        <span class="d-sm-none d-md-inline">${requestScope['osivia.toolbar.principal']}</span>
                                    </c:when>

                                    <c:otherwise>
                                        <img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}" alt="">
                                        <span class="d-sm-none d-md-inline">${requestScope['osivia.toolbar.person'].displayName}</span>
                                    </c:otherwise>
                                </c:choose>
                            </a>

                            <ul class="dropdown-menu dropdown-menu-sm-end">
                                <%--Logout--%>
                                <li>
                                    <a href="#" onclick="logout()" class="dropdown-item">
                                        <i class="glyphicons glyphicons-basic-log-out"></i>
                                        <span><op:translate key="LOGOUT" /></span>
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </c:if>
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
