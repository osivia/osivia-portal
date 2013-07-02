<%@page import="org.osivia.portal.core.selection.portlet.SelectionPortlet"%>
<%@page import="java.util.Locale"%>
<%@page import="org.osivia.portal.core.constants.InternalConstants"%>
<%@page import="org.osivia.portal.api.internationalization.IInternationalizationService"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<%
// Internationalization service
IInternationalizationService is = (IInternationalizationService) request.getAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE);
// Locale
Locale locale = request.getLocale();

// Selection ID
String selectionId = (String) request.getAttribute(SelectionPortlet.REQUEST_ATTRIBUTE_SELECTION_ID);
%>

<portlet:defineObjects/>

<div>
	<form method="post" action="<portlet:actionURL />">	
		<label><%=is.getString("SELECTION_PORTLET_ID_LABEL", locale) %></label>
        <input type="text" name="<%=SelectionPortlet.REQUEST_ATTRIBUTE_SELECTION_ID %>" value="<%=selectionId %>" />
		<input type="submit" name="<%=SelectionPortlet.REQUEST_PARAMETER_SAVE %>" value="<%=is.getString("SAVE", locale) %>" />
		<input type="submit" name="<%=SelectionPortlet.REQUEST_PARAMETER_CANCEL %>" value="<%=is.getString("CANCEL", locale) %>" />
	</form>
</div>
