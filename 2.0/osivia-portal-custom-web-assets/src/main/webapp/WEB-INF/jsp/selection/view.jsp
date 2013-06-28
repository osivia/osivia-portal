<%@page import="org.osivia.portal.api.selection.SelectionItem"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/plain; charset=UTF-8"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<div class="portlet-selection">

    <%
    // Selection items display
    @SuppressWarnings("unchecked")
    Set<SelectionItem> selectionItemsSet = (Set<SelectionItem>) renderRequest.getAttribute("selection");
    if ((selectionItemsSet != null) && (!selectionItemsSet.isEmpty())) {
    %>
    <ul>
    <%
        for (SelectionItem item : selectionItemsSet) {
        %>
    	<li>
    		<%=item.getDisplayTitle() %>
    		<a href="<portlet:actionURL>
    	                    <portlet:param name="action" value="delete" />
    	                    <portlet:param name="id" value="<%=item.getId() %>" />
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
                    <portlet:param name="action" value="deleteAll" />
                </portlet:actionURL>">Tout supprimer</a>
    </div>

</div>
