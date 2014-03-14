/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
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
