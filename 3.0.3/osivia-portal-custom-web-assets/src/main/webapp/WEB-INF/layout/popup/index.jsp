<%@ page import="org.jboss.portal.server.PortalConstants" %>
<%@ taglib uri="/WEB-INF/theme/portal-layout.tld" prefix="p" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><%= PortalConstants.VERSION.toString() %></title>
    <meta http-equiv="Content-Type" content="text/html;"/>
    <!-- to correct the unsightly Flash of Unstyled Content. -->
    <script type="text/javascript"></script>

    <p:headerContent/>
    
	   <p:theme themeName="osivia-popup"/>	 
 
</head>

<body id="body">

<p:region regionName='AJAXScripts' regionID='AJAXScripts'/>

<p:region regionName='popup_header' regionID='popup_header'/>

<div id="portal-container">
    <div id="sizer">
        <div id="expander">
            <div id="content-container">
            <table width="100%">
              <tr>
                <td>
                  <p:region regionName='notifications' regionID='notifications' />
                </td>
              </tr>
              <tr>
                <td valign="top" width="100%">
                   <p:region regionName='popup' regionID='popup'/>
                 </td>
              </tr>
            </table>
                
           </div>

        </div>
    </div>
</div>



<p:region regionName='AJAXFooter' regionID='AJAXFooter'/>
</body>
</html>
