package org.osivia.portal.core.cms;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class DisplayContentCommand extends DynamicCommand  {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	public CommandInfo getInfo() {
		return info;
	}

	private String pageId;
	
	private String uri;

	private String instanceId;
	
	private Map<String, String> props;
	private Map<String, String> params;	

	public String getPageId() {
		return pageId;
	}

	public DisplayContentCommand() {
	}

	public DisplayContentCommand(String pageId,  String portletInstance, String uri, Map<String, String> props, Map<String, String> params) {
		
		this.pageId = pageId;
		this.instanceId = portletInstance;
		this.uri = uri;
		this.props = props;
		this.params = params;		
	}

	public ControllerResponse execute() throws ControllerException {

		try {

			// Récupération page
			PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
			Page page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

			
			
		/* On force toutes les windows en mode NORMAL */
			
			for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK))	{
				Window  child = (Window) po;
				NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, child.getId());
				WindowNavigationalState windowNavState = WindowNavigationalState.create();

				// On force la maximisation
				WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL,
						windowNavState.getMode(), windowNavState.getContentState());
				
	
				getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
				}
			
			
			
			IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
					"pia:service=DynamicPortalObjectContainer");
			
			

			Map<String, String> properties = new HashMap<String, String>();
			
			//Set default properties
			properties.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
			properties.put(ThemeConstants.PORTAL_PROP_REGION, "virtual");
			
			properties.put("pia.cms.uri", uri);
			properties.put("pia.hideDecorators", "1");
			
			logger.debug("pia.cms.uri="+uri);
			
			//Set specific properties	
			
			for( String name : props.keySet()){
				properties.put(name, props.get(name));
								
			}
	
			String windowName = "cms";
			
			
			String controlledPageMarker = (String) getControllerContext().getAttribute(Scope.REQUEST_SCOPE,
					"controlledPageMarker");
						
			
			dynamicCOntainer.addDynamicWindow(new DynamicWindowBean(page, "cms", instanceId, properties, controlledPageMarker));

			PortalObjectId vindowId = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString()
					.concat("/").concat(windowName), PortalObjectPath.CANONICAL_FORMAT));

			
			
			
			// Pour forcer le rechargement de la page, on supprime l'ancien
			// windowState
			// pour etre sur qu'elle est considérée comme modifiée
			getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, vindowId.toString());

			/*
			NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, vindowId);
			WindowNavigationalState windowNavState = WindowNavigationalState.create();
			
			// Réinitialisation état pour forcer rechargement
			// (réinitalisation du cache)
			ParametersStateString state = ParametersStateString.create();
			state.setValue("timestamp", ""+ System.currentTimeMillis());

			
			// On force la maximisation
			WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED,
					windowNavState.getMode(), state);
			*/
			
			NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, vindowId);
			WindowNavigationalState windowNavState = WindowNavigationalState.create();
			
			Map<String, String[]> parameters = new HashMap<String, String[]>();
			for ( String keyParam: params.keySet())
				parameters.put(keyParam, new String[] {params.get(keyParam)});

			
			
			// On force la maximisation
			WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.MAXIMIZED,
					windowNavState.getMode(), ParametersStateString.create(parameters));


			getControllerContext().setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
			
			// Suppression du cache
			getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, "cached_markup." +vindowId.toString());



			return new UpdatePageResponse(page.getId());

		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

}
