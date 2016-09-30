<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>


<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:resourceURL id="lazyLoading" var="lazyLoadingUrl">
    <portlet:param name="cmsBasePath" value="${cmsBasePath}" />
    <portlet:param name="cmsNavigationPath" value="${cmsNavigationPath}" />
    <portlet:param name="link" value="true" />
    <portlet:param name="live" value="${live}" />
    <portlet:param name="includedTypes" value="PortalSite,PortalPage" />
    <portlet:param name="forceReload" value="true" />
    <portlet:param name="popup" value="true" />
    <portlet:param name="highlight" value="true" />
</portlet:resourceURL>


<div>
    <p class="lead">
        <i class="glyphicons glyphicons-map-marker"></i>
        <span><op:translate key="SITEMAP_PORTLET_TITLE" /></span>
    </p>

    <dl class="dl-horizontal">
        <dt><op:translate key="LEGEND" /></dt>
        <dd>
            <span><op:translate key="DOCUMENT_UP_TO_DATE" /></span>
            <br>
            <span class="text-warning"><op:translate key="DOCUMENT_ONGOING_CHANGES" /></span>
            <br>
            <span><strong><op:translate key="DOCUMENT_CURRENT" /></strong></span>
        </dd>
    </dl>

    <c:choose>
        <c:when test="${live}">
            <div class="panel panel-info">
                <div class="panel-heading">Versions de travail</div>
            
                <div class="panel-body">
                    <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingUrl}"></div>
                </div>
            </div>
        </c:when>
        
        <c:otherwise>
            <div class="panel panel-success">
                <div class="panel-heading">Versions en ligne</div>
            
                <div class="panel-body">
                    <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingUrl}"></div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
