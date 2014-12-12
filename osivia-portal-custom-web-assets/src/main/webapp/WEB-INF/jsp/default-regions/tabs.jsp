<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="userPages" value="${requestScope['osivia.userPortal'].userPages}" />

<!-- Fixed nav -->
<c:choose>
    <c:when test="${fn:length(userPages) > 10}">
        <c:set var="fixed" value="fixed-lg" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 8}">
        <c:set var="fixed" value="fixed-md" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 6}">
        <c:set var="fixed" value="fixed-sm" />
    </c:when>
</c:choose>


<nav role="navigation">
    <h2 class="hidden"><is:getProperty key="TABS_TITLE" /></h2>
    
    <ul class="nav nav-osivia ${fixed}">
        <c:forEach var="userPage" items="${userPages}" varStatus="status">
            <!-- Active tab -->
            <c:remove var="active" />
            <c:if test="${userPage.id == requestScope['osivia.currentPageId']}">
                <c:set var="active" value="active" />
            </c:if>
            
            <!-- Tooltip data -->
            <c:if test="${fn:length(userPages) > 6}">
                <c:set var="tooltipData" value="title='${userPage.name}' data-toggle='tooltip' data-placement='bottom'" />
            </c:if>
            
            
            <li class="${active}" role="presentation">
                <div class="text-center clearfix">
                 <!-- Close -->
                 <c:if test="${not empty userPage.closePageUrl}">
                    <a href="${userPage.closePageUrl}" class="page-close">
                        <i class="glyphicons halflings remove"></i>
                    </a>
                 </c:if>
                 
                 <div>
                    <c:choose>
                        <c:when test="${userPage.defaultPage}">
                            <a href="${userPage.url}" title="${userPage.name}" data-toggle="tooltip" data-placement="bottom">
                                <i class="glyphicons halflings home"></i>
                            </a>
                        </c:when>
                        
                        <c:otherwise>
                            <a href="${userPage.url}" ${tooltipData}>${userPage.name}</a>
                        </c:otherwise>
                    </c:choose>
                 </div>
                </div>
            </li>
        </c:forEach>
    </ul>
</nav>
