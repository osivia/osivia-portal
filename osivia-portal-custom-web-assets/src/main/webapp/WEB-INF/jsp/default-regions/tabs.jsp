<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var="userPages" value="${requestScope['osivia.userPortal'].userPages}" />


<nav role="navigation">
<!--     <div class="clearfix visible-xs tabs-offcanvas"> -->
<!--         Toggle offcanvas -->
<!--         <button type="button" class="btn btn-default offcanvas-toggle-btn" data-toggle="offcanvas"> -->
<!--             <span class="glyphicon glyphicon-list"></span> -->
<!--         </button> -->
        
<!--         Current page name -->
<%--         <h2>${requestScope['osivia.currentPageName']}</h2> --%>
<!--     </div> -->

    <ul class="btn-toolbar tabs-menu">
        <c:forEach var="userPage" items="${userPages}" varStatus="status">
            <c:if test="${'templates' != fn:toLowerCase(userPage.name)}">
                <c:set var="buttonType" value="btn-default" />
                <c:if test="${userPage.id == requestScope['osivia.currentPageId']}">
                    <c:set var="buttonType" value="btn-primary" />
                </c:if>
                
                <li class="btn-group">
                    <a href="${userPage.url}" class="btn ${buttonType}">
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


<!-- <nav role="navigation"> -->
<!--     <ul class="btn-toolbar"> -->
<%--         <c:forEach var="userPage" items="${userPages}" varStatus="status"> --%>
<%--             <c:if test="${status.count == 4}"> --%>
<!--                 Submenu toggle button -->
<!--                 <li class="visible-xs btn-group pull-right"> -->
<!--                     <a href="#" class="btn btn-default" data-toggle="responsive-tabs-submenu"> -->
<!--                         <span class="glyphicon glyphicon-chevron-down"></span> -->
<!--                     </a> -->
<!--                 </li> -->
            
<!--                 Submenu -->
<!--                 <li> -->
<!--                     <ul class="responsive-tabs-submenu btn-toolbar"> -->
<%--             </c:if> --%>
        
<%--             <c:set var="buttonType" value="btn-default" /> --%>
<%--             <c:if test="${userPage.id == requestScope['osivia.currentPageId']}"> --%>
<%--                 <c:set var="buttonType" value="btn-primary" /> --%>
<%--             </c:if> --%>
        
        
<!--             <li class="btn-group"> -->
<%--                 <a href="${userPage.url}" class="btn ${buttonType}"> --%>
<%--                     <span class="tabs-title">${userPage.name}</span> --%>
<!--                 </a> -->
                
<!--                 Close -->
<%--                 <c:if test="${not empty userPage.closePageUrl}"> --%>
<%--                     <a href="${userPage.closePageUrl}" class="btn ${buttonType}"> --%>
<!--                         <span class="glyphicon glyphicon-remove"></span> -->
<!--                     </a> -->
<%--                 </c:if> --%>
<!--             </li> -->
        
        
<%--             <c:if test="${status.last && status.count >= 4}"> --%>
<!--                     </ul> -->
<!--                 </li> -->
<%--             </c:if> --%>
<%--         </c:forEach> --%>
<!--     </ul> -->
<!-- </nav> -->



<!-- NAVIGATION PILLS -->
<!-- <nav class="hidden-xs" role="navigation"> -->
<!--     <ul class="nav nav-pills"> -->
<%--         <c:forEach var="userPage" items="${userPages}" varStatus="status" > --%>
<%--             <c:if test="${'templates' != fn:toLowerCase(userPage.name)}">             --%>
<%--                 <c:if test="${userPage.id == requestScope['osivia.currentPageId']}"> --%>
<%--                     <c:set var="className" value="active" /> --%>
<%--                 </c:if> --%>
    
<%--                 <li class="${className}"> --%>
<%--                     <a href="${userPage.url}">${userPage.name}</a> --%>
<!--                 </li> -->
    
<%--                 <c:if test="${fn:length(userPage.children) > 0}"> --%>
<!--                     <li class="dropdown"> -->
<!--                         <a class="dropdown-toggle" data-toggle="dropdown" href="#"> -->
<!--                             <span class="caret"></span> -->
<!--                         </a> -->
<!--                         <ul class="dropdown-menu"> -->
<%--                             <c:forEach var="child" items="${userPage.children}"> --%>
<!--                                 <li> -->
<%--                                     <a href="${child.url}">${child.name}</a> --%>
<!--                                 </li> -->
<%--                             </c:forEach> --%>
<!--                         </ul> -->
<!--                     </li> -->
<%--                 </c:if> --%>
                
<%--                 <c:remove var="className" /> --%>
<%--             </c:if> --%>
<%--         </c:forEach> --%>
<!--     </ul> -->
<!-- </nav> -->


<!-- BUTTONS TOOLBAR -->
<!-- <nav class="hidden-xs"> -->
<!--     <ul class="btn-toolbar"> -->
<%--         <c:forEach var="userPage" items="${userPages}" varStatus="status" > --%>
<%--             <c:if test="${'templates' != fn:toLowerCase(userPage.name)}"> --%>
<%--                 <c:set var="button" value="btn-default" /> --%>
<%--                 <c:if test="${userPage.id == requestScope['osivia.currentPageId']}"> --%>
<%--                     <c:set var="button" value="btn-primary" /> --%>
<%--                 </c:if> --%>
                
<%--                 <c:if test="${fn:length(userPage.children) > 0}"> --%>
<%--                     <c:set var="dropdown" value="dropdown" /> --%>
<%--                 </c:if> --%>

<%--                 <li class="btn-group ${dropdown}"> --%>
<%--                     <a href="${userPage.url}" class="btn ${button}">${userPage.name}</a> --%>
                    
<!--                     Children -->
<%--                     <c:if test="${fn:length(userPage.children) > 0}"> --%>
<%--                         <a class="btn ${button} dropdown-toggle" data-toggle="dropdown" href="#"> --%>
<!--                             <span class="caret"></span> -->
<!--                         </a> -->
<!--                         <ul class="dropdown-menu"> -->
<%--                             <c:forEach var="child" items="${userPage.children}"> --%>
<!--                                 <li> -->
<%--                                     <a href="${child.url}">${child.name}</a> --%>
<!--                                 </li> -->
<%--                             </c:forEach> --%>
<!--                         </ul> -->
<%--                     </c:if> --%>
                    
<!--                     Close -->
<%--                     <c:if test="${not empty userPage.closePageUrl}"> --%>
<%--                         <a href="${userPage.closePageUrl}" class="btn ${button}"> --%>
<!--                             <span class="glyphicon glyphicon-remove"></span> -->
<!--                         </a> -->
<%--                     </c:if> --%>
<!--                 </li> -->
                
<%--                 <c:remove var="dropdown" /> --%>
<%--             </c:if> --%>
<%--         </c:forEach> --%>
<!--     </ul> -->
<!-- </nav> -->

