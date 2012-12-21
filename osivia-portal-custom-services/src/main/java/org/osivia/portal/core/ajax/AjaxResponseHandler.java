package org.osivia.portal.core.ajax;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.util.MarkupInfo;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.portlet.ControllerPortletControllerContext;
import org.jboss.portal.core.controller.portlet.ControllerPageNavigationalState;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SignOutResponse;
import org.jboss.portal.core.controller.handler.AjaxResponse;
import org.jboss.portal.core.controller.handler.CommandForward;
import org.jboss.portal.core.controller.handler.HTTPResponse;
import org.jboss.portal.core.controller.handler.HandlerResponse;
import org.jboss.portal.core.controller.handler.ResponseHandler;
import org.jboss.portal.core.controller.handler.ResponseHandlerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.command.response.MarkupResponse;
import org.jboss.portal.core.model.portal.command.response.PortletWindowActionResponse;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.response.UpdateWindowResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateChange;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.core.navstate.NavigationalStateObjectChange;
import org.jboss.portal.core.theme.WindowContextFactory;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageLocationResponse;
import org.jboss.portal.theme.impl.render.dynamic.response.UpdatePageStateResponse;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.jboss.portal.web.ServletContextDispatcher;
import org.osivia.portal.core.page.PageCustomizerInterceptor;


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Ajoute une commande de redirection (sert notamment pour les erreurs sur les actions Ajax)
 * Coordination des publics parameters en AJAX (par défaut, les publics parameters obligent à recharger la page)
 *
 */
public class AjaxResponseHandler implements ResponseHandler {
	
	
	protected static final Log log = LogFactory.getLog(AjaxResponseHandler.class);

	   /** . */
	   private PortalObjectContainer portalObjectContainer;

	   /** . */
	   private PageService pageService;

	   public PortalObjectContainer getPortalObjectContainer()
	   {
	      return portalObjectContainer;
	   }

	   public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer)
	   {
	      this.portalObjectContainer = portalObjectContainer;
	   }

	   public PageService getPageService()
	   {
	      return pageService;
	   }

	   public void setPageService(PageService pageService)
	   {
	      this.pageService = pageService;
	   }

	   public HandlerResponse processCommandResponseOrginal(
	      ControllerContext controllerContext,
	      ControllerCommand commeand,
	      ControllerResponse controllerResponse) throws ResponseHandlerException
	   {
	   if (controllerResponse instanceof PortletWindowActionResponse)
	      {
	         PortletWindowActionResponse pwr = (PortletWindowActionResponse)controllerResponse;
	         StateString contentState = pwr.getContentState();
	         WindowState windowState = pwr.getWindowState();
	         Mode mode = pwr.getMode();
	         ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(
	            pwr.getWindowId(),
	            mode,
	            windowState,
	            contentState);
	         if (renderCmd != null)
	         {
	            return new CommandForward(renderCmd, null);
	         }
	         else
	         {
	            return null;
	         }
	      }
	      else if (controllerResponse instanceof SignOutResponse)
	      {
	         // Get the optional signout location
	         String location = ((SignOutResponse)controllerResponse).getLocation();

	         final ServerInvocation invocation = controllerContext.getServerInvocation();

	         //
	         if (location == null)
	         {
	            PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();
	            Portal portal = (Portal)portalObjectContainer.getContext().getDefaultPortal();
	            ViewPageCommand renderCmd = new ViewPageCommand(portal.getId());
	            URLContext urlContext = invocation.getServerContext().getURLContext();
	            location = controllerContext.renderURL(renderCmd, urlContext.asNonAuthenticated(), null);
	         }

	         // Indicate that we want a sign out to be done
	         invocation.getResponse().setWantSignOut(true);
	         
	         // We perform a full refresh
	         UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(location);
	         return new AjaxResponse(dresp);
	      }
	      else if (controllerResponse instanceof UpdatePageResponse)
//	      {
//	         UpdatePageResponse upr = (UpdatePageResponse)controllerResponse;
//	         ViewPageCommand rpc = new ViewPageCommand(upr.getPageId());
//	         String url = controllerContext.renderURL(rpc, null, null);
//	         UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
//	         return new AjaxResponse(dresp);
//	      }
//	      else if (controllerResponse instanceof UpdateWindowResponse)
	      {
	    	 boolean reloadAjaxWindows = false; 
	    	  
	         UpdatePageResponse upw = (UpdatePageResponse)controllerResponse;

	         // Obtain page and portal
//	         final Window window = (Window)portalObjectContainer.getObject(upw.getWindowId());
//	         Page page = (Page)window.getParent();
	         final Page page = (Page)portalObjectContainer.getObject(upw.getPageId());
	         

	         //
	         NavigationalStateContext ctx = (NavigationalStateContext)controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

	         // The windows marked dirty during the request
	         Set dirtyWindowIds = new HashSet();

	         // Whether we need a full refresh or not
	         boolean fullRefresh = false;

	         //
	         
	         if (ctx.getChanges() == null)
	         {
	            fullRefresh = true;
	         }
	         else
	         {
	         
	         for (Iterator i = ctx.getChanges(); i.hasNext();)
	         {
	            NavigationalStateChange change = (NavigationalStateChange)i.next();

	            // A change that modifies potentially the page structure
	            if (!(change instanceof NavigationalStateObjectChange))
	            {
	               fullRefresh = true;
	               break;
	            }
	            NavigationalStateObjectChange update = (NavigationalStateObjectChange)change;
	/*
	            // A change that modifies potentially the page structure
	            if (update.getType() != NavigationalStateObjectChange.UPDATE)
	            {
	               fullRefresh = true;
	               break;
	            }
	*/
	            // Get the state key
	            NavigationalStateKey key = update.getKey();

	            // We consider only portal object types
	            Class type = key.getType();
	            if (type == WindowNavigationalState.class)
	            {
	               // Get old window state
	               WindowNavigationalState oldNS = (WindowNavigationalState)update.getOldValue();
	               WindowState oldWindowState = oldNS != null ? oldNS.getWindowState() : null;

	               // Get new window state
	               WindowNavigationalState newNS = (WindowNavigationalState)update.getNewValue();
	               WindowState newWindowState = newNS != null ? newNS.getWindowState() : null;

	               // Check if window state requires a refresh
	               if (WindowState.MAXIMIZED.equals(oldWindowState))
	               {
	                  if (!WindowState.MAXIMIZED.equals(newWindowState))
	                  {
	                     fullRefresh = true;
	                     break;
	                  }
	               }
	               else if (WindowState.MAXIMIZED.equals(newWindowState))
	               {
	                  fullRefresh = true;
	                  break;
	               }

	               // Collect the dirty window id
	               dirtyWindowIds.add(key.getId());
	            }
	            else if (type == PageNavigationalState.class)
	            {
	            	
	            	// Pas de rechargement de la page si les parametres publics sont modifies
	            	
	               // force full refresh for now...
	               //fullRefresh = true;
	               
	            	// A la place, on recharge les windows
	            	reloadAjaxWindows = true;
	            	
	            	//On peut vérifier que les paramètres publics on bien été modifiés
	            	//PageNavigationalState pp = (PageNavigationalState) update.getNewValue();
		            }
	         	}
	         }
	         
	         // TODO : rechargement systématique des windows supportant le mode Ajax
/*	         
	         if( reloadAjaxWindows && !fullRefresh){
	        	 
	        	 Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
	        	 
					for (PortalObject window : windows) {
						
						if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
							if (!dirtyWindowIds.contains(window.getId()))
								dirtyWindowIds.add(window.getId());
						}
					}
 */

			// Le rafraichissment de la page doit etre explicitement demandé par le portlet
	         if( reloadAjaxWindows ){	         
				if ( !"true".equals(controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,
						"pia.refreshPage"))) {

					Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);


					for (PortalObject window : windows) {
					
						if ("true".equals(window.getProperty("theme.dyna.partial_refresh_enabled"))) {
							if (!dirtyWindowIds.contains(window.getId()))
								dirtyWindowIds.add(window.getId());
						}
					}
				} else {
					fullRefresh = true;
				}
	
	         }
	         

	         

	         // Commit changes
	         ctx.applyChanges();

	         //
	         if (!fullRefresh)
	         {
	            ArrayList<PortalObject> refreshedWindows = new ArrayList<PortalObject>();
	            for (PortalObject child : page.getChildren(PortalObject.WINDOW_MASK))
	            {
	               PortalObjectId childId = child.getId();
	               if (dirtyWindowIds.contains(childId))
	               {
	                  refreshedWindows.add(child);
	               }
	            }

	            // Obtain layout
	            LayoutService layoutService = getPageService().getLayoutService();
	            PortalLayout layout = RenderPageCommand.getLayout(layoutService, page);

	            //
	            UpdatePageStateResponse updatePage = new UpdatePageStateResponse(ctx.getViewId());

	            // Call to the theme framework
	            PageResult res = new PageResult(page.getName(), page.getProperties());

	            //
	            ServerInvocation invocation = controllerContext.getServerInvocation();

	            //
	            WindowContextFactory wcf = new WindowContextFactory(controllerContext);

	            //
	            ControllerPortletControllerContext portletControllerContext = new ControllerPortletControllerContext(controllerContext, page);
	            ControllerPageNavigationalState pageNavigationalState = portletControllerContext.getStateControllerContext().createPortletPageNavigationalState(true);

	            //
	            for (Iterator i = refreshedWindows.iterator(); i.hasNext() && !fullRefresh;)
	            {
	               try
	               {
	                  Window refreshedWindow = (Window)i.next();
	                  RenderWindowCommand rwc = new RenderWindowCommand(pageNavigationalState, refreshedWindow.getId());
	                  WindowRendition rendition = rwc.render(controllerContext);

	                  //
	                  if (rendition != null)
	                  {
	                     ControllerResponse resp = rendition.getControllerResponse();

	                     //
	                     if (resp instanceof MarkupResponse)
	                     {
	                        WindowContext wc = wcf.createWindowContext(refreshedWindow, rendition);

//	                     WindowContext wc = new WindowContext(
//	                        _window.getId().toString(PortalObjectPath.LEGACY_BASE64_FORMAT),
//	                        _window.getProperty(ThemeConstants.PORTAL_PROP_REGION),
//	                        "0",
//	                        windowResult);

	                        //
	                        res.addWindowContext(wc);

	                        //
	                        MarkupInfo markupInfo = (MarkupInfo)invocation.getResponse().getContentInfo();

	                        // The buffer
	                        StringWriter buffer = new StringWriter();

	                        // Get a dispatcher
	                        ServletContextDispatcher dispatcher = new ServletContextDispatcher(
	                           invocation.getServerContext().getClientRequest(),
	                           invocation.getServerContext().getClientResponse(),
	                           controllerContext.getServletContainer());

	                        // Not really used for now in that context, so we can pass null (need to change that of course)
	                        ThemeContext themeContext = new ThemeContext(null, null);

	                        // get render context
	                        RendererContext rendererContext = layout.getRenderContext(themeContext, markupInfo, dispatcher, buffer);

	                        // Push page
	                        rendererContext.pushObjectRenderContext(res);

	                        // Push region
	                        Region region = res.getRegion2(wc.getRegionName());
	                        rendererContext.pushObjectRenderContext(region);

	                        // Render
	                        rendererContext.render(wc);

	                        // Pop region
	                        rendererContext.popObjectRenderContext();

	                        // Pop page
	                        rendererContext.popObjectRenderContext();

	                        // Add render to the page
	                        updatePage.addFragment(wc.getId(), buffer.toString());
	                     }
	                     else
	                     {
	                        fullRefresh = true;
	                     }
	                  }
	                  else
	                  {
	                     // We'd better do a full refresh for now
	                     // It could be handled as a portlet removal in the protocol between the client side and server side
	                     fullRefresh = true;
	                  }
	               }
	               catch (Exception e)
	               {
	                  log.error("An error occured during the computation of window markup", e);

	                  //
	                  fullRefresh = true;
	               }
	            }

	            //
	            if (!fullRefresh)
	            {
	               return new AjaxResponse(updatePage);
	            }
	         }

	         // We perform a full refresh
	         ViewPageCommand rpc = new ViewPageCommand(page.getId());
	         String url = controllerContext.renderURL(rpc, null, null);
	         UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
	         return new AjaxResponse(dresp);
	      }
	      else
	      {
	         return null;
	      }
	   }	
	
	 public HandlerResponse processCommandResponse(
		      ControllerContext controllerContext,
		      ControllerCommand commeand,
		      ControllerResponse controllerResponse) throws ResponseHandlerException
		   {

		 
		 HandlerResponse resp = processCommandResponseOrginal(controllerContext, commeand, controllerResponse);

		 
		 if (resp == null)	{
			 
			    if (controllerResponse instanceof RedirectionResponse)	{
			         String url = ((RedirectionResponse) controllerResponse).getLocation();
			         UpdatePageLocationResponse dresp = new UpdatePageLocationResponse(url);
			         return new AjaxResponse(dresp);
			    	
			    }
			    	  
			 
		 }
		 
		 return resp;
		   }

}
