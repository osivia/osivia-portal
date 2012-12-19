package fr.toutatice.portail.api.windows;

import javax.portlet.PortletRequest;

import org.jboss.portal.core.model.portal.Window;

public class WindowFactory {
	
	public static PortalWindow getWindow( PortletRequest request)	{
		
		PortalWindow portalWindow = (PortalWindow) request.getAttribute("pia.portal.window");
		if( portalWindow == null){
			Window window = (Window) request.getAttribute("pia.window");
			portalWindow = new InternalWindow( window);
			request.setAttribute("pia.portal.window", portalWindow);
		}
		return portalWindow;
	}
	
	
}
