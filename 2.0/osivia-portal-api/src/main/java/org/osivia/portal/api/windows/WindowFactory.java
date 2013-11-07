package org.osivia.portal.api.windows;

import javax.portlet.PortletRequest;

import org.jboss.portal.core.model.portal.Window;

public class WindowFactory {
	
	public static PortalWindow getWindow( PortletRequest request)	{
		
		PortalWindow portalWindow = (PortalWindow) request.getAttribute("osivia.portal.window");
		if( portalWindow == null){
			Window window = (Window) request.getAttribute("osivia.window");
			portalWindow = new InternalWindow( window);
			request.setAttribute("osivia.portal.window", portalWindow);
		}
		return portalWindow;
	}
	
	
}
