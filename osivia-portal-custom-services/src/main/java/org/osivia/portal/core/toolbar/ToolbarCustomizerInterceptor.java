package org.osivia.portal.core.toolbar;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.PageCommand;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.WindowCommand;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.identity.Role;
import org.jboss.portal.security.PortalSecurityException;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.PortalLayout;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.jboss.portal.theme.impl.render.dynamic.DynaRenderOptions;
import org.jboss.portal.theme.page.Region;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.AssistantPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.CMSEditionPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.assistantpage.DeletePageCommand;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.EcmCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.constants.InternationalizationConstants;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;
import org.osivia.portal.core.page.MonEspaceCommand;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageType;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.RefreshPageCommand;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Toolbar customizer interceptor.
 *
 * @see AssistantPageCustomizerInterceptor
 */
public class ToolbarCustomizerInterceptor extends AssistantPageCustomizerInterceptor {

    /** Toolbar order. */
    private static final String TOOLBAR_ORDER = "0";
    /** Empty renderer. */
    private static final String EMPTY_RENDERER = "emptyRenderer";
    /** Toolbar window title. */
    private static final String WINDOW_TITLE_TOOLBAR = "Toolbar";
    /** Toolbar region name. */
    private static final String REGION_NAME_TOOLBAR = "toolbar";

    /** HTML class "toolbar-administration". */
    private static final String HTML_CLASS_TOOLBAR_ADMINISTRATION = "toolbar-administration";
    /** HTML class "toolbar-menu". */
    private static final String HTML_CLASS_TOOLBAR_MENU = "toolbar-menu";
    /** HTML class "toolbar-menu-title". */
    private static final String HTML_CLASS_TOOLBAR_MENU_TITLE = "toolbar-menu-title";
    /** HTML class "preview-version". */
    private static final String HTML_CLASS_PREVIEW = "preview-version";
    /** HTML class "online-version". */
    private static final String HTML_CLASS_ONLINE = "online-version";
    /** HTML class "fancybox_inline". */
    private static final String HTML_CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** HTML class "fancybox_refresh". */
    private static final String HTML_CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";

    /** HTML identifier for configuration menu. */
    private static final String HTML_ID_CONFIGURATION_MENU = "toolbar-administration-configuration";
    /** HTML identifier for edition menu. */
    private static final String HTML_ID_EDITION_MENU = "toolbar-administration-edition";

    /** Pages list URL. */
    private static final String URL_PAGES_LIST = "#pages-list";
    /** Page creation URL. */
    private static final String URL_PAGE_CREATION = "#page-creation";
    /** Template creation URL. */
    private static final String URL_TEMPLATE_CREATION = "#template-creation";
    /** Page suppression URL. */
    private static final String URL_PAGE_SUPPRESSION = "#page-suppression";
    /** Page location URL. */
    private static final String URL_PAGE_LOCATION = "#page-location";
    /** Page properties URL. */
    private static final String URL_PAGE_PROPERTIES = "#page-properties";
    /** Page CMS URL. */
    private static final String URL_PAGE_CMS = "#page-cms";
    /** Page rights URL. */
    private static final String URL_PAGE_RIGHTS = "#page-rights";


    /** Logger. */
    protected static final Log logger = LogFactory.getLog(ToolbarCustomizerInterceptor.class);

    /** Admin portal ID. */
    private static final PortalObjectId adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

    /** CMS service locator. */
    private static ICMSServiceLocator cmsServiceLocator;


    /** Toolbar path. */
    private String toolbarPath;
    /** Toolbar settings path. */
    private String toolbarSettingsPath;
    /** URL factory. */
    private IPortalUrlFactory urlFactory;
    /** Target settings context path. */
    private String targetSettingsContextPath;


    /**
     * Default constructor.
     */
    public ToolbarCustomizerInterceptor() {
        super();
    }


    /**
     * CMS service access.
     *
     * @return CMS service
     * @throws Exception
     */
    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


    /**
     * Invocation.
     *
     * @param command command
     * @return invocation response
     */
    @Override
    public ControllerResponse invoke(ControllerCommand command) throws Exception {
        ControllerResponse response = (ControllerResponse) command.invokeNext();

        if ((response instanceof PageRendition) && (command instanceof RenderPageCommand)) {
            RenderPageCommand renderPageCommand = (RenderPageCommand) command;
            PageRendition rendition = (PageRendition) response;

            Portal portal = renderPageCommand.getPage().getPortal();
            boolean jbossAdministration = InternalConstants.JBOSS_ADMINISTRATION_PORTAL_NAME.equalsIgnoreCase(portal.getName());

            PortalObjectId popupWindowId = (PortalObjectId) command.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");


            // Toolbar must not be loaded :
            // - in JBoss portal administration
            // - in popup mode
            if (!jbossAdministration && (popupWindowId == null)) {
                // Toolbar
                String toolbarContent = this.injectToolbar(renderPageCommand);
                if (toolbarContent != null) {
                    Map<String, String> windowProps = new HashMap<String, String>();
                    windowProps.put(ThemeConstants.PORTAL_PROP_WINDOW_RENDERER, EMPTY_RENDERER);
                    windowProps.put(ThemeConstants.PORTAL_PROP_DECORATION_RENDERER, EMPTY_RENDERER);
                    windowProps.put(ThemeConstants.PORTAL_PROP_PORTLET_RENDERER, EMPTY_RENDERER);
                    WindowResult result = new WindowResult(WINDOW_TITLE_TOOLBAR, toolbarContent, Collections.EMPTY_MAP, windowProps, null, WindowState.NORMAL,
                            Mode.VIEW);
                    WindowContext toolbar = new WindowContext(WINDOW_TITLE_TOOLBAR, REGION_NAME_TOOLBAR, TOOLBAR_ORDER, result);
                    rendition.getPageResult().addWindowContext(toolbar);

                    Region region = rendition.getPageResult().getRegion2(REGION_NAME_TOOLBAR);
                    DynaRenderOptions.NO_AJAX.setOptions(region.getProperties());
                }

                // Toolbar settings
                String toolbarSettingsContent = this.injectToolbarSettings(renderPageCommand);
                if (toolbarSettingsContent != null) {
                    // Toolbar settings content save
                    command.getControllerContext().setAttribute(ControllerCommand.REQUEST_SCOPE, InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTENT,
                            toolbarSettingsContent);
                }
            }
        }

        return response;
    }

    /**
     * Toolbar injection.
     *
     * @param command page command
     * @return toolbar HTML content
     * @throws Exception
     */
    private String injectToolbar(PageCommand command) throws Exception {
        ControllerContext context = command.getControllerContext();
        ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(getTargetThemeContextPath(command), this.toolbarPath);

        if (dispatcher != null) {
            Page page = command.getPage();

            Principal principal = context.getServerInvocation().getServerContext().getClientRequest().getUserPrincipal();
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_PRINCIPAL, principal);

            // Redirection vers mon espace
            MonEspaceCommand monEspaceCommand = new MonEspaceCommand();
            PortalURL portalURL = new PortalURLImpl(monEspaceCommand, context, true, null);
            if (principal == null) {
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_LOGIN_URL, portalURL.toString());
            } else {
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_MY_SPACE_URL, portalURL.toString());
            }

            try {
                // Administration HTML content that must be injected into toolbar
                String administrationHtmlContent = this.formatHtmlAdministration(context, page);
                dispatcher.setAttribute(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT, administrationHtmlContent);
            } catch (PortalSecurityException e) {
                logger.error(StringUtils.EMPTY, e);
            }

            // Refresh
            RefreshPageCommand refreshCmd = new RefreshPageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, new PortalURLImpl(refreshCmd, context, false, null).toString());

            // Sign out
            SignOutCommand signOutCommand = new SignOutCommand();
            dispatcher.setAttribute(Constants.ATTR_TOOLBAR_SIGN_OUT_URL, new PortalURLImpl(signOutCommand, context, false, null).toString());

            dispatcher.include();
            return dispatcher.getMarkup();
        }

        return null;
    }

    /**
     * Utility method used to generate administration HTML content.
     *
     * @param context controller context
     * @param page current page
     * @return HTML data
     * @throws Exception
     */
    private String formatHtmlAdministration(ControllerContext context, Page page) throws Exception {
        PageType pageType = PageType.getPageType(page, context);

        // Administration root element
        Element administration = new DOMElement(QName.get(HTMLConstants.DIV));
        administration.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_ADMINISTRATION);
        administration.setText(StringUtils.EMPTY);

        if (PageCustomizerInterceptor.isAdministrator(context)) {
            // Configuration menu
            this.generateAdministrationConfigurationMenu(context, page, administration);

            if (!(PageType.DYNAMIC_PAGE.equals(pageType) || (PortalObjectUtils.isSpaceSite(page) && !PortalObjectUtils.isTemplate(page)))) {
                // Edition menu
                this.generateAdministrationEditionMenu(context, page, administration);
            }
        }

        if (CMSEditionPageCustomizerInterceptor.checkWritePermission(context, page)
                && CMSEditionPageCustomizerInterceptor.checkWebPagePermission(context, page)) {
            // Web page menu
            this.generateAdministrationWebPageMenu(context, page, administration);
            this.generateAdministrationToggleVersion(context, page, administration);
        }

        return administration.asXML();
    }


    /**
     * Utility method used to generate configuration menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     * @throws Exception
     */
    private void generateAdministrationConfigurationMenu(ControllerContext context, Page page, Element administration) throws Exception {
        Locale locale = context.getServerInvocation().getRequest().getLocale();
        PageType pageType = PageType.getPageType(page, context);

        // Configuration menu root element
        Element configurationMenu = new DOMElement(QName.get(HTMLConstants.DIV));
        configurationMenu.addAttribute(QName.get(HTMLConstants.ID), HTML_ID_CONFIGURATION_MENU);
        configurationMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
        administration.add(configurationMenu);

        // Configuration menu title
        Element configurationMenuTitle = new DOMElement(QName.get(HTMLConstants.A));
        configurationMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
        configurationMenuTitle.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CONFIGURATION_MENU_TITLE, locale));
        configurationMenu.add(configurationMenuTitle);

        // Configuration menu "ul" node
        Element configurationMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        configurationMenu.add(configurationMenuUl);

        // Home
        Element home = new DOMElement(QName.get(HTMLConstants.A));
        home.addAttribute(QName.get(HTMLConstants.HREF), "/portal");
        home.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_HOME, locale));
        this.addSubMenuElement(configurationMenuUl, home);

        // OSIVIA Portal administration
        PortalControllerContext portalControllerContext = new PortalControllerContext(context);
        String osiviaAdministrationUrl = this.urlFactory.getStartPortletUrl(portalControllerContext, InternalConstants.PORTLET_ADMINISTRATION_INSTANCE_NAME,
                null, null, true);

        Element osiviaAdministration = new DOMElement(QName.get(HTMLConstants.A));
        osiviaAdministration.addAttribute(QName.get(HTMLConstants.HREF), osiviaAdministrationUrl);
        osiviaAdministration.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYFRAME);
        osiviaAdministration.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_OSIVIA_ADMINISTRATION, locale));
        this.addSubMenuElement(configurationMenuUl, osiviaAdministration);

        // JBoss administration
        ViewPageCommand jbossAdministrationCommand = new ViewPageCommand(adminPortalId);
        String jbossAdministrationUrl = new PortalURLImpl(jbossAdministrationCommand, context, null, null).toString();

        Element jbossAdministration = new DOMElement(QName.get(HTMLConstants.A));
        jbossAdministration.addAttribute(QName.get(HTMLConstants.HREF), jbossAdministrationUrl);
        jbossAdministration.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_JBOSS_ADMINISTRATION, locale));
        this.addSubMenuElement(configurationMenuUl, jbossAdministration);

        // Pages list
        this.addSubMenuFancyboxLink(configurationMenuUl, URL_PAGES_LIST,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_PAGES_LIST, locale));

        // HR
        this.addSubMenuElement(configurationMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        if (InternalConstants.PORTAL_TYPE_STATIC_PORTAL.equals(page.getPortal().getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE))) {
            // Page creation
            this.addSubMenuFancyboxLink(configurationMenuUl, URL_PAGE_CREATION,
                    this.getInternationalizationService().getString(InternationalizationConstants.KEY_PAGE_CREATION, locale));
        }

        // Template creation
        this.addSubMenuFancyboxLink(configurationMenuUl, URL_TEMPLATE_CREATION,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_TEMPLATE_CREATION, locale));

        // Page template access
        if (pageType.isTemplated()) {
            ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
            ViewPageCommand pageTemplateAccessCommand = new ViewPageCommand(templatePortalObject.getTemplate().getId());
            String pageTemplateAccessUrl = new PortalURLImpl(pageTemplateAccessCommand, context, null, null).toString();
            pageTemplateAccessUrl += "?init-state=true&edit-template-mode=true";

            Element pageTemplateAccessLink = new DOMElement(QName.get(HTMLConstants.A));
            pageTemplateAccessLink.addAttribute(QName.get(HTMLConstants.HREF), pageTemplateAccessUrl);
            pageTemplateAccessLink.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS, locale));
            this.addSubMenuElement(configurationMenuUl, pageTemplateAccessLink);
        } else {
            Element pageTemplateAccessDisable = new DOMElement(QName.get(HTMLConstants.P));
            pageTemplateAccessDisable.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS, locale));

            this.addSubMenuElement(configurationMenuUl, pageTemplateAccessDisable);
        }

        // HR
        this.addSubMenuElement(configurationMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        // Caches initialization
        ViewPageCommand cachesInitializationCommand = new ViewPageCommand(page.getId());
        String cachesInitializationUrl = new PortalURLImpl(cachesInitializationCommand, context, null, null).toString();
        cachesInitializationUrl += "?init-cache=true";

        Element cachesInitialization = new DOMElement(QName.get(HTMLConstants.A));
        cachesInitialization.addAttribute(QName.get(HTMLConstants.HREF), cachesInitializationUrl);
        cachesInitialization.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CACHES_INITIALIZATION, locale));
        this.addSubMenuElement(configurationMenuUl, cachesInitialization);
    }

    /**
     * Utility method used to generate edition menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     */
    private void generateAdministrationEditionMenu(ControllerContext context, Page page, Element administration) {
        Locale locale = context.getServerInvocation().getRequest().getLocale();
        PageType pageType = PageType.getPageType(page, context);

        // Edition menu root element
        Element editionMenu = new DOMElement(QName.get(HTMLConstants.DIV));
        editionMenu.addAttribute(QName.get(HTMLConstants.ID), HTML_ID_EDITION_MENU);
        editionMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
        administration.add(editionMenu);

        // Edition menu title
        Element editionMenuTitle = new DOMElement(QName.get(HTMLConstants.A));
        editionMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
        if (pageType.isSpace()) {
            editionMenuTitle.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_SPACE_EDITION_MENU_TITLE, locale));
        } else if (PortalObjectUtils.isTemplate(page)) {
            editionMenuTitle.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_TEMPLATE_EDITION_MENU_TITLE, locale));
        } else {
            editionMenuTitle.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_PAGE_EDITION_MENU_TITLE, locale));
        }
        editionMenu.add(editionMenuTitle);

        // Edition menu "ul" node
        Element editionMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        editionMenu.add(editionMenuUl);

        if (!pageType.isTemplated()) {
            // Icons display
            String mode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            ChangeModeCommand changeModeCommand;
            String modeHtmlClass;
            if (InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE.equals(mode)) {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), StringUtils.EMPTY);
                modeHtmlClass = HTMLConstants.CLASS_CHECK;
            } else {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                        InternalConstants.VALUE_WINDOWS_SETTING_WIZARD_MODE);
                modeHtmlClass = HTMLConstants.CLASS_UNCHECK;
            }
            String changeModeUrl = new PortalURLImpl(changeModeCommand, context, null, null).toString();

            Element iconsDisplay = new DOMElement(QName.get(HTMLConstants.A));
            iconsDisplay.addAttribute(QName.get(HTMLConstants.HREF), changeModeUrl);
            iconsDisplay.addAttribute(QName.get(HTMLConstants.CLASS), modeHtmlClass);
            iconsDisplay.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_ICONS_DISPLAY, locale));
            this.addSubMenuElement(editionMenuUl, iconsDisplay);

            // HR
            this.addSubMenuElement(editionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));
        }

        // Page suppression
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_SUPPRESSION,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_SUPPRESSION, locale));

        // Page location
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_LOCATION,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_LOCATION, locale));

        // HR
        this.addSubMenuElement(editionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        // Page properties
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_PROPERTIES,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_PROPERTIES, locale));

        // Page CMS
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_CMS,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_CONFIGURATION, locale));

        // Page rights
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_RIGHTS,
                this.getInternationalizationService().getString(InternationalizationConstants.KEY_RIGHTS, locale));
    }

    /**
     * Utility method used to generate web page menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     * @throws Exception
     */
    private void generateAdministrationWebPageMenu(ControllerContext context, Page page, Element administration) throws Exception {
        Locale locale = context.getServerInvocation().getRequest().getLocale();

        boolean modePreview = CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW);
        boolean editionModeOn = CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_ON);


        Map<String, String> requestParameters = new HashMap<String, String>();

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSPublicationInfos publicationInfos = getCMSService().getPublicationInfos(cmsCtx, pagePath);

        String path = publicationInfos.getDocumentPath();
        Boolean published = publicationInfos.isPublished();



        URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

        // CMS edition menu root element
        Element cmsEditionMenu = new DOMElement(QName.get(HTMLConstants.DIV));
        cmsEditionMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
        administration.add(cmsEditionMenu);

        // CMS edition menu title
        Element cmsEditionMenuTitle = new DOMElement(QName.get(HTMLConstants.A));

        cmsEditionMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
        cmsEditionMenuTitle.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE, locale));

        cmsEditionMenu.add(cmsEditionMenuTitle);


        // Template edition menu "ul" node
        Element templateEditionMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        cmsEditionMenu.add(templateEditionMenuUl);


        // messages
        String previewRequired = this.getInternationalizationService().getString(InternationalizationConstants.KEY_PTITLE_PREVIEW_MODE_REQUIRED, locale);
        String publishRequired = this.getInternationalizationService().getString(InternationalizationConstants.KEY_PTITLE_PUBLISH_REQUIRED, locale);

        // ========== Switch edition mode on / off
        // ChangeCMSEditionModeCommand changeVersion;
        ChangeCMSEditionModeCommand changeEditionMode = null;
        String strChangeEditionMode = this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_DISPLAY_EDITION_MODE, locale);
        String cssChangeEditionMode = null;


        if (editionModeOn) {
            changeEditionMode = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_OFF);

            cssChangeEditionMode = HTMLConstants.CLASS_CHECK;
        } else {
            changeEditionMode = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_ON);

            cssChangeEditionMode = HTMLConstants.CLASS_UNCHECK;
        }


        if (modePreview) {

            String changeCmsEditionModeUrl = new PortalURLImpl(changeEditionMode, context, null, null).toString();

            Element cmsChangeEditionMode = new DOMElement(QName.get(HTMLConstants.A));
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.HREF), changeCmsEditionModeUrl);
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "m");
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.CLASS), cssChangeEditionMode);
            cmsChangeEditionMode.setText(strChangeEditionMode);
            this.addSubMenuElement(templateEditionMenuUl, cmsChangeEditionMode);

        } else {
            Element cmsChangeEditionMode = new DOMElement(QName.get(HTMLConstants.P));
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.CLASS), cssChangeEditionMode);
            cmsChangeEditionMode.setText(strChangeEditionMode);
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);

            this.addSubMenuElement(templateEditionMenuUl, cmsChangeEditionMode);
        }


        // HR
        this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        

        // ========== create new page

        // test si mode assistant activé
        if (modePreview) {
            cmsCtx.setDisplayLiveVersion("1");
        }

        String ecmBaseUrl = getCMSService().getEcmDomain(cmsCtx);

        Element cmsCreatePage = null;
        if (modePreview) {
            cmsCreatePage = new DOMElement(QName.get(HTMLConstants.A));

            String createPageUrl = getCMSService().getEcmUrl(cmsCtx, EcmCommand.createPage, path, requestParameters);
            cmsCreatePage.addAttribute(QName.get(HTMLConstants.HREF), createPageUrl);

            cmsCreatePage.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "n");
            cmsCreatePage.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);

            // prepare the callback url params
            // ============
            PortalControllerContext portalControllerContext = new PortalControllerContext(context);

            String closeUrl = this.urlFactory.getCMSUrl(portalControllerContext, null, "_NEWID_", null, null, "newPage", null, null, null, null);


            cmsCreatePage.addAttribute(QName.get(HTMLConstants.ONCLICK), "javascript:setCallbackFromEcmParams( '" + closeUrl + "' , '" + ecmBaseUrl + "');");
        } else {
            cmsCreatePage = new DOMElement(QName.get(HTMLConstants.P));
            cmsCreatePage.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }

        cmsCreatePage.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_CREATE, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsCreatePage);


        // ========== Edit current page
        Element cmsEditPage = null;
        if (modePreview) {
            String editPageUrl = getCMSService().getEcmUrl(cmsCtx, EcmCommand.editPage, path, requestParameters);

            cmsEditPage = new DOMElement(QName.get(HTMLConstants.A));
            cmsEditPage.addAttribute(QName.get(HTMLConstants.HREF), editPageUrl);
            cmsEditPage.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "e");
            cmsEditPage.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
            cmsEditPage.addAttribute(QName.get(HTMLConstants.ONCLICK), "javascript:setCallbackFromEcmParams( '' , '" + ecmBaseUrl + "');");
        } else {
            cmsEditPage = new DOMElement(QName.get(HTMLConstants.P));
            cmsEditPage.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }

        cmsEditPage.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_OPTIONS, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsEditPage);

        // ========== Publish document

        Element cmsPublishDoc = null;

        if (modePreview) {
            CMSPublishDocumentCommand publish = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CMSPublishDocumentCommand.PUBLISH);
            String publishURL = context.renderURL(publish, urlContext, URLFormat.newInstance(true, true));

            cmsPublishDoc = new DOMElement(QName.get(HTMLConstants.A));
            cmsPublishDoc.addAttribute(QName.get(HTMLConstants.HREF), publishURL);
        } else {
            cmsPublishDoc = new DOMElement(QName.get(HTMLConstants.P));
            cmsPublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }


        cmsPublishDoc.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_PUBLISH, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsPublishDoc);


        // ========== Unpublish document

        Element cmsUnpublishDoc = null;

        if (modePreview) {
            if (published) {
                CMSPublishDocumentCommand unpublish = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                        CMSPublishDocumentCommand.UNPUBLISH);
                String unpublishURL = context.renderURL(unpublish, urlContext, URLFormat.newInstance(true, true));

                cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.A));
                cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.HREF), unpublishURL);
            } else {
                cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.P));
                cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), publishRequired);
            }
        } else {
            cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.P));
            cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }


        cmsUnpublishDoc.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_UNPUBLISH, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsUnpublishDoc);


        // HR
        this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));


        // ========== sitemap
        Map<String, String> windowProps = new HashMap<String, String>();
        windowProps.put("osivia.cms.basePath", page.getProperty("osivia.cms.basePath"));
        Map<String, String> params = new HashMap<String, String>();

        String siteMapPopupURL = this.getUrlFactory().getStartPortletUrl(new PortalControllerContext(context),
                "osivia-portal-custom-web-assets-sitemapPortletInstance", windowProps, params, true);

        Element cmsViewSitemap = new DOMElement(QName.get(HTMLConstants.A));
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.HREF), siteMapPopupURL);
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
        cmsViewSitemap.setText(this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_SITEMAP, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsViewSitemap);

    }


    private void generateAdministrationToggleVersion(ControllerContext context, Page page, Element administration) throws Exception {
        Locale locale = context.getServerInvocation().getRequest().getLocale();

        boolean modePreview = CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW);
        String editionMode = CmsPermissionHelper.getCurrentCmsEditionMode(context);


        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSItem liveDoc = getCMSService().getContent(cmsCtx, pagePath);

        String path = liveDoc.getPath();


        // ---------------------
        String toggleTitle = this.getInternationalizationService().getString(InternationalizationConstants.KEY_PTITLE_TOGGLE_VERSION, locale);
        String previewTxt = this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_PREVIEW, locale);
        String onlineTxt = this.getInternationalizationService().getString(InternationalizationConstants.KEY_CMS_PAGE_ONLINE, locale);

        ChangeCMSEditionModeCommand changeVersion;
        Element cmsToggleVersion = new DOMElement(QName.get(HTMLConstants.A));


        Element cmsToggleBtn1 = new DOMElement(QName.get(HTMLConstants.SPAN));
        cmsToggleVersion.add(cmsToggleBtn1);
        Element cmsToggleBtn2 = new DOMElement(QName.get(HTMLConstants.SPAN));



        if (modePreview) {
            cmsToggleVersion.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_PREVIEW);
            cmsToggleVersion.addAttribute(QName.get(HTMLConstants.TITLE), toggleTitle.concat(onlineTxt));

            cmsToggleBtn1.setText(previewTxt);
            cmsToggleBtn1.add(cmsToggleBtn2);
            cmsToggleBtn2.setText(StringUtils.EMPTY);
            changeVersion = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CmsPermissionHelper.CMS_VERSION_ONLINE,
                    editionMode);

        } else {
            cmsToggleVersion.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_ONLINE);
            cmsToggleVersion.addAttribute(QName.get(HTMLConstants.TITLE), toggleTitle.concat(previewTxt));

            cmsToggleBtn1.add(cmsToggleBtn2);
            cmsToggleBtn1.setText(onlineTxt);
            cmsToggleBtn2.setText(StringUtils.EMPTY);
            changeVersion = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                    CmsPermissionHelper.CMS_VERSION_PREVIEW,
                    editionMode);

        }

        String changeCmsVersionUrl = new PortalURLImpl(changeVersion, context, null, null).toString();
        cmsToggleVersion.addAttribute(QName.get(HTMLConstants.HREF), changeCmsVersionUrl);

        administration.add(cmsToggleVersion);
    }

    /**
     * Add sub-menu Fancybox link.
     *
     * @param ul current "ul" element
     * @param url Fancybox "div" identifier
     * @param title link text and Fancybox title value
     */
    private void addSubMenuFancyboxLink(Element ul, String url, String title) {
        Element element = new DOMElement(QName.get(HTMLConstants.A));
        element.addAttribute(QName.get(HTMLConstants.HREF), url);
        element.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYBOX_INLINE);
        element.addAttribute(QName.get(HTMLConstants.TITLE), title);
        element.setText(title);
        this.addSubMenuElement(ul, element);
    }

    /**
     * Add sub-menu element.
     *
     * @param ul current "ul" element
     * @param element element to add
     */
    private void addSubMenuElement(Element ul, Element element) {
        Element li = new DOMElement(QName.get(HTMLConstants.LI));
        li.add(element);
        ul.add(li);
    }


    /**
     * Toolbar settings injection.
     *
     * @param command page command
     * @return HTML content
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String injectToolbarSettings(PageCommand command) throws Exception {
        ControllerContext context = command.getControllerContext();
        ControllerRequestDispatcher dispatcher = context.getRequestDispatcher(this.getTargetSettingsContextPath(), this.toolbarSettingsPath);

        if (dispatcher != null) {
            Page page = command.getPage();

            if (PageCustomizerInterceptor.isAdministrator(context)) {
                try {
                    PageType pageType = PageType.getPageType(page, context);
                    Boolean templated = pageType.isTemplated();
                    if (PageType.CMS_TEMPLATED_PAGE.equals(pageType)) {
                        page = (Page) page.getParent();
                        pageType = PageType.getPageType(page, context);
                    }

                    URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

                    // Internationalization service
                    dispatcher.setAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE, this.getInternationalizationService());

                    // Formatter
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER, this);

                    // Controller context
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT, context);

                    // Generic command URL
                    String serverContext = context.getServerInvocation().getServerContext().getPortalContextPath();
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL, serverContext + "/commands");

                    // Page
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE, page);

                    // CMS templated indicator
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_TEMPLATED, templated);

                    if (!PageType.DYNAMIC_PAGE.equals(pageType)) {
                        // Draft page indicator
                        Boolean draftPage = "1".equals(page.getDeclaredProperty("osivia.draftPage"));
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE, draftPage);

                        // Layouts
                        List<PortalLayout> layouts = new ArrayList<PortalLayout>(this.getLayoutService().getLayouts());
                        Collections.sort(layouts, new PortalLayoutComparator());
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST, layouts);

                        // Current layout
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT,
                                page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT));

                        // Themes
                        List<PortalTheme> themes = new ArrayList<PortalTheme>(this.getThemeService().getThemes());
                        Collections.sort(themes, new PortalThemeComparator());
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST, themes);

                        // Current theme
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME,
                                page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME));

                        // Roles
                        List<Role> roles = this.getProfilManager().getFilteredRoles();
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_ROLES, roles);

                        // Actions for roles
                        DomainConfigurator domainConfigurator = this.getAuthorizationDomainRegistry().getDomain("portalobject").getConfigurator();
                        Set<RoleSecurityBinding> constraints = domainConfigurator.getSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT));
                        Map<String, Set<String>> actionsForRoles = new HashMap<String, Set<String>>();
                        if (CollectionUtils.isNotEmpty(constraints)) {
                            for (RoleSecurityBinding roleSecurityBinding : constraints) {
                                actionsForRoles.put(roleSecurityBinding.getRoleName(), roleSecurityBinding.getActions());
                            }
                        }
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_ACTIONS_FOR_ROLES, actionsForRoles);

                        // Page suppression
                        DeletePageCommand deletePageCommand = new DeletePageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                        String deletePageCommandUrl = context.renderURL(deletePageCommand, urlContext, URLFormat.newInstance(true, true));
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DELETE_PAGE_COMMAND_URL, deletePageCommandUrl);

                        // CMS scope select
                        String scope = page.getDeclaredProperty("osivia.cms.scope");
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_SCOPE_SELECT, this.formatScopeList(page, "scope", scope));

                        // CMS display live version
                        CMSServiceCtx cmsServiceCtx = new CMSServiceCtx();
                        cmsServiceCtx.setControllerContext(context);
                        String displayLiveVersion = page.getDeclaredProperty("osivia.cms.displayLiveVersion");
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_DISPLAY_LIVE_VERSION,
                                this.formatDisplayLiveVersionList(cmsServiceCtx, page, "displayLiveVersion", displayLiveVersion));

                        // CMS recontextualization support
                        String outgoingRecontextualizationSupport = page.getDeclaredProperty("osivia.cms.outgoingRecontextualizationSupport");
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_RECONTEXTUALIZATION_SUPPORT, this.formatInheritedCheckVakue(page,
                                "outgoingRecontextualizationSupport", "osivia.cms.outgoingRecontextualizationSupport", outgoingRecontextualizationSupport));

                        // CMS base path
                        String pageCmsBasePath = page.getDeclaredProperty("osivia.cms.basePath");
                        if (pageCmsBasePath == null) {
                            pageCmsBasePath = StringUtils.EMPTY;
                        }
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CMS_BASE_PATH, pageCmsBasePath);
                    }
                } catch (PortalSecurityException e) {
                    logger.error(StringUtils.EMPTY, e);
                }

                dispatcher.include();
                return dispatcher.getMarkup();
            }
        }

        return null;
    }


    private String formatInheritedCheckVakue(PortalObject po, String selectName, String propertyName, String selectedValue) throws Exception {
        Map<String, String> supportedValue = new LinkedHashMap<String, String>();

        supportedValue.put("0", "Non");
        supportedValue.put("1", "Oui");


        StringBuffer select = new StringBuffer();

        String disabled = "";
        if (StringUtils.isNotEmpty(po.getDeclaredProperty("osivia.cms.basePath"))) {
            disabled = "disabled='disabled'";
        }

        select.append("<select name=\"" + selectName + "\"" + disabled + ">");

        if (!supportedValue.isEmpty()) {

            /* Héritage */

            String parentScope = po.getParent().getProperty(propertyName);
            String inheritedLabel = null;
            if (parentScope != null) {
                inheritedLabel = supportedValue.get(parentScope);
            }
            ;
            if (inheritedLabel == null) {
                inheritedLabel = "Non";
            }
            inheritedLabel = "Herité [" + inheritedLabel + "]";


            if ((selectedValue == null) || (selectedValue.length() == 0)) {

                select.append("<option selected=\"selected\" value=\"\">" + inheritedLabel + "</option>");

            } else {

                select.append("<option value=\"\">" + inheritedLabel + "</option>");

            }
            for (Entry<String, String> entry : supportedValue.entrySet()) {
                if ((selectedValue != null) && (selectedValue.length() != 0) && selectedValue.equals(entry.getKey())) {
                    select.append("<option selected=\"selected\" value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
                } else {
                    select.append("<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>");
                }
            }
        }

        select.append("</select>");

        return select.toString();
    }


    /**
     * Utility method used to access the target theme context path.
     *
     * @param portalCommand command
     * @return the target theme context path
     */
    private static String getTargetThemeContextPath(PortalCommand portalCommand) {
        Page page = null;
        if (portalCommand instanceof PageCommand) {
            page = ((PageCommand) portalCommand).getPage();
        }

        if (portalCommand instanceof WindowCommand) {
            page = ((WindowCommand) portalCommand).getPage();
        }
        if (page == null) {
            throw new IllegalArgumentException("target path not accessible");
        }

        String themeId = page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
        PageService pageService = portalCommand.getControllerContext().getController().getPageService();
        ThemeService themeService = pageService.getThemeService();
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getContextPath();
    }


    /**
     * Getter for toolbarPath.
     *
     * @return the toolbarPath
     */
    public String getToolbarPath() {
        return this.toolbarPath;
    }

    /**
     * Setter for toolbarPath.
     *
     * @param toolbarPath the toolbarPath to set
     */
    public void setToolbarPath(String toolbarPath) {
        this.toolbarPath = toolbarPath;
    }

    /**
     * Getter for toolbarSettingsPath.
     *
     * @return the toolbarSettingsPath
     */
    public String getToolbarSettingsPath() {
        return this.toolbarSettingsPath;
    }

    /**
     * Setter for toolbarSettingsPath.
     *
     * @param toolbarSettingsPath the toolbarSettingsPath to set
     */
    public void setToolbarSettingsPath(String toolbarSettingsPath) {
        this.toolbarSettingsPath = toolbarSettingsPath;
    }

    /**
     * Getter for urlFactory.
     *
     * @return the urlFactory
     */
    public IPortalUrlFactory getUrlFactory() {
        return this.urlFactory;
    }

    /**
     * Setter for urlFactory.
     *
     * @param urlFactory the urlFactory to set
     */
    public void setUrlFactory(IPortalUrlFactory urlFactory) {
        this.urlFactory = urlFactory;
    }

    /**
     * Getter for targetSettingsContextPath.
     *
     * @return the targetSettingsContextPath
     */
    public String getTargetSettingsContextPath() {
        return this.targetSettingsContextPath;
    }

    /**
     * Setter for targetSettingsContextPath.
     *
     * @param targetSettingsContextPath the targetSettingsContextPath to set
     */
    public void setTargetSettingsContextPath(String targetSettingsContextPath) {
        this.targetSettingsContextPath = targetSettingsContextPath;
    }

}
