package org.osivia.portal.core.dynamic;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.HashMap;
import java.util.Map;

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
import org.osivia.portal.api.charte.Breadcrumb;
import org.osivia.portal.api.charte.BreadcrumbItem;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.WindowStateMarkerInfo;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StartDynamicPageCommand extends DynamicCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

	public CommandInfo getInfo() {
		return info;
	}
	String parentId ;
	String templateId ;
	String pageName;
	Map displayNames;
	Map<String, String> props;
	Map<String, String> params;
	


	public StartDynamicPageCommand() {
	}

	public StartDynamicPageCommand(String parentId,  String pageName,  Map displayNames, String templateId,
			Map<String, String> props, Map<String, String> params) {
		this.parentId = parentId;
		this.pageName = pageName;
		this.templateId = templateId;

		this.displayNames = displayNames;
		this.props = props;
		this.params = params;
	}

	public ControllerResponse execute() throws ControllerException {

		try {
			
			// Récupération page
			PortalObjectId poid = PortalObjectId.parse(parentId, PortalObjectPath.SAFEST_FORMAT);
			PortalObject parent = (PortalObject) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

			PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(parent.getId().getPath().toString()
					.concat("/").concat(pageName), PortalObjectPath.CANONICAL_FORMAT));

			
			
			PortalObjectId potemplateid = PortalObjectId.parse(templateId, PortalObjectPath.SAFEST_FORMAT);
			
			IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
			"osivia:service=DynamicPortalObjectContainer");
			
			
			
			
			
			Map<String, String> properties = new HashMap<String, String>();

			for (String dynaKey : props.keySet()) {
				properties.put(dynaKey, props.get(dynaKey));
				
			}


			// Mémorisation de la page avant l'appel
			PageMarkerInfo markerInfo = PageMarkerUtils.getLastPageState( getControllerContext());
			
			if( markerInfo != null)	{
       		 
		       	properties.put("osivia.dynamic.close_page_path",markerInfo.getPageId().toString());
			}
			
				
			
			DynamicPageBean pageBean = new DynamicPageBean(parent, pageName, displayNames, potemplateid, properties);
			
			
			
			
			
			
			
			dynamicCOntainer.addDynamicPage(pageBean);
			
			PortalObject page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(pageId);	

			
			
			// Maj des paramètres publics de la page
	         NavigationalStateContext nsContext = (NavigationalStateContext)context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
 	         Map<QName, String[]> state = new HashMap<QName, String[]>();
	         for (Map.Entry<String, String> entry : params.entrySet())
	         {
	            state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[] {entry.getValue()});
	         }
	         nsContext.setPageNavigationalState(pageId.toString(), new PageNavigationalState(state));
	         
	         /* Suppression des anciens renders params */
	                  
				for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
					getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, po.getId().toString());
				}
	
				// Maj du breadcrumb
				getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, 	"breadcrumb", null);
			
			//Impact sur les caches du bandeau
			ICacheService cacheService =  Locator.findMBean(ICacheService.class,"osivia:service=Cache");
			cacheService.incrementHeaderCount();
			
			
			return new UpdatePageResponse(pageId);



		} catch (Exception e) {
			if( ! (e instanceof ControllerException))
				throw new ControllerException(e);
			else
				throw ((ControllerException) e);
		}

	}

}
