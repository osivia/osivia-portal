<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="internationalization" prefix="is" %>


<c:if test="${not requestScope['osivia.user.administrator']}">
    <c:set var="toolbarDisplayClass" value="visible-xs" />
</c:if>

<div class="toolbar ${toolbarDisplayClass}">
    <div class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <!-- Toggle drawer menu button -->
                <button type="button" class="drawer-toggle-btn btn navbar-btn pull-left" data-toggle="drawer">
                    <i class="glyphicons show_lines"></i>
                    <span class="text"><is:getProperty key="MENU" /></span>
                </button>
    
                <!-- Toggle toolbar menu button -->
                <button type="button" class="btn navbar-btn visible-xs" data-toggle="collapse" data-target="#toolbar-content">
                    <i class="glyphicons halflings cog"></i>
                </button>
    
                <!-- Search -->
                <form class="navbar-form visible-xs" onsubmit="return onsubmitGlobalSearch(this);" method="post" role="search">
                    <div class="form-group">
                        <label class="sr-only" for="search-toolbar-input">Search</label>
                        <div class="input-group">
                            <input id="search-toolbar-input" type="text" name="keywords" class="form-control" placeholder='<is:getProperty key="SEARCH_PLACEHOLDER" />'>
                            <span class="input-group-btn">
                                <button type="submit" class="btn btn-default"><i class="glyphicons halflings search"></i></button>
                            </span>
                        </div>
                    </div>
                </form>
            </div>
    
            <div id="toolbar-content" class="collapse navbar-collapse">
                <c:choose>
                    <c:when test="${empty requestScope['osivia.toolbar.principal']}">
                        <ul class="nav navbar-nav navbar-right offline">
                            <!-- Login -->
                            <li>
                                <a href="${requestScope['osivia.toolbar.loginURL']}" class="navbar-link">
                                    <i class="glyphicons halflings log_in"></i>
                                    <is:getProperty key="LOGIN" />
                                </a>
                            </li>
                        </ul>
                    </c:when>
            
                    <c:otherwise>
                        <!-- Administration -->
                        <c:out value="${requestScope['osivia.toolbar.administrationContent']}" escapeXml="false" />
      
                        <!-- User links -->
                        <ul class="nav navbar-nav navbar-right">
                            <li class="hidden-xs">
                                <p class="navbar-text">
                                    <i class="glyphicons halflings user"></i>
                                    <span>${requestScope['osivia.toolbar.principal'].name}</span>
                                </p>
                            </li>
                            <li>
                                <a href="${requestScope['osivia.toolbar.signOutURL']}" class="navbar-link">
                                    <i class="glyphicons halflings log_out"></i>
                                    <is:getProperty key="LOGOUT" />
                                </a>
                            </li>
                            <li>
                                <a href="${requestScope['osivia.toolbar.refreshPageURL']}" class="navbar-link">
                                    <i class="glyphicons halflings refresh"></i>
                                    <is:getProperty key="REFRESH_PAGE" />
                                </a>
                            </li>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
