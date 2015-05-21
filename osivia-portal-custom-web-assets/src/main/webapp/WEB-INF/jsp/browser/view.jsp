<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="internationalization" prefix="is" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:resourceURL id="lazyLoading" var="lazyLoadingURL">
    <portlet:param name="live" value="true" />
    <portlet:param name="link" value="true" />
    <portlet:param name="displayContext" value="proxy_preview" />
    <portlet:param name="popup" value="true" />
    
    <c:if test="${not empty cmsBasePath}">
        <portlet:param name="cmsBasePath" value="${cmsBasePath}" />
    </c:if>
</portlet:resourceURL>



<div>
    <p class="lead">
        <i class="glyphicons glyphicons-search"></i>
        <span><is:getProperty key="BROWSE_PORTLET_LEGEND" /></span>
    </p>
    
    <dl class="dl-horizontal">
        <dt><is:getProperty key="BROWSE_PORTLET_HELP_TITLE" /></dt>
        <dd>
            <span><is:getProperty key="BROWSE_PORTLET_HELP_DESCRIPTION_PUBLISHED" /></span>
            <br>
            <span class="text-info"><is:getProperty key="BROWSE_PORTLET_HELP_DESCRIPTION_EDITED" /></span>
        </dd>
    </dl>
    
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingURL}">
            </div>
        </div>
    </div>
</div>


