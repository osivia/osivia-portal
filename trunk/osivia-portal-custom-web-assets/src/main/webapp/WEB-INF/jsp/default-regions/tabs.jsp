<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="userPortal" value="${requestScope['osivia.userPortal']}" />
<c:set var="userPages" value="${userPortal.userPages}" />
<c:set var="count" value="${userPortal.displayedPagesCount}" />
<c:set var="currentId" value="${requestScope['osivia.currentPageId']}" />
<c:set var="currentGroup" value="${requestScope['osivia.tab.currentGroup']}" />


<!-- Fixed nav -->
<c:choose>
    <c:when test="${count > 9}">
        <c:set var="fixed" value="fixed-lg" />
    </c:when>
    
    <c:when test="${count > 7}">
        <c:set var="fixed" value="fixed-md" />
    </c:when>
    
    <c:when test="${count > 5}">
        <c:set var="fixed" value="fixed-sm" />
    </c:when>
</c:choose>


<nav class="tabs" role="navigation">
    <!-- Title -->
    <h2 class="sr-only"><op:translate key="TABS_TITLE" /></h2>
    
    <div class="primary-tabs">
        <!-- Home -->
        <c:if test="${not empty userPortal.defaultPage}">
            <div class="pull-left">
                <ul class="home">
                    <li role="presentation"
                        <c:if test="${userPortal.defaultPage.id eq currentId}">class="active"</c:if>
                    >
                        <a href="${userPortal.defaultPage.url}" title="${userPortal.defaultPage.name}" data-toggle="tooltip" data-placement="bottom">
                            <i class="halflings halflings-home"></i>
                            <span class="sr-only">${userPortal.defaultPage.name}</span>
                        </a>
                    </li>
                </ul>
            </div>
        </c:if>
        
        
        <!-- Groups -->
        <c:if test="${not empty userPortal.groups}">
            <div class="pull-left">
                <ul class="groups">
                    <c:forEach var="group" items="${userPortal.groups}">
                        <li
                            <c:if test="${group.name eq currentGroup}">class="current"</c:if>
                        >
                            <ul>
                                <c:choose>
                                    <c:when test="${empty group.rootPage}">
                                        <c:if test="${not empty group.icon and not empty group.displayName}">
                                            <li class="group-title" role="presentation">
                                                <c:choose>
                                                    <c:when test="${empty group.displayedPages}">
                                                        <i class="${group.icon}"></i>
                                                        <span class="text-muted">${group.displayName}</span>
                                                    </c:when>
                                                    
                                                    <c:otherwise>
                                                        <i class="${group.icon}" title="${group.displayName}" data-toggle="tooltip" data-placement="bottom"></i>
                                                        <span class="sr-only">${group.displayName}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:if>
                                    
                                        <c:forEach var="userPage" items="${group.displayedPages}">
                                            <li role="presentation"
                                                <c:if test="${userPage.id eq currentId}">class="active"</c:if>
                                            >
                                                <a href="${userPage.url}">
                                                    <span>${userPage.name}</span>
                                                </a>
                                                
                                                <!-- Close -->
                                                <c:if test="${not userPage.maintains and not empty userPage.closePageUrl}">
                                                    <a href="${userPage.closePageUrl}" class="page-close">
                                                        <i class="glyphicons glyphicons-remove"></i>
                                                        <span class="sr-only"><op:translate key="CLOSE" /></span>
                                                    </a>
                                                </c:if>
                                            </li>
                                        </c:forEach>
                                    
                                        <c:if test="${not empty group.hiddenPages}">
                                            <li role="presentation">
                                                <a href="#" data-toggle="modal" data-target="#add-page-to-${group.name}">
                                                    <i class="halflings halflings-plus"></i>
                                                    <span class="sr-only"><op:translate key="OPEN_TAB" /></span>
                                                </a>
                                                
                                                <div id="add-page-to-${group.name}" class="modal fade">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <button type="button" class="close" data-dismiss="modal">
                                                                    <span>&times;</span>
                                                                </button>
                                                                <h3 class="h4 modal-title">
                                                                    <c:choose>
                                                                        <c:when test="${not empty tabGroup}">
                                                                            <i class="${tabGroup.icon}"></i>
                                                                            <span>${label}</span>
                                                                        </c:when>
                                                                        
                                                                        <c:otherwise>
                                                                            <span><op:translate key="OPEN_TAB" /></span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </h3>
                                                            </div>
                                                            <div class="modal-body">
                                                                <div class="row">
                                                                    <c:forEach var="userPage" items="${group.hiddenPages}" varStatus="status">
                                                                        <div class="col-sm-4">
                                                                            <p>
                                                                                <a href="${userPage.url}">
                                                                                    <span>${userPage.name}</span>
                                                                                </a>
                                                                            </p>
                                                                        </div>
                                                                        
                                                                        <!-- Responsive column reset -->
                                                                        <c:if test="${status.count % 3 == 0}">
                                                                            <div class="clearfix"></div>
                                                                        </c:if>
                                                                    </c:forEach>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </li>
                                        </c:if>
                                    </c:when>
                                
                                    <c:otherwise>
                                        <c:if test="${group.name eq currentGroup}">
                                            <c:set var="children" value="${group.displayedPages}" />
                                        </c:if>
                                    
                                        <li role="presentation"
                                            <c:if test="${group.rootPage.id eq currentId}">class="active"</c:if>
                                        >
                                            <a href="${group.rootPage.url}">
                                                <span>${group.rootPage.name}</span>
                                            </a>
                                            
                                            <!-- Close -->
                                            <c:if test="${not empty group.rootPage.closePageUrl}">
                                                <a href="${group.rootPage.closePageUrl}" class="page-close">
                                                    <i class="glyphicons glyphicons-remove"></i>
                                                    <span class="sr-only"><op:translate key="CLOSE" /></span>
                                                </a>
                                            </c:if>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        
        
        <!-- Tabs -->
        <div class="fixed-tabs-container">
            <ul class="${fixed}">
                <c:forEach var="userPage" items="${userPages}">
                    <c:if test="${not userPage.defaultPage and empty userPage.group}">
                        <li role="presentation"
                            <c:if test="${userPage.id eq currentId}">class="active"</c:if>
                        >
                            <a href="${userPage.url}"
                                <c:if test="${not empty fixed}">title="${userPage.name}" data-toggle="tooltip" data-placement="bottom"</c:if>
                            >
                                <span>${userPage.name}</span>
                            </a>
                            
                            <!-- Close -->
                            <c:if test="${not empty userPage.closePageUrl}">
                                <a href="${userPage.closePageUrl}" class="page-close">
                                    <i class="glyphicons glyphicons-remove"></i>
                                    <span class="sr-only"><op:translate key="CLOSE" /></span>
                                </a>
                            </c:if>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
    
    
    <c:if test="${not empty children}">
        <div class="secondary-tabs">
            <ul>
                <c:forEach var="userPage" items="${children}">
                    <li role="presentation"
                        <c:if test="${userPage.id eq currentId}">class="active"</c:if>
                    >
                        <a href="${userPage.url}">
                            <span>${userPage.name}</span>
                        </a>
                        
                        <!-- Close -->
                        <c:if test="${not userPage.maintains and not empty userPage.closePageUrl}">
                            <a href="${userPage.closePageUrl}" class="page-close">
                                <i class="glyphicons glyphicons-remove"></i>
                                <span class="sr-only"><op:translate key="CLOSE" /></span>
                            </a>
                        </c:if>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
</nav>
