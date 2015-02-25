<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="internationalization" prefix="is"%>


<c:set var="userPortal" value="${requestScope['osivia.userPortal']}" />
<c:set var="userPages" value="${userPortal.userPages}" />

<c:set var="searchPlaceholder"><is:getProperty key="SEARCH_PLACEHOLDER" /></c:set>


<div class="visible-xs clearfix">
    <div class="col-xs-12 drawer-toolbar-header">
        <div>
            <!-- Search -->
            <div class="drawer-toolbar-search">
                <div class="media">
                    <div class="media-left">
                        <a href="#" onclick="hideDrawerSearch();" class="btn">
                            <i class="halflings halflings-arrow-left"></i>
                        </a>
                    </div>
                
                    <div class="media-body">
                        <form onsubmit="return onsubmitGlobalSearch(this);" method="post" role="search">
                            <div class="input-group">
                                <input type="text" name="keywords" class="form-control" placeholder="${searchPlaceholder}">
                                
                                <span class="input-group-btn">    
                                    <button type="submit" class="btn btn-default">
                                        <i class="halflings halflings-search"></i>
                                    </button>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        
            <!-- Title -->
            <div class="drawer-toolbar-title">
                <div class="media">
                    <div class="media-body">
                        <div>
                            <p class="text-middle text-overflow">
                                <img src="${pageContext.request.contextPath}/img/header/brand.png" alt="">
                                <span><is:getProperty key="BRAND" /></span>
                            </p>
                        </div>
                    </div>
                
                    <div class="media-right text-nowrap">
                        <a href="#" onclick="showDrawerSearch();" class="btn btn-link">
                            <i class="halflings halflings-search"></i>
                        </a>
                        
                        <c:choose>
                            <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                                <a href="${requestScope['osivia.toolbar.loginURL']}" class="btn btn-link">
                                    <i class="halflings halflings-log-in"></i>
                                </a>
                            </c:when>
                            
                            <c:otherwise>
                                <a href="${requestScope['osivia.toolbar.signOutURL']}" class="btn btn-link">
                                    <i class="halflings halflings-log-out"></i>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-xs-12 drawer-toolbar-tabs">
        <div class="dropdown">
            <a href="#" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                <span class="pull-right"><small><i class="halflings halflings-triangle-bottom"></i></small></span>
                <span class="text-overflow">${requestScope['osivia.currentPageName']}</span>
            </a>
            
            <ul class="dropdown-menu" role="menu">
                <c:if test="${not empty requestScope['osivia.currentPageURL']}">
                    <!-- Tab home -->
                    <li role="presentation">
                        <a href="${requestScope['osivia.currentPageURL']}" class="text-overflow" role="menuitem">
                            <i class="halflings halflings-home"></i>
                            <span>${requestScope['osivia.currentPageName']}</span>
                        </a>
                    </li>
                    
                    <!-- Divider -->
                    <li class="divider" role="presentation"></li>
                </c:if>
            
                <!-- Portal home -->
                <c:if test="${not empty userPortal.defaultPage}">
                    <li role="presentation"
                        <c:if test="${userPortal.defaultPage.id eq requestScope['osivia.currentPageId']}">class="active"</c:if>
                    >
                        <a href="${userPortal.defaultPage.url}" class="text-overflow" role="menuitem">
                            <i class="halflings halflings-home"></i>
                            <span>${userPortal.defaultPage.name}</span>
                        </a>
                    </li>
                </c:if>
            
                <c:forEach var="userPage" items="${userPages}">
                    <c:if test="${not userPage.defaultPage}">
                        <li role="presentation"
                            <c:if test="${userPage.id eq requestScope['osivia.currentPageId']}">class="active"</c:if>
                        >
                            <a href="${userPage.url}" class="text-overflow" role="menuitem">${userPage.name}</a>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
