package org.osivia.portal.core.theming.attributesbundle;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.page.WindowContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.path.PortletPathItem;
import org.osivia.portal.api.theming.Breadcrumb;
import org.osivia.portal.api.theming.BreadcrumbItem;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Breadcrumb attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class BreadcrumbAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static BreadcrumbAttributesBundle instance;

    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Portal object container. */
    private final PortalObjectContainer portalObjectContainer;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private BreadcrumbAttributesBundle() {
        super();

        // URL Factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        // Portal object container
        this.portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_BREADCRUMB);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static BreadcrumbAttributesBundle getInstance() {
        if (instance == null) {
            instance = new BreadcrumbAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Breadcrumb
        Breadcrumb breadcrumb = this.computeBreadcrumb(renderPageCommand, pageRendition);
        attributes.put(Constants.ATTR_BREADCRUMB, breadcrumb);
    }


    /**
     * Utility method used to compute breadcrumb value.
     *
     * @param renderPageCommand render page command
     * @param pageRendition page rendition
     * @return breadcrumb
     * @throws ControllerException
     */
    @SuppressWarnings("unchecked")
    private Breadcrumb computeBreadcrumb(RenderPageCommand renderPageCommand, PageRendition pageRendition) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Window context map
        Map<?, ?> windowContextMap = pageRendition.getPageResult().getWindowContextMap();
        // Current locale
        Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();
        // Edition mode
        String mode = (String) controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
        // Current page
        Page page = renderPageCommand.getPage();
        // Current page state
        PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());
        // Current CMS base path
        String basePath = page.getProperty("osivia.cms.basePath");
        // Current publication path
        String publicationPath = this.getPublicationPath(pageState, page);

        // Publication indicator
        boolean publication = ((basePath != null) && StringUtils.startsWith(publicationPath, basePath));

        // Breadcrumb memo
        Breadcrumb breadcrumbMemo = (Breadcrumb) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb");
        if (breadcrumbMemo == null) {
            breadcrumbMemo = new Breadcrumb();
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "breadcrumb", breadcrumbMemo);
        }

        // Breadcrum initialization
        Breadcrumb breadcrumb = new Breadcrumb();

        // Loop on pages
        do {
            boolean displayPage = true;

            // No display for CMS templated page in wizard mode
            if (InternalConstants.VALUE_WINDOWS_WIZARD_TEMPLATE_MODE.equals(mode) && (page instanceof CMSTemplatePage)) {
                displayPage = false;
            }

            if (publication && ((basePath != null) || "1".equals(page.getProperty("osivia.cms.directContentPublisher")))) {
                displayPage = false;
            }

            if (displayPage) {
                ViewPageCommand viewPageCommand = new ViewPageCommand(page.getId());
                String pageName = PortalObjectUtils.getDisplayName(page, locale);

                String url = new PortalURLImpl(viewPageCommand, controllerContext, null, null).toString() + "?init-state=true";
                BreadcrumbItem item = new BreadcrumbItem(pageName, url.toString(), page.getId(), false);
                breadcrumb.getChilds().add(0, item);
            }

            // Continue loop on parent page
            PortalObject parent = page.getParent();
            if (parent instanceof Page) {
                page = (Page) parent;
            } else {
                page = null;
            }
        } while (page != null);


        // Add CMS path
        page = renderPageCommand.getPage();
        if (!"1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {
            // Get publication header
            PortalObject portalObject = page;
            while ((portalObject instanceof Page) && (portalObject.getDeclaredProperty("osivia.cms.basePath") == null)) {
                portalObject = portalObject.getParent();
            }

            if (publication) {
                String navigationScope = page.getProperty("osivia.cms.navigationScope");

                CMSServiceCtx cmxCtx = new CMSServiceCtx();
                cmxCtx.setControllerContext(controllerContext);
                cmxCtx.setScope(navigationScope);

                // Check CMS preview mode
                if (CmsPermissionHelper.CMS_VERSION_PREVIEW.equals(controllerContext.getAttribute(ControllerCommand.SESSION_SCOPE,
                        CmsPermissionHelper.ATTR_TOOLBAR_CMS_VERSION))) {
                    cmxCtx.setDisplayLiveVersion("1");
                }

                while (StringUtils.contains(publicationPath, basePath)) {
                    Map<String, String> pageParams = new HashMap<String, String>();

                    String url = this.urlFactory.getCMSUrl(new PortalControllerContext(controllerContext),
                            portalObject.getId().toString(PortalObjectPath.CANONICAL_FORMAT), publicationPath, pageParams,
                            IPortalUrlFactory.CONTEXTUALIZATION_PAGE, null, null, null, null, null);

                    try {
                        CMSItem cmsItem = this.cmsServiceLocator.getCMSService().getPortalNavigationItem(cmxCtx, basePath, publicationPath);

                        BreadcrumbItem breadcrumbItem = new BreadcrumbItem(cmsItem.getProperties().get("displayName"), url, null, false);
                        breadcrumb.getChilds().add(0, breadcrumbItem);
                    } catch (CMSException e) {
                        throw new ControllerException(e);
                    }

                    // Get the navigation parent
                    CMSObjectPath parent = CMSObjectPath.parse(publicationPath).getParent();
                    publicationPath = parent.toString();
                }
            }
        }

        // Find first non navigation portlet index
        int firstPortletIndex = -1;
        int i = 0;
        for (BreadcrumbItem item : breadcrumbMemo.getChilds()) {
            if (!item.isNavigationPlayer()) {
                firstPortletIndex = i;
                break;
            }
            i++;
        }

        // If current page become MAXIMIZED, add to breadcrumb
        for (Object value : windowContextMap.values()) {
            WindowContext windowContext = (WindowContext) value;
            if (WindowState.MAXIMIZED.equals(windowContext.getWindowState())) {

                PortalObjectId targetWindowId = PortalObjectId.parse(windowContext.getId(), PortalObjectPath.SAFEST_FORMAT);
                Window window = (Window) this.portalObjectContainer.getObject(targetWindowId);

                // Dynamic windows already added when startDynamicCommand
                if (!"1".equals(window.getDeclaredProperty("osisia.dynamicStarted"))) {
                    // CMS portlets already added on breadcrumb
                    if (!"1".equals(window.getDeclaredProperty("osivia.cms.contextualization"))) {
                        // Delete current item
                        if (firstPortletIndex != -1) {
                            while (breadcrumbMemo.getChilds().size() > firstPortletIndex) {
                                breadcrumbMemo.getChilds().remove(firstPortletIndex);
                            }
                        }

                        // Window title
                        String title = windowContext.getProperty(InternalConstants.PROP_WINDOW_TITLE);
                        if (title == null) {
                            title = windowContext.getResult().getTitle();
                        }

                        page = renderPageCommand.getPage();
                        ViewPageCommand viewPageCommand = new ViewPageCommand(page.getId());
                        String url = new PortalURLImpl(viewPageCommand, controllerContext, null, null).toString();
                        BreadcrumbItem newItem = new BreadcrumbItem(title, url, windowContext.getId(), true);
                        breadcrumbMemo.getChilds().add(newItem);
                    }
                }
            }
        }

        if (firstPortletIndex != -1) {
            // Check if window maximized
            boolean isWindowMaximized = false;
            for (Object value : windowContextMap.values()) {
                WindowContext windowContext = (WindowContext) value;
                if (WindowState.MAXIMIZED.equals(windowContext.getWindowState())) {
                    isWindowMaximized = true;
                }
            }

            if (!isWindowMaximized) {
                while (breadcrumbMemo.getChilds().size() > firstPortletIndex) {
                    breadcrumbMemo.getChilds().remove(firstPortletIndex);
                }
            }
        }

        // Update current item
        if (breadcrumbMemo.getChilds().size() > 0) {
            for (Object value : windowContextMap.values()) {
                WindowContext windowContext = (WindowContext) value;

                if (WindowState.MAXIMIZED.equals(windowContext.getWindowState())) {
                    BreadcrumbItem last = breadcrumbMemo.getChilds().get(breadcrumbMemo.getChilds().size() - 1);

                    // Update path
                    List<PortletPathItem> portletPath = (List<PortletPathItem>) controllerContext.getAttribute(ControllerCommand.REQUEST_SCOPE,
                            "osivia.portletPath");
                    if (portletPath == null) {
                        // Update titles in order : window title, path title, portlet title
                        String title = windowContext.getProperty(InternalConstants.PROP_WINDOW_TITLE);
                        if (title == null) {
                            title = windowContext.getResult().getTitle();
                        }

                        // Update URL
                        page = renderPageCommand.getPage();
                        ViewPageCommand viewCmd = new ViewPageCommand(page.getId());
                        String url = new PortalURLImpl(viewCmd, controllerContext, null, null).toString();

                        last.setName(title);
                        last.setUrl(url);
                    } else {
                        // Valorize labels and path related URLs
                        int pathItemIndex = 0;
                        for (PortletPathItem pathItem : portletPath) {
                            // Set the content as a render parameter
                            ParametersStateString parameters = ParametersStateString.create();
                            for (Entry<String, String> name : pathItem.getRenderParams().entrySet()) {
                                parameters.setValue(name.getKey(), name.getValue());
                            }

                            // Add public parameters
                            Map<QName, String[]> ps = pageState.getParameters();
                            for (Entry<QName, String[]> pageEntry : ps.entrySet()) {
                                if (parameters.getValue(pageEntry.getKey().toString()) == null) {
                                    if (pageEntry.getValue().length > 0) {
                                        parameters.setValue(pageEntry.getKey().toString(), pageEntry.getValue()[0]);
                                    }
                                }
                            }

                            PortalObjectId targetWindowId = PortalObjectId.parse(windowContext.getId(), PortalObjectPath.SAFEST_FORMAT);

                            ControllerCommand renderCmd = new InvokePortletWindowRenderCommand(targetWindowId, Mode.VIEW, null, parameters);

                            // Perform a render URL on the target window
                            String url = new PortalURLImpl(renderCmd, controllerContext, null, null).toString();
                            pathItem.setUrl(url);

                            String label = pathItem.getLabel();
                            String title = windowContext.getProperty(InternalConstants.PROP_WINDOW_TITLE);
                            if ((pathItemIndex == 0) && (title != null)) {
                                label = title;
                            }
                            pathItem.setLabel(label);
                            last.setPortletPath(portletPath);

                            pathItemIndex++;
                        }
                    }
                }
            }
        }

        // Add memorized items
        for (BreadcrumbItem itemMemo : breadcrumbMemo.getChilds()) {

            if (!itemMemo.isNavigationPlayer()) {
                if (itemMemo.getPortletPath() != null) {
                    // Add corresponding item to portlet path
                    for (PortletPathItem pathItem : itemMemo.getPortletPath()) {
                        BreadcrumbItem pathDisplayItem = new BreadcrumbItem(pathItem.getLabel(), pathItem.getUrl(), itemMemo.getId(), true);
                        breadcrumb.getChilds().add(pathDisplayItem);
                    }
                } else {
                    // No portlet path : add corresponding item to portlet title
                    breadcrumb.getChilds().add(itemMemo);
                }
            }
        }
        return breadcrumb;
    }


    /**
     * Utility method used to get publication path.
     *
     * @param pageState page navigational state
     * @param page current page
     * @return publication path
     */
    private String getPublicationPath(PageNavigationalState pageState, Page page) {
        String sPath[] = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
        }

        String publicationPath = null;
        if (ArrayUtils.isNotEmpty(sPath)) {
            publicationPath = sPath[0];
        }
        return publicationPath;
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
