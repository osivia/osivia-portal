package org.osivia.portal.core.pagemarker;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.DelegatingURLFactoryService;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.WindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.invocation.response.ErrorResponse;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;
import org.osivia.portal.core.assistantpage.SecurePageCommand;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.TrackerBean;



/**
 * 
 * ajout d'un tag /pagemarker dans l'url pour associer à chaque page
 * l'état des portlets
 * 
 * @author jeanseb
 *
 */

public class PortalDelegatingURLFactoryService extends DelegatingURLFactoryService {
	
	protected static final Log logger = LogFactory.getLog(PortalDelegatingURLFactoryService.class);

	private ITracker tracker;
	
	public ITracker getTracker() {
		return tracker;
	}
	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}


	public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand cmd) {
		
		ServerURL url = super.doMapping(controllerContext, invocation, cmd);
		
		boolean pageMarkerInsertion = true;
		
		if (cmd instanceof PermLinkCommand) 
			pageMarkerInsertion = false;
		
		if (cmd instanceof CmsCommand) {
			if( !((CmsCommand) cmd).isInsertPageMarker())
				pageMarkerInsertion = false;
		}
		
		if( ! (cmd instanceof InvokePortletWindowResourceCommand) && pageMarkerInsertion)	{
		
			String pageMarker =  PageMarkerUtils.getCurrentPageMarker(controllerContext);
			

			if (url != null && pageMarker != null) {
				url.setPortalRequestPath(PageMarkerUtils.PAGE_MARKER_PATH+ pageMarker + url.getPortalRequestPath());
			}
		}
		

		return url;
	}

}
