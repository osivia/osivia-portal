<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/internationalization.tld" prefix="is" %>

<c:choose>
    <c:when test="${empty requestScope['osivia.toolbar.principal']}">
        <!-- Login -->
        <a href="${requestScope['osivia.toolbar.loginURL']}">
            <is:getProperty key="LOGIN" />
        </a>
    </c:when>
        
    <c:otherwise>
        <!-- Administration -->
        <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false" />
        
        <!-- Logout -->
        <a href="${requestScope['osivia.toolbar.signOutURL']}">
            ${requestScope['osivia.toolbar.principal'].name} - <is:getProperty key="LOGOUT" />
        </a>
        
        <!-- Refresh page -->
        <a href='<c:out value="${requestScope['osivia.toolbar.refreshPageURL']}" />'>
            <is:getProperty key="REFRESH_PAGE" />
        </a>
    </c:otherwise>
</c:choose>
