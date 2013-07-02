<%@page import="java.util.Locale"%>
<%@page import="org.osivia.portal.api.internationalization.IInternationalizationService"%>
<%@page import="org.osivia.portal.core.constants.InternalConstants"%>
<%@page import="org.jboss.portal.core.controller.ControllerContext"%>
<%@page import="org.jboss.portal.portlet.Portlet"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.collections.CollectionUtils"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.osivia.portal.core.formatters.IFormatter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashSet"%>
<%@page import="org.jboss.portal.core.model.portal.PortalObjectPath"%>
<%@page import="org.jboss.portal.core.model.instance.InstanceDefinition"%>
<%@page import="org.jboss.portal.portlet.info.PortletInfo"%>
<%@page import="org.jboss.portal.core.portlet.info.PortletInfoInfo"%>
<%@page import="org.jboss.portal.core.portlet.info.PortletIconInfo"%>
<%@page import="org.jboss.portal.core.model.portal.Page"%>
<%@page import="java.util.Collection"%>
<%@page import="org.jboss.portal.theme.PortalLayout"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="org.jboss.portal.identity.Role"%>
<%@page import="org.jboss.portal.core.model.portal.Window"%>
<%@page import="org.jboss.portal.theme.PortalTheme"%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<%
//Internationalization service
IInternationalizationService is = (IInternationalizationService) request.getAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE);

//Formatter
IFormatter formatter = (IFormatter) request.getAttribute(InternalConstants.ATTR_WINDOWS_FORMATTER);
//Controller context
ControllerContext context = (ControllerContext) request.getAttribute(InternalConstants.ATTR_WINDOWS_CONTROLLER_CONTEXT);
// Command URL
String commandUrl = (String) request.getAttribute(InternalConstants.ATTR_WINDOWS_COMMAND_URL);
// Current page
Page currentPage = (Page) request.getAttribute(InternalConstants.ATTR_WINDOWS_PAGE);
// Current page windows list
@SuppressWarnings("unchecked")
List<Window> windows = (List<Window>) request.getAttribute(InternalConstants.ATTR_WINDOWS_CURRENT_LIST);

//Current page ID
String currentPageId = formatter.formatHtmlSafeEncodingId(currentPage.getId());

//Locale
Locale locale = request.getLocale();
%>


<script type="text/javascript" src="/osivia-portal-custom-web-assets/js/modal-message.js"></script>

<script type="text/javascript">

// Variables filled when opening fancybox
var regionId;
var windowId;

// Onclick action for add portlet formulaire submit
function selectPortlet(instanceId, formulaire) {
    formulaire.instanceId.value = instanceId;
    formulaire.regionId.value = regionId;
}

// Onclick action for delete portlet formulaire submit
function selectWindow(formulaire) {
	formulaire.windowId.value = windowId;
}

// Onclick action for display window settings
function insertWindowSettingsContent() {
	
}

// Toggle row display
function toggleRow(link, divClass) {
	var divToggleRow = $JQry(link).parents(".fancybox-table-row").siblings("." + divClass)[0];
	if (undefined != divToggleRow) {
		if (divToggleRow.style.display == "none") {
			divToggleRow.style.display = "table-row";			
		} else {
			divToggleRow.style.display = "none";
		}
		parent.jQuery.fancybox.update();
	}	
} 

</script>


<!-- Fancybox d'ajout de portlet -->
<div class="fancybox-content">
    <div id="add-portlet">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="addPortlet" />
            <input type="hidden" name="pageId" value="<%=currentPageId %>" />
            <input type="hidden" name="regionId" />
            <input type="hidden" name="instanceId" />

            <%=formatter.formatHtmlPortletsList(context) %>
            
            <div class="fancybox-center-content">
                <input type="button" value='<%=is.getString("CANCEL", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>


<!-- Fancyboxes d'affichage des paramètres des fenêtres -->
<%=formatter.formatHtmlWindowsSettings(currentPage, windows, context) %>


<!-- Fancybox de suppression de portlet -->
<div class="fancybox-content">
    <div id="delete-portlet">
        <form action="<%=commandUrl %>" method="get" class="fancybox-form">
            <input type="hidden" name="action" value="deleteWindow" />
            <input type="hidden" name="windowId" />
            
            <div class="fancybox-center-content">
                <p><%=is.getString("PORTLET_SUPPRESSION_CONFIRM_MESSAGE", locale) %></p>
            </div>
            <div class="fancybox-center-content">
                <input type="submit" value='<%=is.getString("YES", locale) %>' onclick="selectWindow(this.form)" />
                <input type="button" value='<%=is.getString("NO", locale) %>' onclick="closeFancybox()" />
            </div>
        </form>
    </div>
</div>

