package org.osivia.portal.core.portalobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.AbstractInvocationContext;
import org.jboss.portal.common.invocation.Invocation;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.impl.model.portal.PageImpl;
import org.jboss.portal.core.impl.model.portal.PortalImpl;
import org.jboss.portal.core.impl.model.portal.PortalObjectImpl;
import org.jboss.portal.core.impl.model.portal.WindowImpl;
import org.jboss.portal.core.model.portal.Page;
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
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.DynamicPageBean;
import org.osivia.portal.core.dynamic.DynamicWindowBean;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PermLinkCommand;
import org.osivia.portal.core.page.PortalObjectContainer;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.osivia.portal.core.tracker.ITracker;
import org.osivia.portal.core.tracker.TrackerBean;

/**
 * 
 * Ce module surcharge le container par defaut de JBoss Portal
 * 
 * @author jsteux
 * 
 */

public class DynamicPortalObjectContainer extends ServiceMBeanSupport implements IDynamicObjectContainer, Serializable {
	private Log logger = LogFactory.getLog(DynamicPortalObjectContainer.class);

	private ITracker tracker;

	private static ThreadLocal<DynamicCache> dynamicLocalCache = new ThreadLocal<DynamicCache>();

	public static DynamicCache getDynamicCache() {

		return dynamicLocalCache.get();
	}

	public void startPersistentIteration() {
		getTracker().pushState(new PersistentIteration());
	}

	public void stopPersistentIteration() {
		getTracker().popState();
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

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	public void addDynamicWindow(DynamicWindowBean newWindow) {

		List<DynamicWindowBean> windows = getDynamicWindows();
		List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

		for (DynamicWindowBean window : windows) {
			if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT).equals(newWindow.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT)))
				newWindows.add(window);
		}

		newWindows.add(newWindow);

		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", newWindows);

		// On vide le cache
		getDatas().clear();
	}

	public void addDynamicPage(DynamicPageBean newPage) {

		List<DynamicPageBean> pages = getDynamicPages();
		List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

		int maxOrder = DynamicPageBean.DYNAMIC_PAGES_FIRST_ORDER - 1;

		// Reconstruction du tableau

		for (DynamicPageBean page : pages) {
			if (!page.getPageId().toString(PortalObjectPath.SAFEST_FORMAT).equals(newPage.getPageId().toString(PortalObjectPath.SAFEST_FORMAT))) {
				newPages.add(page);
				if (page.getOrder() > maxOrder)
					maxOrder = page.getOrder();
			}
		}

		// Insertion nouvelle page

		if (newPage.getOrder() == -1)
			newPage.setOrder(maxOrder + 1);
		newPages.add(newPage);

		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", newPages);

		// On vide le cache
		getDatas().clear();
	}

	public void removeDynamicWindow(String dynamicWindowId) {

		List<DynamicWindowBean> windows = getDynamicWindows();
		List<DynamicWindowBean> newWindows = new ArrayList<DynamicWindowBean>();

		for (DynamicWindowBean window : windows) {
			if (!window.getWindowId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId))
				newWindows.add(window);
		}
		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", newWindows);

		// On vide le cache
		getDatas().clear();

	}

	public void removeDynamicPage(String dynamicWindowId) {

		List<DynamicPageBean> pages = getDynamicPages();
		List<DynamicPageBean> newPages = new ArrayList<DynamicPageBean>();

		for (DynamicPageBean page : pages) {
			if (!page.getPageId().toString(PortalObjectPath.SAFEST_FORMAT).equals(dynamicWindowId))
				newPages.add(page);
		}
		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", newPages);

		// On vide le cache
		getDatas().clear();

	}

	public List<DynamicWindowBean> getDynamicWindows() {

		List<DynamicWindowBean> windows = null;

		if (getTracker().getHttpSession() != null)
			windows = (List<DynamicWindowBean>) getTracker().getHttpSession().getAttribute("osivia.dynamic_windows");

		if (windows == null)
			windows = new ArrayList<DynamicWindowBean>();

		/*
		 * 
		 * for( DynamicWindowBean window : windows) { logger.debug("cms.uri" +
		 * window.getProperties().get("osivia.cms.uri")); }
		 */

		return windows;
	}

	public List<DynamicPageBean> getDynamicPages() {

		List<DynamicPageBean> pages = null;

		if (getTracker().getHttpSession() != null)
			pages = (List<DynamicPageBean>) getTracker().getHttpSession().getAttribute("osivia.dynamic_pages");

		if (pages == null)
			pages = new ArrayList<DynamicPageBean>();

		/*
		 * 
		 * for( DynamicWindowBean window : windows) { logger.debug("cms.uri" +
		 * window.getProperties().get("osivia.cms.uri")); }
		 */

		return pages;
	}

	public void setDynamicWindows(List<DynamicWindowBean> dynaWindows) {

		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_windows", dynaWindows);

		// On vide le cache
		getDatas().clear();
	}

	public void setDynamicPages(List<DynamicPageBean> dynaPages) {

		// Copie dans la session
		getTracker().getHttpSession().setAttribute("osivia.dynamic_pages", dynaPages);

		// On vide le cache
		getDatas().clear();
	}

	public PortalObject getObject(PortalObjectContainer container, PortalObjectId id) {

		if (logger.isDebugEnabled()) {
			Object cmd = getTracker().getCurrentState();

			if (cmd != null)

				logger.debug("cmd=" + cmd.getClass().getName().substring(cmd.getClass().getName().lastIndexOf(".") + 1) + " getObject " + id.toString());
		}

		PortalObject po = getObjectInternal(container, id);

		if (logger.isDebugEnabled()) {
			if (po != null)
				logger.debug("    return " + po.getClass().getName().substring(po.getClass().getName().lastIndexOf(".") + 1) + " : " + po.getId().toString());
			else
				logger.debug("    return null");

		}

		return po;

	}

	private PortalObject getParent(PortalObjectContainer container, PortalObjectId childId) {

		PortalObjectPath parentPath = childId.getPath().getParent();
		PortalObjectId parentId = new PortalObjectId("", parentPath);

		// CMS Layout
		if (parentPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {

			PortalObjectId cmsParentId = new PortalObjectId("", parentPath.getParent());

			DynamicPage dynamicPage = CMSTemplatePage.createPage(container, cmsParentId, getCMSTemplate(container, parentPath), this);

			return dynamicPage;

		}

		// Accès direct à une page dynamique stockée dans la session
		for (DynamicPageBean dynamicPageBean : getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(parentId)) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template,
						this, dynamicPageBean, templateId);

				return dynamicPage;
			}
		}

		// Page parent non dynamique
		return container.getObject(parentId);
	}

	private PortalObjectImpl getCMSTemplate(PortalObjectContainer container, PortalObjectPath cmsPagePath) {

		Stack stack = getTracker().getStack();

		Invocation invocation = null;

		List commands = new ArrayList();

		// Inverse order
		for (Object cmd : stack)
			commands.add(0, cmd);

		PageNavigationalState ns = null;

		for (Object cmd : commands) {
			if (cmd instanceof ControllerCommand) {
				invocation = (Invocation) cmd;

				NavigationalStateContext nsContext = (NavigationalStateContext) invocation.getContext().getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
				ns = nsContext.getPageNavigationalState(cmsPagePath.toString());

				break;

			}
			if (cmd instanceof ServerInvocation) {

				PortalObjectNavigationalStateContext pnsCtx = new PortalObjectNavigationalStateContext(((ServerInvocation) cmd).getContext().getAttributeResolver(ControllerCommand.PRINCIPAL_SCOPE));

				ns = pnsCtx.getPageNavigationalState(cmsPagePath.toString());

				break;

			}

		}

		if (ns != null) {
			String layoutPath[] = ns.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.layout_path"));

			if (layoutPath != null) {

				PortalObjectPath layoutObjectPath = PortalObjectPath.parse(layoutPath[0], PortalObjectPath.CANONICAL_FORMAT);
				PortalObjectId layoutId = new PortalObjectId("", layoutObjectPath);
				return (PortalObjectImpl) container.getNonDynamicObject(layoutId);
			}
		}

		return null;
	}

	public PortalObject getObjectInternal(PortalObjectContainer container, PortalObjectId id) {

		Object cmd = getTracker().getCurrentState();

		if (cmd instanceof PersistentIteration)
			return container.getNonDynamicObject(id);

		// Stockage d'un cache dans la requête
		PortalObject cache = getDatas().get(id);

		if (cache != null) {
			if (logger.isDebugEnabled())
				logger.debug("    retrieve cache " + cache.getId());
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
		for (DynamicWindowBean dynamicWindow : getDynamicWindows()) {
			if (dynamicWindow.getWindowId().equals(id)) {

				Page parentPage = (Page) getParent(container, id);

				Window window = (Window) parentPage.getChild(dynamicWindow.getName());
				return window;
			}
		}

		// Accès à une page CMS
		PortalObjectPath objectPath = id.getPath();

		if (objectPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {

			PortalObjectPath parentPath = id.getPath().getParent();
			PortalObjectId parentId = new PortalObjectId("", parentPath);

			PortalObjectImpl template = getCMSTemplate(container, objectPath);

			if (template != null) {

				DynamicPage dynamicPage = CMSTemplatePage.createPage(container, parentId, getCMSTemplate(container, objectPath), this);

				return dynamicPage;
			} else
				return null;
		}

		// Accès à une window CMS
		PortalObjectPath parentPath = id.getPath().getParent();

		if (parentPath != null && parentPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {

			PortalObject templatePage = getCMSTemplate(container, parentPath);

			PortalObjectId parentId = new PortalObjectId("", parentPath.getParent());

			PortalObjectImpl template = getCMSTemplate(container, parentPath);

			if (template != null) {

				CMSTemplatePage dynamicPage = CMSTemplatePage.createPage(container, parentId, getCMSTemplate(container, parentPath), this);

				String windowName = id.getPath().getLastComponentName();
				WindowImpl templateWindow = (WindowImpl) templatePage.getChild(windowName);
				Window window = new DynamicTemplateWindow(dynamicPage, templateWindow, templateWindow.getName(), ((PageImpl) templatePage).getObjectNode().getContext(), this);
				return window;
			} else
				return null;
		}

		for (DynamicPageBean dynamicPageBean : getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(id)) {
				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template,
						this, dynamicPageBean, templateId);

				return dynamicPage;
			}

			// Accès à une window d'une page template
			// Pour l'instant : un template ne peut pas contenir de sous-page,
			// il s'agit donc forcément d'une window
			if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);

				DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(),
						(PageImpl) template, this, dynamicPageBean, templateId);
				String windowName = id.getPath().getLastComponentName();
				WindowImpl templateWindow = (WindowImpl) template.getChild(windowName);
				Window window = new DynamicTemplateWindow(dynamicPage, templateWindow, templateWindow.getName(), ((PageImpl) template).getObjectNode().getContext(), this);
				return window;

			}
		}

		// Accès direct à une page dynamique (stockée dans la session)
		for (DynamicPageBean dynamicPageBean : getDynamicPages()) {

			if (dynamicPageBean.getPageId().equals(id)) {
				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);
				DynamicPage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(), (PageImpl) template,
						this, dynamicPageBean, templateId);

				return dynamicPage;
			}

			// Accès à une window d'une page template
			// Pour l'instant : un template ne peut pas contenir de sous-page,
			// il s'agit donc forcément d'une window
			if (dynamicPageBean.getPageId().getPath().equals(id.getPath().getParent())) {

				PortalObjectId templateId = dynamicPageBean.getTemplateId();
				PortalObject template = container.getNonDynamicObject(templateId);

				DynamicTemplatePage dynamicPage = DynamicTemplatePage.createPage(container, dynamicPageBean.getParentId(), dynamicPageBean.getName(), dynamicPageBean.getDisplayNames(),
						(PageImpl) template, this, dynamicPageBean, templateId);
				String windowName = id.getPath().getLastComponentName();
				WindowImpl templateWindow = (WindowImpl) template.getChild(windowName);
				Window window = new DynamicTemplateWindow(dynamicPage, templateWindow, templateWindow.getName(), ((PageImpl) template).getObjectNode().getContext(), this);
				return window;

			}

		}

		PortalObject object = container.getNonDynamicObject(id);

		boolean dynamicPage = false;
		boolean dynamicPortal = false;

		if ((cmd instanceof PageCommand || cmd instanceof PortalCommand || cmd instanceof DynamicCommand || cmd instanceof PermLinkCommand || cmd instanceof MonEspaceCommand)
				&& object instanceof PageImpl) {
			dynamicPage = true;
		}

		if ((cmd instanceof PageCommand || cmd instanceof PortalCommand || cmd instanceof DynamicCommand || cmd instanceof PermLinkCommand || cmd instanceof MonEspaceCommand)
				&& object instanceof PortalImpl) {
			dynamicPortal = true;
		}

		if (cmd instanceof ServerInvocation && object instanceof PortalImpl) {
			dynamicPortal = true;
		}

		if (cmd instanceof ServerInvocation && object instanceof PageImpl) {
			// Par défaut les requêtes serveurs sont dynamiques
			// (indispensable pour le PortalObjectMapper)
			dynamicPage = true;
		}

		if (dynamicPage) {
			return new DynamicPersistentPage(container, (PageImpl) object, this);
		}

		// A COMMENTER POUR REVENIR AU COMPORTEMENT PRECEDENT

		if (dynamicPortal) {
			return new DynamicPortal(container, (PortalImpl) object, this);
		}

		/*
		 * if( object instanceof PageImpl) { return new DynamicPersistentPage(
		 * (PageImpl) object, this); }
		 * 
		 * if( object instanceof PortalImpl) { return new
		 * DynamicPortal(container, (PortalImpl) object, this); }
		 */

		if (object instanceof WindowImpl) {
			return new DynamicPersistentWindow(container, (WindowImpl) object, this);
		}

		return object;

	}

}
