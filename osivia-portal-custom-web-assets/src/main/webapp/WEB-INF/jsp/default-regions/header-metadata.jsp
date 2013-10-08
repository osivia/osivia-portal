<%@page import="org.osivia.portal.api.Constants" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
	String content = (String) request.getAttribute(Constants.ATTR_HEADER_METADATA_CONTENT);
%>
<%=content%>