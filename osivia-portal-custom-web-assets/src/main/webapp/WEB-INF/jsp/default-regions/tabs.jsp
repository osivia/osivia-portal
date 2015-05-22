<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="userPortal" value="${requestScope['osivia.userPortal']}" />
<c:set var="userPages" value="${userPortal.userPages}" />

<!-- Fixed nav -->
<c:choose>
    <c:when test="${fn:length(userPages) > 9}">
        <c:set var="fixed" value="fixed-lg" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 7}">
        <c:set var="fixed" value="fixed-md" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 5}">
        <c:set var="fixed" value="fixed-sm" />
    </c:when>
</c:choose>


<nav class="tabs tabs-default" role="navigation">
    <!-- Title -->
    <h2 class="hidden"><is:getProperty key="TABS_TITLE" /></h2>
    
    
    <!-- Home -->
    <c:if test="${not empty userPortal.defaultPage}">
        <div class="pull-left">
            <ul class="home">
                <li role="presentation"
                    <c:if test="${userPortal.defaultPage.id eq requestScope['osivia.currentPageId']}">class="active"</c:if>
                >
                    <a href="${userPortal.defaultPage.url}" title="${userPortal.defaultPage.name}" data-toggle="tooltip" data-placement="bottom">
                        <i class="halflings halflings-home"></i>
                    </a>
                </li>
            </ul>
        </div>
    </c:if>
    
    
    <!-- Tabs -->
    <div class="fixed-tabs-container">
        <ul class="${fixed}">
            <c:forEach var="userPage" items="${userPages}">
                <c:if test="${not userPage.defaultPage}">
                    <li role="presentation"
                        <c:if test="${userPage.id eq requestScope['osivia.currentPageId']}">class="active"</c:if>
                    >
                        <a href="${userPage.url}">
                            <span>${userPage.name}</span>
                        </a>
                        
                        <!-- Close -->
                        <c:if test="${not empty userPage.closePageUrl}">
                            <a href="${userPage.closePageUrl}" class="page-close">
                                <i class="halflings halflings-remove"></i>
                                <span class="sr-only"><is:getProperty key="CLOSE" /></span>
                            </a>
                        </c:if>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </div>
</nav>
