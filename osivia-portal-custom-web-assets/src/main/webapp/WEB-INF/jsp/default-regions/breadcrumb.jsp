<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="breadcrumb" value="${requestScope['osivia.breadcrumb']}" />


<nav>
    <h2 class="sr-only"><op:translate key="BREADCRUMB_TITLE" /></h2>
    <ol class="breadcrumb hidden-xs">
        <c:forEach var="child" items="${breadcrumb.children}" varStatus="breadcrumbStatus">
            <c:choose>
                <c:when test="${breadcrumbStatus.last and not empty breadcrumb.menu}">
                    <li class="active">
                        <div class="dropdown">
                            <a href="#" data-toggle="dropdown">
                                <span>${child.name}</span>
                                <span class="caret"></span>    
                            </a>
                            
                            <c:out value="${breadcrumb.menu}" escapeXml="false" />
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
