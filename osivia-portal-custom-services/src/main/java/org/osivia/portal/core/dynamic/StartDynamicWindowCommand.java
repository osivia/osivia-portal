package org.osivia.portal.core.dynamic;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.PortletWindowActionResponse;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.WindowStateMarkerInfo;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StartDynamicWindowCommand extends DynamicCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	public CommandInfo getInfo() {
		return info;
	}

	private String pageId;
	private String regionId;
	private String instanceId;
	private String windowName;
	private Map<String, String> dynaProps;
	private Map<String, String> params;
	private String addTobreadcrumb;

	public String getPageId() {
		return pageId;
	}

	public StartDynamicWindowCommand() {
	}

	public StartDynamicWindowCommand(String pageId, String regionId, String portletInstance, String windowName,
			Map<String, String> props, Map<String, String> params, String addTobreadcrumb) {
		this.pageId = pageId;
		this.regionId = regionId;
		this.instanceId = portletInstance;
		this.windowName = windowName;
		this.dynaProps = props;
		this.params = params;
		this.addTobreadcrumb = addTobreadcrumb;
	}

	public ControllerResponse execute() throws ControllerException {

		try {

			// Récupération page
			PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
			Page page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(poid);
			
			
			if( page == null){
				// La page dynamique n'existe plus 				
				// Redirection vers la page par défaut du portail
				 Portal portal = (Portal)getControllerContext().getController().getPortalObjectContainer().getContext().getDefaultPortal();
				 return new UpdatePageResponse(portal.getDefaultPage().getId());
			}

			IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
					"osivia:service=DynamicPortalObjectContainer");
			
			
			
			
			
			

			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
			properties.put(ThemeConstants.PORTAL_PROP_REGION, regionId);

			for (String dynaKey : dynaProps.keySet()) {
				properties.put(dynaKey, dynaProps.get(dynaKey));
				
			}
			
			properties.put("osisia.dynamicStarted", "1");
			
			// close url : lien vers la page dans son état avant le lancement
			
	
			PageMarkerInfo markerInfo = PageMarkerUtils.getLastPageState( getControllerContext());
			
			if( markerInfo != null)	{
			
				
				String backUrl = null;
				
				if ( "1".equals(addTobreadcrumb)) 	{
					
					ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());
					
					PortalURL url = new PortalURLImpl(pageCmd,getControllerContext(), null, null);
					
					backUrl = url.toString();					
					
				}	else	{
									
					
					/* On détermine si on est en mode contextualisé */
	
					/*	
					String cmsNav[] = null;
					
					NavigationalStateContext nsContext = (NavigationalStateContext) context
					.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
									
					PageNavigationalState pns = nsContext.getPageNavigationalState( page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
					
					if( pns != null )	{
						cmsNav = pns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path") );
					}
					
					
				
					if( cmsNav != null && cmsNav.length > 0)	{
						// Si contenu contextualisé, renvoi sur le cms
						// Pour réinitialiser la page
						
						Map<String, String> pageParams = new HashMap<String, String>();
						
						IPortalUrlFactory urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
						
						backUrl = urlFactory.getCMSUrl(new PortalControllerContext(getControllerContext()), page.getId()
								.toString(PortalObjectPath.CANONICAL_FORMAT), cmsNav[0],  pageParams, IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null,  null, null,null, null);

					}
					else	{
*/					
						
						// Mode non contextualisé
						
						// Use case : menu > maximized puis a nouvean menu > maximized 
						//            le close revient sur l'accueil
						ViewPageCommand pageCmd = new ViewPageCommand(markerInfo.getPageId());
						
						PortalURL url = new PortalURLImpl(pageCmd,getControllerContext(), null, null);
						
						backUrl = url.toString();
						backUrl +=  "?unsetMaxMode=true";
//					}
				}
				
	       		 if( backUrl.indexOf("/pagemarker/") != -1)	{
  	        			 String pageMarker = markerInfo.getPageMarker();
	        			 backUrl =  backUrl.replaceAll("/pagemarker/([0-9]*)/","/pagemarker/"+pageMarker+"/");
	        		 }
	       		properties.put("osivia.dynamic.close_url",backUrl);
			}
			
				
			



			InstanceDefinition instance = getControllerContext().getController().getInstanceContainer()
					.getDefinition(instanceId);
			if (instance == null)
				throw new ControllerException("Instance not defined");

			/*
			 * /* On force toutes les windows en mode NORMAL
			 */

			/*
			 * for (PortalObject po :
			 * page.getChildren(PortalObject.WINDOW_MASK)) { Window child =
			 * (Window) po; NavigationalStateKey nsKey = new
			 * NavigationalStateKey(WindowNavigationalState.class,
			 * child.getId()); WindowNavigationalState windowNavState =
			 * WindowNavigationalState.create();
			 * 
			 * // On force la mise en mode normal WindowNavigationalState newNS
			 * = WindowNavigationalState.bilto(windowNavState,
			 * WindowState.NORMAL, windowNavState.getMode(),
			 * windowNavState.getContentState());
			 * 
			 * 
			 * getControllerContext().setAttribute(ControllerCommand.
			 * NAVIGATIONAL_STATE_SCOPE, nsKey, newNS); }
			 */
			
		

			for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
				Window child = (Window) po;
				NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, child.getId());

				WindowNavigationalState ws = (WindowNavigationalState) getControllerContext().getAttribute(
						ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);

				if (ws != null) {
					WindowNavigationalState newNS = new WindowNavigationalState(WindowState.NORMAL, ws.getMode(),
							ws.getContentState(), ws.getPublicContentState());
					getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
				}

			}
			
			
			
			String controlledPageMarker = (String) getControllerContext().getAttribute(Scope.REQUEST_SCOPE,
					"controlledPageMarker");
			
			
			
			

			PortalObjectId vindowId = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString()
					.concat("/").concat(windowName), PortalObjectPath.CANONICAL_FORMAT));
			
			
			
			/* Création de la nouvelle fenêtre */
			dynamicCOntainer.addDynamicWindow(new DynamicWindowBean(page.getId(), windowName, instanceId, properties, controlledPageMarker));
			
			
			
			
			// TODO : SESSIONDYNA A reactiver pour synchroniser les sessions
			//getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.session.refresh."+vindowId, "1");


			// Pour forcer le rechargement de la page, on supprime l'ancien
			// windowState
			// pour etre sur qu'elle est considérée comme modifiée
			getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, vindowId.toString());

			NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, vindowId);
			WindowNavigationalState windowNavState = WindowNavigationalState.create();

			Map<String, String[]> parameters = new HashMap<String, String[]>();
			for (String keyParam : params.keySet())
				parameters.put(keyParam, new String[] { params.get(keyParam) });
			

			// On force la maximisation
			WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED,
					windowNavState.getMode(), ParametersStateString.create(parameters));

			getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);

			// Mise à jour des public parameters
			/*
			 * AttributeResolver resolver =
			 * getControllerContext().getAttributeResolver
			 * (NAVIGATIONAL_STATE_SCOPE); ParametersStateString params =
			 * ParametersStateString.create(); newNS.setPublicState(resolver,
			 * nsKey, params);
			 */

			// Suppression du cache
			getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE,
					"cached_markup." + vindowId.toString());

			
			// Maj du breadcrumb
			Breadcrumb breadcrumb = (Breadcrumb) getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
					"breadcrumb");

			if (breadcrumb == null)	{
				breadcrumb = new Breadcrumb();
			}

			if (!"1".equals(addTobreadcrumb)) {
				breadcrumb.getChilds().clear();
			}

			/* ajout du nouvel item */

			PageURL url = new PageURL(page.getId(), getControllerContext());

			String name = properties.get("osivia.title");
			if (name == null)
				name = instance.getDisplayName().getDefaultString();
			BreadcrumbItem item = new BreadcrumbItem(name, url.toString(), vindowId, false);
			
			if( "navigationPlayer".equals(addTobreadcrumb))	{
				item.setNavigationPlayer(true);
			}

			breadcrumb.getChilds().add(item);

			getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumb);

			return new UpdatePageResponse(page.getId());

		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

}
