<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.osivia.org/jsp/taglib/osivia-portal" prefix="op" %>

<%@ page contentType="text/html" isELIgnored="false"%>


<portlet:defineObjects />

<portlet:resourceURL id="lazyLoading" var="lazyLoadingURL">
    <portlet:param name="cmsBasePath" value="${cmsBasePath}" />
    <portlet:param name="live" value="true" />
    <portlet:param name="link" value="true" />
    <portlet:param name="displayContext" value="proxy_preview" />
    <portlet:param name="popup" value="true" />
    <portlet:param name="highlight" value="true" />
    
    <c:if test="${not empty cmsNavigationPath}">
        <portlet:param name="cmsNavigationPath" value="${cmsNavigationPath}" />
    </c:if>
    
    <c:if test="${not empty excludedTypes}">
        <portlet:param name="excludedTypes" value="${excludedTypes}" />
    </c:if>
</portlet:resourceURL>



<div>
    <h3>
        <i class="glyphicons glyphicons-book-open"></i>
        <span><op:translate key="BROWSE_PORTLET_TITLE" /></span>
    </h3>
    
    
    <div class="row">
        <div class="col-sm-6 col-md-7 col-lg-8">
            <h4><op:translate key="BROWSE_PORTLET_DOCUMENTS" /></h4>
            
            <div class="panel panel-default">
                <div class="panel-body">
                    <div class="fancytree fancytree-browser fixed-height" data-lazyloadingurl="${lazyLoadingURL}">
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-sm-6 col-md-5 col-lg-4">
            <h4><op:translate key="BROWSE_PORTLET_LEGEND" /></h4>
            
            <div class="well">
                <dl>
                    <dt><op:translate key="BROWSE_PORTLET_DOCUMENT_STATES" /></dt>
                    <dd>
                        <ul>
                            <li><op:translate key="BROWSE_PORTLET_DOCUMENT_UP_TO_DATE" /></li>
                            <li class="text-info"><op:translate key="BROWSE_PORTLET_DOCUMENT_ONGOING_CHANGES" /></li>
                        </ul>
                    </dd>
                </dl>
            
                <p>
                    <strong><op:translate key="BROWSE_PORTLET_DOCUMENT_CURRENT" /></strong>
                </p>
            </div>
        </div>
    </div>
</div>
