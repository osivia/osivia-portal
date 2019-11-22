/**
 *
 */
package org.osivia.portal.core.assistantpage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowRenderCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.IdentityContext;
import org.jboss.portal.identity.IdentityServiceController;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.jboss.portal.theme.render.renderer.RegionRendererContext;
import org.jboss.portal.theme.render.renderer.WindowRendererContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.ecm.EcmViews;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSConfigurationItem;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.cms.RegionInheritance;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.pagemarker.PortalCommandFactory;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;

public class CMSEditionPageCustomizerInterceptor extends ControllerInterceptor {

    private String targetContextPath;

    private String pageSettingPath;

    private LayoutService serviceLayout;

    private ThemeService serviceTheme;

    private InstanceContainer instanceContainer;

    private IdentityServiceController identityServiceController;

    private RoleModule roleModule;

    private AuthorizationDomainRegistry authorizationDomainRegistry;

    private PortalObjectContainer portalObjectContainer;

    private IProfilManager profilManager;

    private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

    ICMSService cmsService;

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();
    }


    public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
        return this.portalAuthorizationManagerFactory;
    }

    public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory portalAuthorizationManagerFactory) {
        this.portalAuthorizationManagerFactory = portalAuthorizationManagerFactory;
    }

    public IProfilManager getProfilManager() {
        return this.profilManager;
    }

    public void setProfilManager(IProfilManager profilManager) {
        this.profilManager = profilManager;
    }


    /**
     * Check if the user can modify the current page
     *
     * @param ctx
     * @param page
     * @return
     * @throws Exception
     */
    public static boolean checkWritePermission(ControllerContext ctx, Page page) throws Exception {

        // Get edit authorization
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setServerInvocation(ctx.getServerInvocation());

        NavigationalStateContext nsContext = (NavigationalStateContext) ctx.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);


        PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());


        String pagePath = null;
        String sPath[] = null;
        if (pageState != null) {
            sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            if ((sPath != null) && (sPath.length == 1)) {
                pagePath = sPath[0];
            }
        }


        if (pagePath != null) {
            return checkWritePermission(ctx, pagePath);
        }

        return false;


    }


    /**
     * Check if the user can modify the current page
     *
     * @param ctx
     * @param page
     * @return
     * @throws Exception
     */
    public static boolean checkWritePermission(ControllerContext ctx, String pagePath) throws Exception {

        // Get edit authorization
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setServerInvocation(ctx.getServerInvocation());


        if (pagePath != null) {
            CMSPublicationInfos pubInfos = getCMSService().getPublicationInfos(cmsContext, pagePath);

            if (pubInfos.isEditableByUser()) {
                return true;
            }
        }

        return false;


    }

    /**
     * Check if the current content is managed as a web page
     *
     * @param ctx the portal context
     * @param page the current page
     * @return true if the type is allowed in CMS edition mode
     * @throws Exception
     */
    public static boolean checkWebPagePermission(ControllerContext ctx, Page page) throws Exception {

     String contentPath = PagePathUtils.getContentPath(ctx, page.getId());
        String pagePath = PagePathUtils.getNavigationPath(ctx, page.getId());

      if( ! StringUtils.equals(contentPath, pagePath)) {
           return false;
      }

        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setServerInvocation(ctx.getServerInvocation());


        if (pagePath != null) {

            Boolean pageInEditionMode =  (Boolean) ctx.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.isPageInEditionMode");

            if( BooleanUtils.isTrue(pageInEditionMode)) {
                cmsContext.setDisplayLiveVersion("1");
            }
            // check if type is known as a web page type
            return getCMSService().isCmsWebPage(cmsContext, pagePath);

        }
        return false;

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
        ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

        if ((resp instanceof PageRendition) && (cmd instanceof PageCommand)) {
            PageRendition rendition = (PageRendition) resp;
            PageCommand pageCommand = (PageCommand) cmd;
            Page page = pageCommand.getPage();

            ControllerContext controllerContext = cmd.getControllerContext();

            // Synchronize region contexts
            this.synchronizeRegionContexts(rendition, page);

            // Online mode indicator
            boolean online;
            if (PortalObjectUtils.isSpaceSite(page)) {
                online = Level.allowOnlineVersion.equals(CmsPermissionHelper.getCurrentPageSecurityLevel(controllerContext, page.getId()));
            } else {
                NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext
                        .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
                PageNavigationalState ns = nsContext.getPageNavigationalState(page.getId().getPath().toString());
                if (ns != null) {
                    EditionState editionState = ContributionService.getNavigationalState(controllerContext, ns);
                    online = (editionState == null) || EditionState.CONTRIBUTION_MODE_ONLINE.equals(editionState.getContributionMode());
                } else {
                    online = true;
                }
            }


            // CMS service
            ICMSService cmsService = getCMSService();

            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setControllerContext(controllerContext);
            if (!online) {
                cmsContext.setDisplayLiveVersion("1");
            }

            // Page CMS path
            String pageCMSPath = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
            if (StringUtils.isNotEmpty(pageCMSPath)) {
                // Fetch document
                CMSItem cmsItem = cmsService.getContent(cmsContext, pageCMSPath);

                // Global configuration
                this.injectCMSPorletGlobalConfiguration(controllerContext, cmsContext, cmsItem, rendition, page);

                if (!online && checkWritePermission(controllerContext, page)) {
                    // Edition tools
                    this.injectCMSPortletEditionTools(controllerContext, cmsContext, cmsItem, rendition, page);
                }
            }
        }

        return resp;
    }


    /**
     * Inject CMS portlet global configuration.
     *
     * @param controllerContext controller context
     * @param cmsContext CMS context
     * @param cmsItem current CMS item
     * @param rendition page rendition
     * @param page current page
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void injectCMSPorletGlobalConfiguration(ControllerContext controllerContext, CMSServiceCtx cmsContext, CMSItem cmsItem, PageRendition rendition,
            Page page) throws Exception {
        // CMS service
        ICMSService cmsService = getCMSService();

        // Page layout
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout pageLayout = this.getServiceLayout().getLayout(layoutId, true);


        // Locale
        Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();

        // Regions layout
        Set<CMSConfigurationItem> regionLayouts = cmsService.getCMSRegionLayoutsConfigurationItems(cmsContext);
        PageProperties.getProperties().setRegionLayouts(regionLayouts);
        Map<String, CMSConfigurationItem> regionsSelectedLayout = cmsService.getCMSRegionsSelectedLayout(cmsItem, regionLayouts);


        for (Object regionObject : rendition.getPageResult().getRegions()) {
            // Region
            RegionRendererContext region = (RegionRendererContext) regionObject;

            // Check if page layout contains this region
            if (pageLayout.getLayoutInfo().getRegionNames().contains(region.getId())) {
                // Region identifier
                String regionId = region.getId();
                // Region properties
                Map<String, String> regionProperties = region.getProperties();


                // Locale
                regionProperties.put(InternalConstants.LOCALE_PROPERTY, locale.toString());

                // Selected region layout
                CMSConfigurationItem regionLayout = regionsSelectedLayout.get(regionId);
                if (regionLayout != null) {
                    regionProperties.put(InternalConstants.CMS_REGION_LAYOUT_CODE, regionLayout.getCode());
                    regionProperties.put(InternalConstants.CMS_REGION_LAYOUT_CLASS, regionLayout.getAdditionalCode());
                }


                // Region windows
                for (Object windowObject : region.getWindows()) {
                    WindowRendererContext windowRendererContext = (WindowRendererContext) windowObject;
                    Map<String, String> windowProperties = windowRendererContext.getProperties();
                    String windowId = windowRendererContext.getId();

                    if (!windowId.endsWith("PIA_EMPTY")) {
                        PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
                        Window window = (Window) this.getPortalObjectContainer().getObject(poid);

                        // Ref URI
                        String refURI = window.getProperty("osivia.refURI");
                        windowProperties.put("osivia.windowId", refURI);
                    }
                }
            }
        }
    }


    /**
     * Inject CMS portlet edition tools.
     *
     * @param controllerContext controller context
     * @param cmsContext CMS context
     * @param cmsItem current CMS item
     * @param rendition page rendition
     * @param page current page
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void injectCMSPortletEditionTools(ControllerContext controllerContext, CMSServiceCtx cmsContext, CMSItem cmsItem, PageRendition rendition,
            Page page) throws Exception {
        // CMS service
        ICMSService cmsService = getCMSService();

        // URL context
        URLContext urlContext = controllerContext.getServerInvocation().getServerContext().getURLContext();
        // URL format
        URLFormat urlFormat = URLFormat.newInstance(true, true);

        // Page identifier
        String pageId = PortalObjectUtils.getHTMLSafeId(page.getId());

        // Page layout
        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout pageLayout = this.getServiceLayout().getLayout(layoutId, true);


        // Show advanced CMS tools indicator
        String advancedCMSTools = String.valueOf(controllerContext.getAttribute(Scope.SESSION_SCOPE, InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR));

        // Regions inheritance
        Map<String, RegionInheritance> regionsInheritance = cmsService.getCMSRegionsInheritance(cmsItem);

        // Refresh page command URL
        RefreshPageCommand resfreshCmd = new RefreshPageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
        String resfreshUrl = controllerContext.renderURL(resfreshCmd, urlContext, urlFormat);

        // ECM base URL
        String ecmBaseUrl = cmsService.getEcmDomain(cmsContext);


        // Show CMS tools indicator
        boolean showCMSTools = this.showCmsTools(controllerContext, page)
                && CMSEditionPageCustomizerInterceptor.checkWebPagePermission(controllerContext, page);


        for (Object regionObject : rendition.getPageResult().getRegions()) {
            // Region
            RegionRendererContext region = (RegionRendererContext) regionObject;

            // Check if page layout contains this region
            if (pageLayout.getLayoutInfo().getRegionNames().contains(region.getId())) {
                // Region identifier
                String regionId = region.getId();
                // Region properties
                Map<String, String> regionProperties = region.getProperties();


                // Show CMS tools indicator
                regionProperties.put(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY, String.valueOf(showCMSTools));
                // Show advanced CMS tools indicator
                regionProperties.put(InternalConstants.SHOW_ADVANCED_CMS_TOOLS_INDICATOR, advancedCMSTools);

                // Inheritance mode
                RegionInheritance inheritance = regionsInheritance.get(regionId);
                if (inheritance != null) {
                    regionProperties.put(InternalConstants.INHERITANCE_VALUE_REGION_PROPERTY, inheritance.getValue());
                }

                // Save inheritance configuration command URL
                ControllerCommand saveInheritanceConfigurationCommand = new SaveInheritanceConfigurationCommand(pageId, cmsItem.getPath(), regionId, null);
                String saveInheritanceConfigurationURL = controllerContext.renderURL(saveInheritanceConfigurationCommand, urlContext, urlFormat);
                regionProperties.put(InternalConstants.INHERITANCE_SAVE_URL, saveInheritanceConfigurationURL);

                // Save region layout command URL
                ControllerCommand saveRegionLayoutCommand = new SaveRegionLayoutCommand(pageId, cmsItem.getPath(), regionId, null);
                String saveRegionLayoutURL = controllerContext.renderURL(saveRegionLayoutCommand, urlContext, urlFormat);
                regionProperties.put(InternalConstants.CMS_REGION_LAYOUT_SAVE_URL, saveRegionLayoutURL);

                // build and set url for create fgt in region in CMS mode
                Map<String, String> requestParameters = new HashMap<String, String>();
                requestParameters.put("region", regionId);
                String ecmCreateInRegionUrl = cmsService.getEcmUrl(cmsContext, EcmViews.createFgtInRegion, cmsItem.getPath(), requestParameters);
                regionProperties.put("osivia.cmsCreateUrl", ecmCreateInRegionUrl);

                // Refresh page command URL
                regionProperties.put("osivia.cmsCreateCallBackURL", resfreshUrl);

                // ECM base URL
                regionProperties.put("osivia.ecmBaseUrl", ecmBaseUrl);


                for (Object windowObject : region.getWindows()) {
                    // Window
                    WindowRendererContext windowRendererContext = (WindowRendererContext) windowObject;
                    Map<String, String> windowProperties = windowRendererContext.getProperties();
                    String windowId = windowRendererContext.getId();

                    // Update region properties if it contains inherited window
                    if (BooleanUtils.toBoolean(windowProperties.get(InternalConstants.INHERITANCE_INDICATOR_PROPERTY))) {
                        regionProperties.put(InternalConstants.INHERITANCE_INDICATOR_PROPERTY, String.valueOf(true));
                    }
                    // Update region properties if it contains locked window
                    if (BooleanUtils.toBoolean(windowProperties.get(InternalConstants.INHERITANCE_LOCKED_INDICATOR_PROPERTY))) {
                        regionProperties.put(InternalConstants.INHERITANCE_LOCKED_INDICATOR_PROPERTY, String.valueOf(true));
                    }

                    if (!windowId.endsWith("PIA_EMPTY")) {
                        PortalObjectId poid = PortalObjectId.parse(windowId, PortalObjectPath.SAFEST_FORMAT);
                        Window window = (Window) this.getPortalObjectContainer().getObject(poid);

                        if ("1".equals(window.getDeclaredProperty("osivia.dynamic.cmsEditable"))) {
                            // Set the current edition mode to the window
                            windowProperties.put(InternalConstants.SHOW_CMS_TOOLS_INDICATOR_PROPERTY, String.valueOf(showCMSTools));


                            // build and set urls for create/edit fgts in window in CMS mode
                            String refURI = window.getProperty("osivia.refURI");

                            requestParameters = new HashMap<String, String>();
                            requestParameters.put("belowURI", refURI);

                            windowProperties.put("osivia.ecmBaseUrl", ecmBaseUrl);

                            String cmsCreateUrl = cmsService.getEcmUrl(cmsContext, EcmViews.createFgtBelowWindow, cmsItem.getPath(), requestParameters);
                            windowProperties.put("osivia.cmsCreateUrl", cmsCreateUrl);
                            windowProperties.put("osivia.cmsCreateCallBackURL", resfreshUrl);

                            requestParameters.put("refURI", refURI);
                            String cmsEditUrl = cmsService.getEcmUrl(cmsContext, EcmViews.editFgt, cmsItem.getPath(), requestParameters);
                            windowProperties.put("osivia.cmsEditUrl", cmsEditUrl);


                            // To reload only current window on backup
                            InvokePortletWindowRenderCommand endPopupCMD = new InvokePortletWindowRenderCommand(poid, Mode.VIEW, WindowState.NORMAL);
                            String url = new PortalURLImpl(endPopupCMD, controllerContext, null, null).toString();


                            // 20131004JSS : prise en compte des urls web
                            int insertIndex = url.indexOf(PageMarkerUtils.PAGE_MARKER_PATH);
                            if (insertIndex == -1) {
                                // Web command
                                insertIndex = url.indexOf("/web?");
                                if (insertIndex == -1) {
                                    insertIndex = url.indexOf("/web/");
                                }
                            }

                            if (insertIndex != -1) {
                                url = url.substring(0, insertIndex) + PortalCommandFactory.POPUP_REFRESH_PATH + url.substring(insertIndex + 1);
                            }


                            windowProperties.put("osivia.cmsEditCallbackUrl", url);
                            // Sera ignoré car on n'est pas en ajax
                            windowProperties.put("osivia.cmsEditCallbackId", windowId);

                            // Duplicate fragment command
                            ControllerCommand duplicateCMD = new CMSDuplicateFragmentCommand(pageId, cmsItem.getPath(), refURI);
                            String duplicateFragmentUrl = controllerContext.renderURL(duplicateCMD, urlContext, urlFormat);
                            windowProperties.put("osivia.cmsDuplicateUrl", duplicateFragmentUrl + "#" + refURI);
                            
                            // Delete fragment command
                            ControllerCommand deleteCMD = new CMSDeleteFragmentCommand(pageId, cmsItem.getPath(), refURI);
                            String deleteFragmentUrl = controllerContext.renderURL(deleteCMD, urlContext, urlFormat);
                            windowProperties.put("osivia.cmsDeleteUrl", deleteFragmentUrl);
                            windowProperties.put("osivia.cmsRegionId", regionId);
                        }
                    }
                }
            }
        }
    }


    /**
     * Synchronize context regions with layout.
     *
     * if a region is not present in the context, creates a new one
     *
     * @param rendition
     * @param page
     * @throws Exception
     */
    // TODO : mutualiser avec mode EDITION PAGE (AssistantPageCustomizer)
    private void synchronizeRegionContexts(PageRendition rendition, Page page) throws Exception {

        String layoutId = page.getProperty(ThemeConstants.PORTAL_PROP_LAYOUT);
        PortalLayout layout = this.getServiceLayout().getLayout(layoutId, true);

        if ((layout != null) && (layout.getLayoutInfo() != null) && CollectionUtils.isNotEmpty(layout.getLayoutInfo().getRegionNames())) {
            for (Object region : layout.getLayoutInfo().getRegionNames()) {
                String regionName = (String) region;
                RegionRendererContext renderCtx = rendition.getPageResult().getRegion(regionName);
                if (renderCtx == null) {
                    // Empty region : must create blank window
                    Map<String, String> windowProps = new HashMap<String, String>();
                    windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, "emptyRenderer");
                    windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, "emptyRenderer");
                    windowProps.put(InternalConstants.ATTR_WINDOWS_HIDDEN_INDICATOR, String.valueOf(true));

                    WindowResult windowResult = new WindowResult("PIA_EMPTY", "", Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL, Mode.VIEW);
                    WindowContext windowContext = new WindowContext(regionName + "_PIA_EMPTY", regionName, "0", windowResult);
                    rendition.getPageResult().addWindowContext(windowContext);

                    renderCtx = rendition.getPageResult().getRegion2(regionName);
                }
            }
        }
    }


    private boolean showCmsTools(ControllerContext controllerContext, Page page) {
        boolean show;
        if (PortalObjectUtils.isSpaceSite(page)) {
            Boolean currentEditionMode = CmsPermissionHelper.getCurrentCmsEditionMode(controllerContext).equals(CmsPermissionHelper.CMS_EDITION_MODE_ON);
            if (currentEditionMode && CmsPermissionHelper.getCurrentCmsVersion(controllerContext).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)) {
                // in online mode, cms tools are hidden
                show = false;
            } else {
                show = currentEditionMode;
            }
        } else {
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            PageNavigationalState ns = nsContext.getPageNavigationalState(page.getId().getPath().toString());
            if (ns != null) {
                EditionState editionState = ContributionService.getNavigationalState(controllerContext, ns);
                show = (editionState != null) && EditionState.CONTRIBUTION_MODE_EDITION.equals(editionState.getContributionMode());
            } else {
                show = false;
            }
        }
        return show;
    }


    /**
     * @return the serviceLayout
     */
    public LayoutService getServiceLayout() {
        return this.serviceLayout;
    }

    /**
     * @param serviceLayout
     *            the serviceLayout to set
     */
    public void setServiceLayout(LayoutService serviceLayout) {
        this.serviceLayout = serviceLayout;
    }

    /**
     * @return the pageSettingPath
     */
    public String getPageSettingPath() {
        return this.pageSettingPath;
    }

    /**
     * @param pageSettingPath
     *            the pageSettingPath to set
     */
    public void setPageSettingPath(String pageSettingPath) {
        this.pageSettingPath = pageSettingPath;
    }

    /**
     * @return the roleModule
     */
    public RoleModule getRoleModule() throws Exception {
        if (this.roleModule == null) {
            this.roleModule = (RoleModule) this.getIdentityServiceController().getIdentityContext().getObject(IdentityContext.TYPE_ROLE_MODULE);
        }
        return this.roleModule;
    }

    /**
     * @param roleModule
     *            the roleModule to set
     */
    public void setRoleModule(RoleModule roleModule) {
        this.roleModule = roleModule;
    }

    /**
     * @return the authorizationDomainRegistry
     */
    public AuthorizationDomainRegistry getAuthorizationDomainRegistry() {
        return this.authorizationDomainRegistry;
    }

    /**
     * @param authorizationDomainRegistry
     *            the authorizationDomainRegistry to set
     */
    public void setAuthorizationDomainRegistry(AuthorizationDomainRegistry authorizationDomainRegistry) {
        this.authorizationDomainRegistry = authorizationDomainRegistry;
    }

    /**
     * @return the identityServiceController
     */
    public IdentityServiceController getIdentityServiceController() {
        return this.identityServiceController;
    }

    /**
     * @param identityServiceController
     *            the identityServiceController to set
     */
    public void setIdentityServiceController(IdentityServiceController identityServiceController) {
        this.identityServiceController = identityServiceController;
    }

    /**
     * @return the targetContextPath
     */
    public String getTargetContextPath() {
        return this.targetContextPath;
    }

    /**
     * @param targetContextPath
     *            the targetContextPath to set
     */
    public void setTargetContextPath(String targetContextPath) {
        this.targetContextPath = targetContextPath;
    }

    /**
     * @return the instanceContainer
     */
    public InstanceContainer getInstanceContainer() {
        return this.instanceContainer;
    }

    /**
     * @param instanceContainer
     *            the instanceContainer to set
     */
    public void setInstanceContainer(InstanceContainer instanceContainer) {
        this.instanceContainer = instanceContainer;
    }

    /**
     * @return the serviceTheme
     */
    public ThemeService getServiceTheme() {
        return this.serviceTheme;
    }

    /**
     * @param serviceTheme
     *            the serviceTheme to set
     */
    public void setServiceTheme(ThemeService serviceTheme) {
        this.serviceTheme = serviceTheme;
    }

    public PortalObjectContainer getPortalObjectContainer() {
        return this.portalObjectContainer;
    }

    public void setPortalObjectContainer(PortalObjectContainer portalObjectContainer) {
        this.portalObjectContainer = portalObjectContainer;
    }

}
