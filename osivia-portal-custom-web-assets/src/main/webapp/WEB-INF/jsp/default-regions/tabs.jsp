<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:set var="userPortal" value="${requestScope['osivia.userPortal']}" />
<c:set var="userPages" value="${userPortal.userPages}" />

<!-- Default page properties -->
<c:set var="tabStyle" value="bg-gray-lighter-alpha-40 bg-gray-lighter-alpha-80-hover border-gray-lighter-top border-primary-bottom border-gray-lighter-left border-gray-lighter-right" />
<c:set var="color" value="text-muted" />
<c:if test="${userPortal.defaultPage.id eq requestScope['osivia.currentPageId']}">
    <c:set var="active" value="active" />
    <c:set var="tabStyle" value="bg-body-alpha-60 border-primary-top border-body-bottom border-primary-left border-primary-right" />
    <c:remove var="color" />
</c:if>



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


<nav id="tabs-menu" role="navigation">
    <!-- Title -->
    <h2 class="hidden"><is:getProperty key="TABS_TITLE" /></h2>
    
    <div class="tabs border-primary">
        <!-- Home -->
        <c:if test="${not empty userPortal.defaultPage}">
            <div class="home pull-left">
                <div class="${active}">
                    <div class="${tabStyle}">
                        <a href="${userPortal.defaultPage.url}" class="${color}" title="${userPortal.defaultPage.name}" data-toggle="tooltip" data-placement="bottom">
                            <i class="glyphicons halflings home"></i>
                        </a>
                    </div>
                </div>
            </div>
        </c:if>
        
        <div>
            <ul class="${fixed}">
                <c:forEach var="userPage" items="${userPages}">
                    <c:if test="${not userPage.defaultPage}">
                        <c:set var="tabStyle" value="text-muted bg-gray-lighter-alpha-40 bg-gray-lighter-alpha-80-hover border-gray-lighter-top border-primary-bottom border-gray-lighter-left border-gray-lighter-right" />
                        <c:set var="color" value="text-muted" />
                        
                        <!-- Active tab properties -->
                        <c:remove var="active" />
                        <c:if test="${userPage.id eq requestScope['osivia.currentPageId']}">
                            <c:set var="active" value="active" />
                            <c:set var="tabStyle" value="bg-body-alpha-60 border-primary-top border-body-bottom border-primary-left border-primary-right" />
                            <c:remove var="color" />
                        </c:if>
                        
                        <!-- Tooltip -->
                        <c:remove var="tooltip" />
                        <c:if test="${not empty fixed and empty active}">
                            <c:set var="tooltip">title="${userPage.name}" data-toggle="tooltip" data-placement="bottom"</c:set>
                        </c:if>
                    
                    
                        <li class="${active}" role="presentation">
                            <div class="${tabStyle}">
                                <a href="${userPage.url}" class="${color}" ${tooltip}>
                                    <span>${userPage.name}</span>
                                </a>
                                
                                <!-- Close -->
                                <c:if test="${not empty userPage.closePageUrl}">
                                    <a href="${userPage.closePageUrl}" class="page-close ${color}">
                                        <i class="glyphicons halflings remove"></i>
                                        <span class="sr-only"><is:getProperty key="CLOSE" /></span>
                                    </a>
                                </c:if>
                            </div>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
    
    
    
<%--     <ul class="nav nav-osivia border-primary ${fixed}"> --%>
<%--         <c:forEach var="userPage" items="${userPages}" varStatus="status"> --%>
<%--             <c:set var="background" value="bg-gray-lighter-alpha-40" /> --%>
<%--             <c:set var="border" value="border-gray-lighter border-primary-bottom" /> --%>
        
<!--             Active tab -->
<%--             <c:remove var="active" /> --%>
<%--             <c:if test="${userPage.id == requestScope['osivia.currentPageId']}"> --%>
<%--                 <c:set var="active" value="active" /> --%>
<%--             </c:if> --%>
            
<!--             Tooltip data -->
<%--             <c:if test="${fn:length(userPages) > 6}"> --%>
<%--                 <c:set var="tooltipData" value="title='${userPage.name}' data-toggle='tooltip' data-placement='bottom'" /> --%>
<%--             </c:if> --%>
            
            
<%--             <li class="${active}" role="presentation"> --%>
<%--                 <div class="text-center clearfix ${background}"> --%>
<!--                     Close -->
<%--                     <c:if test="${not empty userPage.closePageUrl}"> --%>
<%--                         <a href="${userPage.closePageUrl}" class="page-close hidden-xs ${border}"> --%>
<!--                             <i class="glyphicons halflings remove"></i> -->
<!--                         </a> -->
<%--                     </c:if> --%>
                    
<!--                     <div> -->
<%--                         <c:choose> --%>
<%--                             <c:when test="${userPage.defaultPage}"> --%>
<%--                                 <a href="${userPage.url}" class="${border}" title="${userPage.name}" data-toggle="tooltip" data-placement="bottom"> --%>
<!--                                     <i class="glyphicons halflings home"></i> -->
<!--                                 </a> -->
<%--                             </c:when> --%>
                            
<%--                             <c:otherwise> --%>
<%--                                 <a href="${userPage.url}" class="${border}" ${tooltipData}>${userPage.name}</a> --%>
<%--                             </c:otherwise> --%>
<%--                         </c:choose> --%>
<!--                     </div> -->
<!--                 </div> -->
<!--             </li> -->
<%--         </c:forEach> --%>
<!--     </ul> -->
</nav>
