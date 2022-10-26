<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="breadcrumb" value="${requestScope['osivia.breadcrumb']}" />


<nav>
    <h2 class="sr-only"><op:translate key="BREADCRUMB_TITLE" /></h2>
    <ol class="breadcrumb mb-0 px-0">
        <c:forEach var="child" items="${breadcrumb.children}" varStatus="status">
            <c:choose>
                <c:when test="${status.last and not empty breadcrumb.menu}">
                    <li class="breadcrumb-item active">
                        <div class="dropdown d-inline">
                            <a href="javascript:;" class="text-secondary dropdown-toggle" data-toggle="dropdown">
                                <span>${child.name}</span>
                            </a>
                            
                            <c:out value="${breadcrumb.menu}" escapeXml="false" />
                        </div>
                    </li>
                </c:when>
                
                <c:when test="${status.last}">
                    <li class="breadcrumb-item active">
                        <span class="text-secondary">${child.name}</span>
                    </li>
                </c:when>
                
                <c:otherwise>
                    <li class="breadcrumb-item">
                        <a href="${child.url}" class="text-secondary">${child.name}</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ol>
</nav>
