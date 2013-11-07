package org.osivia.portal.core.pagemarker;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.DefaultPortalCommandFactory;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


/**
 * 
 * ajout d'un tag /pagemarker dans l'url pour associer à chaque page l'état des
 * portlets
 * 
 * @author jeanseb
 * 
 */
public class PortalCommandFactory extends DefaultPortalCommandFactory {

	protected static final Log logger = LogFactory.getLog(PortalCommandFactory.class);

	public IDynamicObjectContainer dynamicCOntainer;
	public PortalObjectContainer portalObjectContainer;

	public IDynamicObjectContainer getDynamicContainer() {

		if (dynamicCOntainer == null)
			dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
					"osivia:service=DynamicPortalObjectContainer");

		return dynamicCOntainer;
	}

	public PortalObjectContainer getPortalObjectContainer() {

		if (portalObjectContainer == null)
			portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");

		return portalObjectContainer;
	}

	private void createPages(List<CMSPage> preloadedPages) {

		int order = DynamicPageBean.DYNAMIC_PRELOADEDPAGES_FIRST_ORDER;

		for (CMSPage page : preloadedPages) {

			CMSItem publishSpace = page.getPublishSpace();

			PortalObject parent;
			
			try	{
		

			String parentPath = page.getParentPath();
			if (parentPath != null) {

				PortalObjectId poid = PortalObjectId.parse(parentPath, PortalObjectPath.CANONICAL_FORMAT);
				parent = getPortalObjectContainer().getObject(poid);
			} else
				parent = getPortalObjectContainer().getContext().getDefaultPortal();

			Map displayNames = new HashMap();
			displayNames.put(Locale.FRENCH, publishSpace.getProperties().get("displayName"));

			Map<String, String> props = new HashMap<String, String>();

			String pageName = "portalSite"
					+ (new CMSObjectPath(publishSpace.getPath(), CMSObjectPath.CANONICAL_FORMAT))
							.toString(CMSObjectPath.SAFEST_FORMAT);

			props.put("osivia.cms.basePath", publishSpace.getPath());

			// v2.0-rc7
			
//			if ("1".equals(publishSpace.getProperties().get("contextualizeInternalContents")))
//				props.put("osivia.cms.pageContextualizationSupport", "1");
//
//			if ("1".equals(publishSpace.getProperties().get("contextualizeExternalContents")))
//				props.put("osivia.cms.outgoingRecontextualizationSupport", "1");

			props.put("osivia.cms.layoutType", "1");
			props.put("osivia.cms.layoutRules", "return ECMPageTemplate;");

			DynamicPageBean dynaPage = new DynamicPageBean(parent, pageName, displayNames, PortalObjectId.parse(
					"/default/templates/publish", PortalObjectPath.CANONICAL_FORMAT), props);
			dynaPage.setOrder(order);
			
			dynaPage.setClosable(false);

			getDynamicContainer().addDynamicPage(dynaPage);
			
			
			} catch( Exception e){
				
				String cmsDebugPath = "";
				
				if( page.getPublishSpace() != null)	{
					cmsDebugPath = page.getPublishSpace().getPath(); 
				}
					
				
				// Don't block login
				log.error("Can't preload user cms page " + cmsDebugPath);
			}
			

			order++;
		}

	}

	public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host,
			String contextPath, String requestPath) {

		String newPath = PageMarkerUtils.restorePageState(controllerContext, requestPath);

		/*
		 * Synchronisation des pages préchargées
		 * 
		 * A faire après le restorePageState
		 */

		List<CMSPage> preloadedPages = (List<CMSPage>) invocation.getAttribute(Scope.REQUEST_SCOPE,
				"osivia.userPreloadedPages");

		if (preloadedPages != null) {
			createPages(preloadedPages);
		}

		return super.doMapping(controllerContext, invocation, host, contextPath, newPath);
	}

}
