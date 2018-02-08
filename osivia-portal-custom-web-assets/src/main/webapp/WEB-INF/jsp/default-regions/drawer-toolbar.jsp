<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<c:set var="userPortal" value="${requestScope['osivia.userPortal']}" />
<c:set var="userPages" value="${userPortal.userPages}" />
<c:set var="userProfileUrl" value="${requestScope['osivia.toolbar.myprofile']}" />
<c:set var="userSettingsUrl" value="${requestScope['osivia.toolbar.userSettings.url']}" />

<c:set var="searchPlaceholder"><op:translate key="SEARCH_PLACEHOLDER" /></c:set>


<div class="visible-xs clearfix">
    <div class="col-xs-12 drawer-toolbar-header">
        <div>
            <!-- Search -->
            <div class="drawer-toolbar-search">
                <div class="media">
                    <div class="media-left">
                        <a href="#" type="button" onclick="hideDrawerSearch();" class="btn">
                            <i class="halflings halflings-arrow-left"></i>
                            <span class="sr-only"></span>
                        </a>
                    </div>
                
                    <div class="media-body">
                        <form action="${requestScope['osivia.search.url']}" method="get" role="search">
                            <input type="hidden" name="action" value="advancedSearch">
                        
                            <div class="input-group">
                                <input type="text" name="search" class="form-control" placeholder="${searchPlaceholder}">
                                
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
                <div class="clearfix">
                    <div class="pull-left">
                        <p class="text-overflow">
                            <a href="${requestScope['osivia.home.url']}">
                                <span>${requestScope['osivia.header.application.name']}</span>
                            </a>
                        </p>
                    </div>
                    
                    <div class="pull-right text-nowrap">
                        <button type="button" onclick="showDrawerSearch();" class="btn btn-link">
                            <i class="halflings halflings-search"></i>
                        </button>
                        
                        <c:choose>
                            <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                                <a href="${requestScope['osivia.toolbar.loginURL']}" class="btn btn-link">
                                    <i class="halflings halflings-log-in"></i>
                                </a>
                            </c:when>
                            
                            <c:otherwise>
                                <div class="btn-group dropdown">
                                    <button type="button" class="btn btn-link dropdown-toggle" data-toggle="dropdown">
                                        <c:choose>
                                            <c:when test="${empty requestScope['osivia.toolbar.person']}"><i class="halflings halflings-user"></i></c:when>
                                            <c:otherwise><img class="avatar" src="${requestScope['osivia.toolbar.person'].avatar.url}" /></c:otherwise>
                                        </c:choose>
                                        <span class="caret"></span>
                                    </button>
    
                                    <ul class="dropdown-menu dropdown-menu-right" role="menu">
                                        <li class="dropdown-header" role="presentation">${requestScope['osivia.toolbar.person'].displayName}</li>
                                    
                                        <!-- User profile -->
                                        <c:if test="${not empty userProfileUrl}">
                                            <li role="presentation">
                                                <a href="${userProfileUrl}" role="menuitem">
                                                    <i class="glyphicons glyphicons-nameplate"></i>
                                                    <span><op:translate key="MY_PROFILE" /></span>
                                                </a>
                                            </li>
                                        </c:if>
                                        
                                        <!-- User settings -->
                                        <c:if test="${not empty userSettingsUrl}">
                                            <li role="presentation">
                                                <a href="${userSettingsUrl}" role="menuitem">
                                                    <i class="glyphicons glyphicons-cogwheel"></i>
                                                    <span><op:translate key="USER_SETTINGS"/></span>
                                                </a>
                                            </li>
                                        </c:if>
    
                                        <c:if test="${not empty userProfileUrl or not empty userSettingsUrl}">
                                            <li class="divider" role="presentation"></li>
                                        </c:if>
                                        
                                        <li role="presentation">
                                            <a href="#" onclick="logout()" role="menuitem">
                                                <i class="halflings halflings-log-out"></i>
                                                <span><op:translate key="LOGOUT" /></span>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${not requestScope['osivia.spaceSite']}">
        <div class="col-xs-12 drawer-toolbar-tabs">
            <div class="dropdown">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                    <span class="pull-right"><span class="caret"></span></span>
                    <span class="text-overflow">${requestScope['osivia.currentPageName']}</span>
                </button>
                
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
    </c:if>
</div>
