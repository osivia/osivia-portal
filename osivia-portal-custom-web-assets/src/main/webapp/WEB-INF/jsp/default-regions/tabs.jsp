<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="userPages" value="${requestScope['osivia.userPortal'].userPages}" />


<nav class="tabs" role="navigation">
    <h2 class="hidden"><is:getProperty key="TABS_TITLE" /></h2>
    
    <ul class="btn-toolbar tabs-menu">
        <c:forEach var="userPage" items="${userPages}" varStatus="status">
            <c:if test="${'templates' != fn:toLowerCase(userPage.name)}">
                <c:set var="url" value="${userPage.url}" />
                <c:set var="buttonType" value="btn-default" />
                <c:if test="${userPage.id == requestScope['osivia.currentPageId']}">
                    <c:set var="url" value="#" />
                    <c:set var="buttonType" value="btn-primary" />
                </c:if>
                
                <li class="btn-group">
                    <a href="${url}" class="btn ${buttonType}">
                        <span class="tabs-title">${userPage.name}</span>
                    </a>
                    
                    <!-- Close -->
                    <c:if test="${not empty userPage.closePageUrl}">
                        <a href="${userPage.closePageUrl}" class="btn ${buttonType} hidden-xs">
                            <span class="glyphicons halflings remove"></span>
                        </a>
                    </c:if>
                </li>
            </c:if>
        </c:forEach>
    </ul>
</nav>
