<%@page import="java.util.Locale"%>
<%@page import="org.osivia.portal.api.internationalization.IInternationalizationService"%>
<%@page import="org.osivia.portal.core.constants.InternalConstants"%>
<%@page import="org.osivia.portal.core.selection.portlet.SelectionPortlet"%>
<%@page import="org.osivia.portal.api.selection.SelectionItem"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/plain; charset=UTF-8"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
// Internationalization service
IInternationalizationService is = (IInternationalizationService) request.getAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE);
// Locale
Locale locale = request.getLocale();

//Selection items display
@SuppressWarnings("unchecked")
Set<SelectionItem> selectionItemsSet = (Set<SelectionItem>) request.getAttribute(SelectionPortlet.REQUEST_ATTRIBUTE_SELECTION);
%>

<portlet:defineObjects />


<div class="portlet-selection">

    <%    
    if ((selectionItemsSet != null) && (!selectionItemsSet.isEmpty())) {
    %>
    <ul>
    <%
        for (SelectionItem item : selectionItemsSet) {
        %>
    	<li>
    		<%=item.getDisplayTitle() %>
    		<a href="<portlet:actionURL>
    	                    <portlet:param name="<%=SelectionPortlet.REQUEST_PARAMETER_ACTION %>" value="<%=SelectionPortlet.REQUEST_PARAMETER_DELETE %>" />
    	                    <portlet:param name="<%=SelectionPortlet.REQUEST_PARAMETER_ITEM_ID %>" value="<%=item.getId() %>" />
    	                </portlet:actionURL>">
    				<img src="<%=renderRequest.getContextPath() %>/images/cross.png" />
    		</a>
    	</li>
    	<%
        }
        %>
    </ul>
    <%
    }
    %>
    
    <div class="delete-all">
        <a href="<portlet:actionURL>
                    <portlet:param name="<%=SelectionPortlet.REQUEST_PARAMETER_ACTION %>" value="<%=SelectionPortlet.REQUEST_PARAMETER_DELETE_ALL %>" />
                </portlet:actionURL>"><%=is.getString("SELECTION_PORTLET_DELETE_ALL", locale) %></a>
    </div>

</div>
