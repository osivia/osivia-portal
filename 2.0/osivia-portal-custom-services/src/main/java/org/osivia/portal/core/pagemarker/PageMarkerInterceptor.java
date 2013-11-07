package org.osivia.portal.core.pagemarker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowActionCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.osivia.portal.core.page.PageProperties;


/**
 * Gestion des états de la page
 * 
 * (permettent de gérér le back et le multi-onglets)
 * 
 * @author jeanseb
 *
 */
public class PageMarkerInterceptor extends ControllerInterceptor {

	
	/** . */
	protected static final Log logger = LogFactory.getLog(PageMarkerInterceptor.class);

	/** . */


	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
		
		
		ControllerResponse resp;
		
		/* Initialisation page marker */
		
		{
		ControllerContext controllerCtx = cmd.getControllerContext();
		
		if( cmd instanceof InvokePortletWindowResourceCommand)	{
			
			/*
			 * String id = (( InvokePortletWindowResourceCommand) cmd).getResourceId();
			if( id.startsWith("/pagemarker/"))
				PageMarkerUtils.getCurrentPageMarker(controllerCtx);
			else
			// Pas de page marker pour les ressources 
			*/
			controllerCtx.setAttribute(Scope.REQUEST_SCOPE, "currentPageMarker", "0");
		
		}	else
			// Initialisation standard
			PageMarkerUtils.getCurrentPageMarker(controllerCtx);
		}
		
		
		// v2.MS Calcul du nom de portail
		// TODO factoriser dans un portal manager
		if( cmd instanceof PortalCommand){
			String portalName = ((PortalCommand) cmd).getPortal().getName();
			PageProperties.getProperties().getPagePropertiesMap().put("portalName", portalName);
			
		}
		

		 resp = (ControllerResponse) cmd.invokeNext();
		

			
		/* Sauvegarde des états en mode ajax */
		
		 boolean alreadySaved = false;
		 
		if (cmd instanceof InvokePortletWindowCommand && (ControllerContext.AJAX_TYPE == cmd.getControllerContext().getType())) {
			
			ControllerContext controllerCtx = cmd.getControllerContext();
			
			Window window = ((InvokePortletWindowCommand)cmd).getWindow();
			
			//  sauvegarde des infos associées au markeur de page
			
			PageMarkerUtils.savePageState(controllerCtx, window.getPage());
			
			alreadySaved = true;
		}

		// 2.0.4 : redirection depuis une action portlet		
		if (!alreadySaved && cmd instanceof InvokePortletWindowActionCommand ) {
			
			ControllerContext controllerCtx = cmd.getControllerContext();
			
			Window window = ((InvokePortletWindowActionCommand)cmd).getWindow();
			
			PageMarkerUtils.savePageState(controllerCtx, window.getPage());
			
			alreadySaved = true;
		}
			
		if (!alreadySaved && cmd instanceof RenderPageCommand) {
				
				ControllerContext controllerCtx = cmd.getControllerContext();
				Page page = ((PageCommand)cmd).getPage();
				
				//  sauvegarde des infos associées au markeur de page
				
				PageMarkerUtils.savePageState(controllerCtx, page);
			}
			
		
		//
		return resp;
	}


}
