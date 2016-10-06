<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="breadcrumb" value="${requestScope['osivia.breadcrumb']}" />


<nav>
    <h2 class="sr-only"><op:translate key="BREADCRUMB_TITLE" /></h2>
    <ol class="breadcrumb hidden-xs">
        <c:forEach var="child" items="${breadcrumb.children}" varStatus="breadcrumbStatus">
            <c:choose>
                <c:when test="${breadcrumbStatus.last and not empty breadcrumb.menubarItems}">
                    <li class="active">
                        <div class="dropdown">
                            <a href="#" data-toggle="dropdown">
                                <span>${child.name}</span>
                                <small>
                                    <i class="halflings halflings-triangle-bottom"></i>
                                </small>    
                            </a>
                            
                            <ul class="dropdown-menu" role="menu">
                                <c:forEach var="menubarItem" items="${breadcrumb.menubarItems}" varStatus="itemsStatus">
                                    <!-- HTML classes -->
                                    <c:remove var="htmlClasses" />
                                    <c:if test="${empty menubarItem.url}"><c:set var="htmlClasses" value="dropdown-header ${htmlClasses}" /></c:if>
                                    <c:if test="${menubarItem.state}"><c:set var="htmlClasses" value="hidden-xs ${htmlClasses}" /></c:if>
                                    <c:if test="${menubarItem.ajaxDisabled}"><c:set var="htmlClasses" value="no-ajax-link ${htmlClasses}" /></c:if>
                                    <c:if test="${menubarItem.active}"><c:set var="htmlClasses" value="active ${htmlClasses}" /></c:if>
                                    <c:if test="${menubarItem.disabled}"><c:set var="htmlClasses" value="disabled ${htmlClasses}" /></c:if>
                                
                                    <c:if test="${menubarItem.divider and not itemsStatus.first}">
                                        <li class="divider" role="presentation"></li>
                                    </c:if>
                                
                                    <li class="${htmlClasses}" role="presentation">
                                        <c:choose>
                                            <c:when test="${empty menubarItem.url}">
                                                <span class="${menubarItem.htmlClasses}" role="menuitem">
                                                    <i class="${menubarItem.glyphicon}"></i>
                                                    <span>${menubarItem.title}</span>
                                                </span>
                                            </c:when>
                                            
                                            <c:otherwise>
                                                <a href="${menubarItem.url}" class="${menubarItem.htmlClasses}" role="menuitem"
                                                    <c:if test="${not empty menubarItem.target}">target="${menubarItem.target}"</c:if>
                                                    <c:if test="${not empty menubarItem.onclick}">onclick="${menubarItem.onclick}"</c:if>
                                                    <c:if test="${not empty menubarItem.tooltip}">title="${menubarItem.tooltip}" data-toggle="tooltip" data-placement="bottom"</c:if>
                                                >
                                                    <i class="${menubarItem.glyphicon}"></i>
                                                    <span>${menubarItem.title}</span>
                                                    
                                                    <c:if test="${not empty menubarItem.target}">
                                                        <small>
                                                            <i class="glyphicons glyphicons-new-window-alt"></i>
                                                        </small>
                                                    </c:if>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </li>
                </c:when>
                
                <c:when test="${breadcrumbStatus.last}">
                    <li class="active">
                        <span>${child.name}</span>
                    </li>
                </c:when>
                
                <c:otherwise>
                    <li>
                        <a href="${child.url}">${child.name}</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ol>
</nav>
