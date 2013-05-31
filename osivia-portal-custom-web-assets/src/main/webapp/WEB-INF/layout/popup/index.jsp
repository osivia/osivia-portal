<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	   <title>popup</title>
	   <meta http-equiv="Content-Type" content="text/html;"/>
	   <!-- to correct the unsightly Flash of Unstyled Content. -->
	   <script type="text/javascript"></script>
	   	
	   <p:headerContent/>
	   
	   <p:theme themeName="osivia-popup"/>	   
	</head>

<body id="body">
<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>

<p:region regionName='popup' regionID='popup'/>

<p:region regionName='footer' regionID='footer'/>

<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>

</body>
</html>
