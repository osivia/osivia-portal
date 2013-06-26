<%@page import="org.osivia.portal.core.portlets.sitemap.SitemapFormatter"%>
<%@page import="org.osivia.portal.core.portlets.sitemap.SitemapPortlet"%>
<%@page import="org.osivia.portal.core.sitemap.Sitemap"%>
<%@ page contentType="text/plain; charset=UTF-8"%>
<%@page import="java.io.IOException"%>
<%@page import="org.jboss.portal.core.controller.ControllerContext"%>
<%@page import="org.osivia.portal.api.Constants"%>
<%@page import="org.osivia.portal.core.formatters.IFormatter"%>
<%@page import="java.util.ResourceBundle"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<portlet:defineObjects />

<%
    // Resource bundle
    //ResourceBundle rb = ResourceBundle.getBundle(IFormatter.RESOURCE_BUNDLE_NAME, request.getLocale());

    //Formatter
    SitemapFormatter formatter = (SitemapFormatter) request.getAttribute(SitemapPortlet.ATTR_SITEMAP_FORMATTER);

    //Current page
    //Page currentPage = (Page) request.getAttribute(Constants.ATTR_TOOLBAR_SETTINGS_PAGE);

    //Controller context
    ControllerContext context = (ControllerContext) request.getAttribute(SitemapPortlet.ATTR_SITEMAP_CONTEXT);
    
    Sitemap itemToDisplay = (Sitemap) renderRequest.getAttribute(SitemapPortlet.ATTR_SITEMAP_ITEMS);
%>


<form action="osivia.sitemap.go" method="get" class="fancybox-form">
	<input type="hidden" name="action" value="makeDefaultPage" /> 
	<input type="hidden" name="jstreeSitemap" />

	<div class="fancybox-table">
		<div class="fancybox-table-row">
			<div class="fancybox-table-cell">
				<input type="text" placeholder="Filtrer" onkeyup="jstreeSearch('jstreeSitemap', this.value)" />
			</div>
		</div>
		
		<div class="fancybox-table-row">
			<div class="fancybox-table-cell">
				<div id="jstreeSitemap" class="jstree-links">
					<%=formatter.formatHtmlTreeSitemap(itemToDisplay)%>
				</div>
			</div>
		</div>
	</div>
</form>
