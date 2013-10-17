<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="internationalization" prefix="is" %>

<c:choose>
    <c:when test="${empty requestScope['osivia.toolbar.principal']}">
        <div class="toolbar-content offline">
            <!-- Login -->
            <a href="${requestScope['osivia.toolbar.loginURL']}">
                <is:getProperty key="LOGIN" />
            </a>
        </div>
    </c:when>
        
    <c:otherwise>
        <div class="toolbar-content">
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
        </div>
    </c:otherwise>
</c:choose>
