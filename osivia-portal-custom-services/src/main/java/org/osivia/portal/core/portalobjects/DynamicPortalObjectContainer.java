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
package org.osivia.portal.core.portalobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationContext;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.PortalObjectNavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.Notifications;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cms.CMSEditableWindow;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ChangeContributionModeCommand;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.contribution.PublishContributionCommand;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.PortalObjectContainer;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.RequestContextUtil;

/**
 *
 * Ce module surcharge le container par defaut de JBoss Portal
 *
 * @author jsteux
 *
 */

public class DynamicPortalObjectContainer extends ServiceMBeanSupport implements IDynamicObjectContainer, Serializable {
	private final Log logger = LogFactory.getLog(DynamicPortalObjectContainer.class);

    private INotificationsService notificationService;

    public INotificationsService getNotificationService()   {
        if( this.notificationService == null) {
            this.notificationService = NotificationsUtils.getNotificationsService();
        }
        return this.notificationService;
    }

	private ITracker tracker;
	private ICMSServiceLocator cmsServiceLocator;

	public ICMSServiceLocator getCmsServiceLocator() {
		return this.cmsServiceLocator;
	}

	public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
		this.cmsServiceLocator = cmsServiceLocator;
	}

	private static ThreadLocal<DynamicCache> dynamicLocalCache = new ThreadLocal<DynamicCache>();

	public static DynamicCache getDynamicCache() {

		return dynamicLocalCache.get();
	}

	public void startPersistentIteration() {
		this.getTracker().pushState(new PersistentIteration());
	}

	public void stopPersistentIteration() {
		this.getTracker().popState();
	}

	public static void clearCache() {
		getDatas().clear();
	}

	public static void addToCache(PortalObjectId id, PortalObject value) {
		getDatas().put(id, value);
	}

	private static Map<PortalObjectId, PortalObject> getDatas() {

		DynamicCache dynamicCache = dynamicLocalCache.get();

		if (dynamicCache == null) {
			dynamicCache = new DynamicCache();
			dynamicLocalCache.set(dynamicCache);
		}

		return dynamicCache.getDatas();

	}

	public ICMSService getCMSService() {

		return this.cmsServiceLocator.getCMSService();

	}

	public ITracker getTracker() {
		return this.tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	public void addDynamicWindow(DynamicWindowBean newWindow) {

		List<DynamicWindowBean> windows = this.getDynamicWindows();
		List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

		for (DynamicWindowBean window : windows) {
			if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT)
					.equals(newWindow.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT))) {
                newWindows.add(window);
            }
		}

		newWindows.add(newWindow);

		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", newWindows);

		// On vide le cache
		getDatas().clear();
	}

	public void addDynamicPage(DynamicPageBean newPage) {

		List<DynamicPageBean> pages = this.getDynamicPages();
		List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

		int maxOrder = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;

		// Reconstruction du tableau

		for (DynamicPageBean page : pages) {
            if (!page.getPageBusinessId().toString(PortalObjectPath.SAFEST_FORMAT)
                    .equals(newPage.getPageBusinessId().toString(PortalObjectPath.SAFEST_FORMAT))) {
				newPages.add(page);
				if (page.getOrder() > maxOrder) {
                    maxOrder = page.getOrder();
                }
			}
		}

		// Insertion nouvelle page

		if (newPage.getOrder() == -1) {
            newPage.setOrder(maxOrder + 1);
        }
		newPages.add(newPage);

		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", newPages);

		// On vide le cache
		getDatas().clear();
	}

	public void removeDynamicWindow(String dynamicWindowId) {

		List<DynamicWindowBean> windows = this.getDynamicWindows();
		List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

		for (DynamicWindowBean window : windows) {
			if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId)) {
                newWindows.add(window);
            }
		}
		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", newWindows);

		// On vide le cache
		getDatas().clear();

	}

	public void removeDynamicPage(String dynamicWindowId) {

		List<DynamicPageBean> pages = this.getDynamicPages();
		List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

		for (DynamicPageBean page : pages) {
			if (!page.getPageId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId)) {
                newPages.add(page);
            }
		}
		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", newPages);
		
		
		// Remove child windows
		
        List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

        for (DynamicWindowBean windowBean : this.getDynamicWindows()) {
            if (!windowBean.getPageId().equals(dynamicWindowId)) {
                newWindows.add(windowBean);
            }
        }
        // Copie dans la session
        this.getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", newWindows);	
		

		// On vide le cache
		getDatas().clear();

	}

	public List<DynamicWindowBean> getDynamicWindows() {

		List<DynamicWindowBean> windows = null;

//		if (this.getTracker().getHttpSession() != null) {
//            windows = (List<DynamicWindowBean>) this.getTracker().getHttpSession().getAttribute("osivia.dynamic_windows");
//        }

		if (this.getTracker().getHttpRequest() != null)    {
		   // get refreshed session
	       HttpSession session =  getTracker().getHttpRequest().getSession( true);
	       if( session != null){
	           windows = (List<DynamicWindowBean>) session.getAttribute("osivia.dynamic_windows");
	       }
		}
		

		if (windows == null) {
            windows = new ArrayList<DynamicWindowBean>();
        }

		/*
		 *
		 * for( DynamicWindowBean window : windows) { logger.debug("cms.uri" +
		 * window.getProperties().get("osivia.cms.uri")); }
		 */

		return windows;
	}





	public ServerInvocation getInvocation()	{

		ServerInvocation invocation = RequestContextUtil.getServerInvocation();

		return invocation;
	}



	public ControllerContext getCommandContext()	{
	    ControllerContext controllerContext = RequestContextUtil.getControllerContext();

        return controllerContext;
	}


    public List<DynamicWindowBean> getEditableWindows(PortalObjectContainer container, PortalObjectId pageId) {

		List<DynamicWindowBean> windows = new ArrayList<DynamicWindowBean>();

		/* Appel des windows editable dans le CMS */

		try {

			PortalObjectPath pagePath = pageId.getPath();




			// Hors requete (ex : au deploiement d'une webapp)
			if ((pagePath == null) || (this.getInvocation() == null) ) {
                return windows;
            }

	         // JSS : on peut avoir des EditableWindows sur des pages statiques
            // TODO : a optimiser ( 1 seul appel à cette méthode)


//			if ((pagePath == null) || !pagePath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {
//                return windows;
//            }


			PageNavigationalState ns = null;
			CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
			HttpServletRequest request = null;
			ServerInvocation invocation = null;

			ControllerContext controllerContext = this.getCommandContext();
            InvocationContext ctx = null;
			if( controllerContext != null)	{
                ctx = controllerContext;

				NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(
						ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
				ns = nsContext.getPageNavigationalState(pagePath.toString());

				cmsReadItemContext.setControllerContext(controllerContext);

				request = controllerContext.getServerInvocation().getServerContext().getClientRequest();
				invocation = controllerContext.getServerInvocation();
			}	else	{
				invocation = this.getInvocation();
                ctx = invocation.getServerContext();

				PortalObjectNavigationalStateContext pnsCtx = new PortalObjectNavigationalStateContext(invocation
						.getContext().getAttributeResolver(ControllerCommand.PRINCIPAL_SCOPE));

				ns = pnsCtx.getPageNavigationalState(pagePath.toString());

				cmsReadItemContext.setServerInvocation(invocation);
				request = invocation.getServerContext().getClientRequest();

			}



			if (ns != null) {
                Page page = (Page) this.getObject(container, pageId);
                Portal portal = page.getPortal();
                Page defaultPage = portal.getDefaultPage();
                Boolean isSpaceSite = Boolean.FALSE;

                // CMS path
                String[] cmsPath = ns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
                // CMS base path
                String cmsBasePath = page.getDeclaredProperty("osivia.cms.basePath");
                // Site path
                String sitePath = defaultPage.getDeclaredProperty("osivia.cms.basePath");

                String procedurePath = page.getDeclaredProperty("osivia.procedure.dashboard.path");

                // Publish space path
                String publishSpacePath;
                // Current path
                String path;

                if ((cmsPath == null) && (cmsBasePath == null)) {
                    publishSpacePath = null;
                    path = null;
                } else if (cmsPath != null) {
                    PortalObject parent = page.getParent();
                    publishSpacePath = parent.getDeclaredProperty("osivia.cms.basePath");
                    path = cmsPath[0];
                } else {
                    publishSpacePath = cmsBasePath;
                    path = cmsBasePath;
                }

                // For portal sites, site path is set to publish space path
                if (!PortalObjectUtils.isSpaceSite(portal)) {
                	isSpaceSite = Boolean.FALSE;
                    sitePath = publishSpacePath;
                }
                else {
                	isSpaceSite = Boolean.TRUE;
                }

                // Scope
                String scope = defaultPage.getDeclaredProperty("osivia.cms.navigationScope");


                if (sitePath != null || procedurePath != null) {
                    String windowsEditableWindowsMode = "";

                    cmsReadItemContext.setDisplayLiveVersion("0");

                    // SILENT MODE
                    int notificationSized = -1;
                    if (controllerContext != null) {
                        notificationSized = this.getNotificationService().getNotificationsList(new PortalControllerContext(controllerContext)).size();
                    }

                    // try {
                    
                    if (sitePath != null) {
                        EditionState state = ContributionService.getNavigationalState(controllerContext, ns);
                        if ((state != null) && EditionState.CONTRIBUTION_MODE_EDITION.equals(state.getContributionMode())) {
                            cmsReadItemContext.setForcedLivePath(state.getDocPath());
                            windowsEditableWindowsMode = "preview";
                        } else if ((state == null) || StringUtils.startsWith( state.getDocPath(), sitePath)) {
                            // Web page mode

                            boolean modePreview = CmsPermissionHelper.getCurrentCmsVersion(controllerContext).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW);
                            if (modePreview) {
                                if (CmsPermissionHelper.getCurrentPageSecurityLevel(ctx, path) == Level.allowPreviewVersion) {
                                    cmsReadItemContext.setDisplayLiveVersion("1");
                                    windowsEditableWindowsMode = "preview";
                                }
                            }
                        }
                    }

                    String cachePath = path != null ? path : "";
                    cachePath = procedurePath != null ? "path_".concat(cachePath).concat("procedurePath_").concat(procedurePath) : cachePath;

                    // Pour performances
                    windows = (List<DynamicWindowBean>) invocation.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows."
                            + windowsEditableWindowsMode + "." + cachePath);

                    if (windows != null) {
                        // Réinitialisation par l'utilisateur
                        if (PageProperties.getProperties().isRefreshingPage()) {
                            if (invocation.getAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + windowsEditableWindowsMode + "."
                                    + cachePath + ".resfreshed") == null) {
                                invocation.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + windowsEditableWindowsMode + "."
                                        + cachePath + ".resfreshed", "1");
                                windows = null;
                            }
                        }
                    }


                    if (windows == null) {
                        windows = new ArrayList<DynamicWindowBean>();

                        if (sitePath != null) {
                            // Editable windows
                            List<CMSEditableWindow> editableWindows = this.getCMSService().getEditableWindows(cmsReadItemContext, path, publishSpacePath,
                                    sitePath, scope, isSpaceSite);
                            for (CMSEditableWindow editableWindow : editableWindows) {
                                // Dynamic window beans creation
                                Map<String, String> dynaProps = new HashMap<String, String>();
                                for (String key : editableWindow.getApplicationProperties().keySet()) {
                                    dynaProps.put(key, editableWindow.getApplicationProperties().get(key));
                                }
                                dynaProps.put("osivia.dynamic.unclosable", "1");
                                dynaProps.put("osivia.dynamic.cmsEditable", "1");
                                dynaProps.put("osivia.dynamic.cmsEditable.cmsPath", path);

                                DynamicWindowBean dynaWindow = new DynamicWindowBean(pageId, editableWindow.getName(), editableWindow.getApplicationID(),
                                        dynaProps, null);
                                dynaWindow.setUniqueID(null);

                                windows.add(dynaWindow);
                            }
                        }
                        if (procedurePath != null) {

                            String regionId = page.getDeclaredProperty("osivia.procedure.dashboard.region");

                            List<CMSEditableWindow> procedureDashboards = this.getCMSService().getProcedureDashboards(cmsReadItemContext, procedurePath);
                            for (CMSEditableWindow cmsEditableWindow : procedureDashboards) {

                                Map<String, String> dynaProps = new HashMap<String, String>(cmsEditableWindow.getApplicationProperties().size() + 4);
                                dynaProps.putAll(cmsEditableWindow.getApplicationProperties());
                                dynaProps.put("osivia.dynamic.unclosable", "1");
                                dynaProps.put("osivia.dynamic.cmsEditable", "1");
                                dynaProps.put("osivia.dynamic.cmsEditable.cmsPath", procedurePath);
                                dynaProps.put(ThemeConstants.PORTAL_PROP_REGION, StringUtils.defaultIfBlank(regionId, "col1"));

                                DynamicWindowBean dynaWindow = new DynamicWindowBean(pageId, cmsEditableWindow.getName(), cmsEditableWindow.getApplicationID(),
                                        dynaProps, null);
                                dynaWindow.setUniqueID(null);

                                windows.add(dynaWindow);
                            }

                        }
                        invocation.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + windowsEditableWindowsMode + "." + cachePath,
                                windows);
                    }

                    /*
                     * } catch( Exception e){
                     * // SILENT MODE > Catch Exceptions
                     *
                     * invocation.setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + windowsEditableWindowsMode + "." + cmsPath[0],
                     * windows);
                     *
                     *
                     * // erreur dues à la confusion entre contribution front office et edition mode page
                     * // des contributions front office
                     * // CONSTAT : folder live et non publié sont en erreur car sont considérés comme NOT FOUND
                     * // PAS FORCEMENT GRAVE ....
                     *
                     * }
                     */
                    // SILENT MODE > catch notifications

                    // erreur due également à la confusion entre contribution front office et edition mode page
                    // des contributions front office
                    // (ex: folder live sur PublishInfosCommand est considérée comme inaccessible par CmsPermissionHelper)

                    // TODO : bien distinguer l'edition en mode page et l'edition live

                    if (notificationSized != -1) {
                        List<Notifications> notificationsAfter = this.getNotificationService().getNotificationsList(
                                new PortalControllerContext(controllerContext));
                        if (notificationsAfter.size() > notificationSized) {
                            notificationsAfter.remove(notificationsAfter.size() - 1);
                        }
                    }
                }
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
        }

		return windows;
	}

    private Map<String, String> spaceMenuBarGenericProps = null;
    
    private Map<String, String> getSpaceMenuBarGenericProps()   {
        if( spaceMenuBarGenericProps == null){
            
            spaceMenuBarGenericProps = new HashMap<String, String>();
            spaceMenuBarGenericProps.put(ThemeConstants.PORTAL_PROP_ORDER, "100");
            spaceMenuBarGenericProps.put(ThemeConstants.PORTAL_PROP_REGION, InternalConstants.PORTAL_GENERIC_REGION_NAME);
            spaceMenuBarGenericProps.put("osivia.fragmentTypeId", "space_menubar");                  
        }
        
        return spaceMenuBarGenericProps;
    }
    
    public List<DynamicWindowBean> getPageWindows(PortalObjectContainer container, PortalObjectId pageId, boolean includeCMSWindows) {

		List<DynamicWindowBean> windows = new ArrayList<DynamicWindowBean>();

		for (DynamicWindowBean windowBean : this.getDynamicWindows()) {
			if (windowBean.getPageId().equals(pageId)) {
				windows.add(windowBean);
			}
		}

        windows.add(new DynamicWindowBean(pageId, InternalConstants.PORTAL_MENUBAR_WINDOW_NAME, "toutatice-portail-cms-nuxeo-viewFragmentPortletInstance", getSpaceMenuBarGenericProps(), null));
	

        if (includeCMSWindows) {
            for (DynamicWindowBean windowBean : this.getEditableWindows(container, pageId)) {
                windows.add(windowBean);
            }
		}

		return windows;
	}

	public List<DynamicPageBean> getDynamicPages() {

		List<DynamicPageBean> pages = null;

//		if (this.getTracker().getHttpSession() != null) {
//            pages = (List<DynamicPageBean>) this.getTracker().getHttpSession().getAttribute("osivia.dynamic_pages");
//        }
		
	      if (this.getTracker().getHttpRequest() != null)    {
	           // get refreshed session
	           HttpSession session =  getTracker().getHttpRequest().getSession( true);
	           if( session != null){
	               pages = (List<DynamicPageBean>) session.getAttribute("osivia.dynamic_pages");
	           }
	        }

		
		

		if (pages == null) {
            pages = new ArrayList<DynamicPageBean>();
        }

		/*
		 *
		 * for( DynamicWindowBean window : windows) { logger.debug("cms.uri" +
		 * window.getProperties().get("osivia.cms.uri")); }
		 */

		return pages;
	}

	public void setDynamicWindows(List<DynamicWindowBean> dynaWindows) {

		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", dynaWindows);

		// On vide le cache
		getDatas().clear();
	}

	public void setDynamicPages(List<DynamicPageBean> dynaPages) {

		// Copie dans la session
		this.getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", dynaPages);

		// On vide le cache
		getDatas().clear();
	}

	public PortalObject getObject(PortalObjectContainer container, PortalObjectId id) {

		if (this.logger.isDebugEnabled()) {
			Object cmd = this.getTracker().getCurrentState();

			if (cmd != null) {
                this.logger.debug("cmd=" + cmd.getClass().getName().substring(cmd.getClass().getName().lastIndexOf(".") + 1) + " getObject "
						+ id.toString());
            }
		}

		PortalObject po = this.getObjectInternal(container, id);

		if (this.logger.isDebugEnabled()) {
			if (po != null) {
                this.logger.debug("    return " + po.getClass().getName().substring(po.getClass().getName().lastIndexOf(".") + 1) + " : "
						+ po.getId().toString());
            } else {
                this.logger.debug("    return null");
            }

		}

		return po;

	}

	private PortalObject getParent(PortalObjectContainer container, PortalObjectId childId) {

		PortalObjectPath parentPath = childId.getPath().getParent();
		PortalObjectId parentId = new PortalObjectId("", parentPath);

		// CMS Layout
		if (parentPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {
			DynamicPage dynamicPage = CMSTemplatePageFactory.getCMSPage(this, container,  parentPath);

			return dynamicPage;

		}

		// Accès direct à une page dynamique stockée dans la session
		for (DynamicPageBean dynamicPageBean : this.getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(parentId)) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(),
						dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template, null,this, dynamicPageBean,
						templateId);

				return dynamicPage;
			}
		}

		PortalObject po = container.getNonDynamicObject(parentId);

		if( po instanceof PageImpl) {
            return new DynamicPersistentPage(container, (PageImpl) po, this);
        }

		// Page parent non dynamique
		return container.getObject(parentId);
	}



	public PortalObject getObjectInternal(PortalObjectContainer container, PortalObjectId id) {

		Object cmd = this.getTracker().getCurrentState();

		if (cmd instanceof PersistentIteration) {
            return container.getNonDynamicObject(id);
        }

		// Stockage d'un cache dans la requête
		PortalObject cache = getDatas().get(id);

		if (cache != null) {
			if (this.logger.isDebugEnabled()) {
                this.logger.debug("    retrieve cache " + cache.getId());
            }
			/*
			 * if( cache instanceof Window){ Window window = (Window) cache; if(
			 * window.getDeclaredProperty("osivia.cms.uri") != null) {
			 * logger.debug("cache osivia.cms.uri "+
			 * window.getDeclaredProperty("osivia.cms.uri")); }
			 *
			 * }
			 */
			return cache;
		}

		// test perfs
		/*
		 * if( true) { logger.warn("NO DYNAMIC WINDOWS"); return
		 * container.getNonDynamicObject( id);
		 *
		 *
		 * }
		 */

		// Accès direct à une window dynamique (stockée dans la session)
		PortalObjectPath pagePath = id.getPath().getParent();


		if( pagePath != null)	{

		PortalObjectId pageId = new PortalObjectId("", pagePath);



            for (DynamicWindowBean dynamicWindow : this.getPageWindows(container, pageId, false)) {
			if (dynamicWindow.getWindowId().equals(id)) {

				Page parentPage = (Page) this.getParent(container, id);

				Window window = (Window) parentPage.getChild(dynamicWindow.getName());
				return window;
			}
		}

		// Accès à une page CMS
		PortalObjectPath objectPath = id.getPath();

		if (objectPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {


			DynamicPage dynamicPage =  CMSTemplatePageFactory.getCMSPage(this, container, objectPath);

			return dynamicPage;
		}

		// Accès à une window CMS
		PortalObjectPath parentPath = id.getPath().getParent();

		if ((parentPath != null) && parentPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {

		    // TODO accès auc cache ???
		    CMSTemplatePage page = CMSTemplatePageFactory.getCMSPage(this, container,  parentPath);

			if (page != null) {

				String windowName = id.getPath().getLastComponentName();

				return page.getChild(windowName);
				/*
				WindowImpl templateWindow = (WindowImpl) page.getTemplate().getChild(windowName);
				try {
				Window window = new DynamicTemplateWindow(page, templateWindow, templateWindow.getName(), ((PageImpl) page.getTemplate())
						.getObjectNode().getContext(), this);

				return window;
				} catch( Exception e)   {
				    e.printStackTrace();
				    return null;
				}
				*/
			} else {
                return null;
            }
		}

		for (DynamicPageBean dynamicPageBean : this.getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(id)) {
				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(),
						dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template, null, this, dynamicPageBean,
						templateId);

				return dynamicPage;
			}

			// Accès à une window d'une page template
			// Pour l'instant : un template ne peut pas contenir de sous-page,
			// il s'agit donc forcément d'une window
			if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);

				DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(),
						dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template, null,this, dynamicPageBean,
						templateId);
				String windowName = id.getPath().getLastComponentName();

				return dynamicPage.getChild(windowName);
				/*
				WindowImpl templateWindow = (WindowImpl) template.getChild(windowName);
				Window window = new DynamicTemplateWindow(dynamicPage, templateWindow, templateWindow.getName(), ((PageImpl) template)
						.getObjectNode().getContext(), this);
				return window;
				*/

			}
		}

		// Accès direct à une page dynamique (stockée dans la session)
		for (DynamicPageBean dynamicPageBean : this.getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(id)) {
				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(),
						dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template, null, this, dynamicPageBean,
						templateId);

				return dynamicPage;
			}

			// Accès à une window d'une page template
			// Pour l'instant : un template ne peut pas contenir de sous-page,
			// il s'agit donc forcément d'une window
			if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);

				DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(),
						dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template, null, this, dynamicPageBean,
						templateId);
				String windowName = id.getPath().getLastComponentName();

                return dynamicPage.getChild(windowName);
                /*
				WindowImpl templateWindow = (WindowImpl) template.getChild(windowName);
				Window window = new DynamicTemplateWindow(dynamicPage, templateWindow, templateWindow.getName(), ((PageImpl) template)
						.getObjectNode().getContext(), this);
				return window;
				*/

			}

		}
	}


        PortalObject object = container.getNonDynamicObject(id);
        // if (object == null) {
        // PortalObjectPath parentPath = id.getPath().getParent();
        // if (parentPath != null) {
        // PortalObjectId parentId = new PortalObjectId(StringUtils.EMPTY, parentPath);
        // PortalObject parent = container.getNonDynamicObject(parentId);
        // if (parent instanceof PageImpl) {
        // PageImpl parentPageImpl = (PageImpl) parent;
        // DynamicPersistentPage dynamicPersistentPage = new DynamicPersistentPage(container, parentPageImpl, this);
        //
        // object = dynamicPersistentPage.getChild(id.getPath().getLastComponentName());
        // }
        // }
        // }

		boolean dynamicPage = false;
		boolean dynamicPortal = false;
		boolean dynamicContext = false;

		if (((cmd instanceof PageCommand) || (cmd instanceof PortalCommand) || (cmd instanceof DynamicCommand) || (cmd instanceof PermLinkCommand) || (cmd instanceof MonEspaceCommand) || (cmd instanceof ChangeContributionModeCommand) || (cmd instanceof PublishContributionCommand))
				&& (object instanceof PageImpl)) {
			dynamicPage = true;
		}

		if (((cmd instanceof PageCommand) || (cmd instanceof PortalCommand) || (cmd instanceof DynamicCommand) || (cmd instanceof PermLinkCommand) || (cmd instanceof MonEspaceCommand) || (cmd instanceof ChangeContributionModeCommand) || (cmd instanceof PublishContributionCommand))
				&& (object instanceof PortalImpl)) {
			dynamicPortal = true;
		}

		if (((cmd instanceof PageCommand) || (cmd instanceof PortalCommand) || (cmd instanceof DynamicCommand) || (cmd instanceof PermLinkCommand) || (cmd instanceof MonEspaceCommand) || (cmd instanceof AssistantCommand))
				&& (object instanceof ContextImpl)) {
			dynamicContext = true;
		}

		if ((cmd instanceof ServerInvocation) && (object instanceof PortalImpl)) {
			dynamicPortal = true;
		}

		if ((cmd instanceof ServerInvocation) && (object instanceof PageImpl)) {
			// Par défaut les requêtes serveurs sont dynamiques
			// (indispensable pour le PortalObjectMapper)
			dynamicPage = true;
		}

		if ((cmd instanceof ServerInvocation) && (object instanceof ContextImpl)) {
			// Par défaut les requêtes serveurs sont dynamiques
			// (indispensable pour le PortalObjectMapper)
			dynamicContext = true;
		}

		if (dynamicPage) {
			return new DynamicPersistentPage(container, (PageImpl) object, this);
		}

		// A COMMENTER POUR REVENIR AU COMPORTEMENT PRECEDENT

		if (dynamicPortal) {
			return new DynamicPortal(container, (PortalImpl) object, this);
		}

		if (dynamicContext) {
			return new DynamicContext(container, (ContextImpl) object, this);
		}


		if (object instanceof WindowImpl) {
			return new DynamicPersistentWindow(container, (WindowImpl) object, this);
		}

		return object;

	}

}
