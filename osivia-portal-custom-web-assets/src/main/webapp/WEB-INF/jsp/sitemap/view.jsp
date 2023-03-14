<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>


<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<%--@elvariable id="cmsBasePath" type="java.lang.String"--%>
<%--@elvariable id="cmsNavigationPath" type="java.lang.String"--%>
<%--@elvariable id="live" type="java.lang.Boolean"--%>
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
    <h3 class="h5 mb-3">
        <i class="glyphicons glyphicons-basic-map-marker"></i>
        <span><op:translate key="SITEMAP_PORTLET_TITLE" /></span>
    </h3>

    <div class="card mb-3 text-bg-light border-0">
        <div class="card-body">
            <h3 class="card-title"><op:translate key="LEGEND" /></h3>
            <p class="card-text mb-0"><op:translate key="DOCUMENT_UP_TO_DATE" /></p>
            <p class="card-text mb-0 text-info"><op:translate key="DOCUMENT_ONGOING_CHANGES" /></p>
            <p class="card-text mb-0 fw-bold"><op:translate key="DOCUMENT_CURRENT" /></p>
        </div>
    </div>

    <c:choose>
        <c:when test="${live}">
            <div class="card border-info">
                <div class="card-header text-bg-info">Versions de travail</div>
                <div class="card-body">
                    <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingUrl}"></div>
                </div>
            </div>
        </c:when>
        
        <c:otherwise>
            <div class="card border-success">
                <div class="card-header text-bg-success">Versions en ligne</div>
                <div class="card-body">
                    <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingUrl}"></div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
