<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="userPages" value="${requestScope['osivia.userPortal'].userPages}" />

<!-- Fixed nav -->
<c:choose>
    <c:when test="${fn:length(userPages) > 8}">
        <c:set var="fixed" value="fixed-lg" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 6}">
        <c:set var="fixed" value="fixed-md" />
    </c:when>
    
    <c:when test="${fn:length(userPages) > 5}">
        <c:set var="fixed" value="fixed-sm" />
    </c:when>
</c:choose>


<nav role="navigation">
    <h2 class="hidden"><is:getProperty key="TABS_TITLE" /></h2>
    
    <ul class="nav nav-osivia ${fixed}">
        <c:forEach var="userPage" items="${userPages}" varStatus="status">
            <c:if test="${'templates' ne fn:toLowerCase(userPage.name)}">
                <!-- Active tab -->
                <c:remove var="active" />
                <c:if test="${userPage.id == requestScope['osivia.currentPageId']}">
                    <c:set var="active" value="active" />
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
	                       <a href="${userPage.url}">${userPage.name}</a>
	                    </div>
                    </div>
                </li>
            </c:if>
        </c:forEach>
    </ul>
    
    
<!--     <ul class="btn-toolbar list-unstyled"> -->
<%--         <c:forEach var="userPage" items="${userPages}" varStatus="status"> --%>
<%--             <c:if test="${'templates' != fn:toLowerCase(userPage.name)}"> --%>
<%--                 <c:set var="url" value="${userPage.url}" /> --%>
<%--                 <c:set var="buttonType" value="btn-default" /> --%>
<%--                 <c:if test="${userPage.id == requestScope['osivia.currentPageId']}"> --%>
<%--                     <c:set var="buttonType" value="btn-primary" /> --%>
<%--                 </c:if> --%>
                
<!--                 <li class="btn-group"> -->
<%--                     <a href="${url}" class="btn ${buttonType}"> --%>
<%--                         <span class="tabs-title">${userPage.name}</span> --%>
<!--                     </a> -->
                    
<!--                     Close -->
<%--                     <c:if test="${not empty userPage.closePageUrl}"> --%>
<%--                         <a href="${userPage.closePageUrl}" class="btn ${buttonType} hidden-xs"> --%>
<!--                             <span class="glyphicons halflings remove"></span> -->
<!--                         </a> -->
<%--                     </c:if> --%>
<!--                 </li> -->
<%--             </c:if> --%>
<%--         </c:forEach> --%>
<!--     </ul> -->
</nav>
