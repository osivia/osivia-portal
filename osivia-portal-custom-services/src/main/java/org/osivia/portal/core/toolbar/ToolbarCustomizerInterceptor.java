package org.osivia.portal.core.toolbar;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerRequestDispatcher;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.portal.Page;
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

/**
 * Toolbar customizer interceptor.
 *
 * @see ControllerInterceptor
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
    /** Mode normal. */
    private static final String MODE_NORMAL = "normal";
    /** Mode wizard. */
    private static final String MODE_WIZARD = "wizzard";
    /** Mode admin. */
    private static final String MODE_ADMIN = "admin";

    // TODO Constantes dans internal constantes ?

    /** HTML class "toolbar-menu". */
    private static final String HTML_CLASS_TOOLBAR_MENU = "toolbar-menu";

    /** HTML class "toolbar-menu-title". */
    private static final String HTML_CLASS_TOOLBAR_MENU_TITLE = "toolbar-menu-title";
    private static final String CLASSES_PREVIEW = "preview-version";
    private static final String CLASSES_ONLINE = "online-version";

    /** HTML class "fancybox_inline". */
    private static final String HTML_CLASS_FANCYBOX_INLINE = "fancybox_inline";
    /** HTML class "fancybox_refresh". */
    private static final String HTML_CLASS_FANCYFRAME_REFRESH = "fancyframe_refresh";

    /** Pages list URL. */
    private static final String URL_PAGES_LIST = "#pages-list";
    /** Page creation URL. */
    private static final String URL_PAGE_CREATION = "#page-creation";
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


    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


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

            PortalObject portalObject = renderPageCommand.getPage().getPortal();
            boolean admin = MODE_ADMIN.equalsIgnoreCase(portalObject.getName());

            PortalObjectId popupWindowId = (PortalObjectId) command.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                    "osivia.popupModeWindowID");


            // Toolbar must not be loaded :
            // - in JBoss portal administration
            // - in popup mode
            if (!admin && (popupWindowId == null)) {
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

        Locale locale = context.getServerInvocation().getRequest().getLocale();

        // Administration root element
        Element administration = new DOMElement(QName.get(HTMLConstants.DIV));

        if (PageCustomizerInterceptor.isAdministrator(context)) {
            // Shortcuts menu root element
            Element shortcutsMenu = new DOMElement(QName.get(HTMLConstants.DIV));
            shortcutsMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
            administration.add(shortcutsMenu);

            // Shortcuts menu title
            Element shortcutsMenuTitle = new DOMElement(QName.get(HTMLConstants.A));
            shortcutsMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
            shortcutsMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_SHORTCUTS_MENU_TITLE, locale));
            shortcutsMenu.add(shortcutsMenuTitle);

            // Shortcuts menu "ul" node
            Element shortcutsMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
            shortcutsMenu.add(shortcutsMenuUl);

            // Home
            Element home = new DOMElement(QName.get(HTMLConstants.A));
            home.addAttribute(QName.get(HTMLConstants.HREF), "/portal");
            home.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_HOME, locale));
            this.addSubMenuElement(shortcutsMenuUl, home);

            // OSIVIA Portal administration
            PortalControllerContext portalControllerContext = new PortalControllerContext(context);
            String osiviaAdministrationUrl = this.urlFactory.getStartPortletUrl(portalControllerContext,
                    InternalConstants.PORTLET_ADMINISTRATION_INSTANCE_NAME, null, null, true);

            Element osiviaAdministration = new DOMElement(QName.get(HTMLConstants.A));
            osiviaAdministration.addAttribute(QName.get(HTMLConstants.HREF), osiviaAdministrationUrl);
            osiviaAdministration.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYFRAME);
            osiviaAdministration.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_OSIVIA_ADMINISTRATION, locale));
            this.addSubMenuElement(shortcutsMenuUl, osiviaAdministration);

            // JBoss administration
            ViewPageCommand jbossAdministrationCommand = new ViewPageCommand(adminPortalId);
            String jbossAdministrationUrl = new PortalURLImpl(jbossAdministrationCommand, context, null, null).toString();

            Element jbossAdministration = new DOMElement(QName.get(HTMLConstants.A));
            jbossAdministration.addAttribute(QName.get(HTMLConstants.HREF), jbossAdministrationUrl);
            jbossAdministration.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_JBOSS_ADMINISTRATION, locale));
            this.addSubMenuElement(shortcutsMenuUl, jbossAdministration);

            // Pages list
            this.addSubMenuFancyboxLink(shortcutsMenuUl, URL_PAGES_LIST,
                    this.internationalizationService.getString(InternationalizationConstants.KEY_PAGES_LIST, locale));
            
            // Page portail (TEST JSS uniquuement en mode PORTAL)
            if( InternalConstants.PORTAL_TYPE_STATIC_PORTAL.equals(page.getPortal().getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE)))  {
                this.addSubMenuFancyboxLink(shortcutsMenuUl, URL_PAGE_CREATION,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_CREATION, locale));
                
            }


            // Template edition menu root element
            Element templateEditionMenu = new DOMElement(QName.get(HTMLConstants.DIV));
            templateEditionMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
            administration.add(templateEditionMenu);

            // Template edition menu title
            Element templateEditionMenuTitle = new DOMElement(QName.get(HTMLConstants.A));
            templateEditionMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
            templateEditionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_TEMPLATE_EDITION_MENU_TITLE, locale));
            templateEditionMenu.add(templateEditionMenuTitle);

            // Template edition menu "ul" node
            Element templateEditionMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
            templateEditionMenu.add(templateEditionMenuUl);

            // Icons display
            String mode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_WINDOWS_SETTING_MODE);
            ChangeModeCommand changeModeCommand;
            String modeHtmlClass;
            if (MODE_WIZARD.equals(mode)) {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), MODE_NORMAL);
                modeHtmlClass = HTMLConstants.CLASS_CHECK;
            } else {
                changeModeCommand = new ChangeModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), MODE_WIZARD);
                modeHtmlClass = HTMLConstants.CLASS_UNCHECK;
            }
            String changeModeUrl = new PortalURLImpl(changeModeCommand, context, null, null).toString();

            Element iconsDisplay = new DOMElement(QName.get(HTMLConstants.A));
            iconsDisplay.addAttribute(QName.get(HTMLConstants.HREF), changeModeUrl);
            iconsDisplay.addAttribute(QName.get(HTMLConstants.CLASS), modeHtmlClass);
            iconsDisplay.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_ICONS_DISPLAY, locale));
            this.addSubMenuElement(templateEditionMenuUl, iconsDisplay);

            // HR
            this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

            // Page creation
            this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_CREATION,
                    this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_CREATION, locale));

            // Page template access
            if (pageType.isTemplated()) {
                ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
                ViewPageCommand pageTemplateAccessCommand = new ViewPageCommand(templatePortalObject.getTemplate().getId());
                String pageTemplateAccessUrl = new PortalURLImpl(pageTemplateAccessCommand, context, null, null).toString();
                pageTemplateAccessUrl += "?init-state=true&edit-template-mode=true";

                this.addSubMenuFancyboxLink(templateEditionMenuUl, pageTemplateAccessUrl,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS, locale));
            }

            if (pageType.isEditable()) {
                // Page suppression
                this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_SUPPRESSION,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_SUPPRESSION, locale));

                // Page location
                this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_LOCATION,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_LOCATION, locale));

                // HR
                this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

                // Page properties
                if (!pageType.isTemplated()) {
                    this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_PROPERTIES,
                            this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_PROPERTIES, locale));
                }

                // Page CMS
                this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_CMS,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_CMS, locale));

                // Page rights
                this.addSubMenuFancyboxLink(templateEditionMenuUl, URL_PAGE_RIGHTS,
                        this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_RIGHTS, locale));
            }

            // HR
            this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

            // Caches initialization
            ViewPageCommand cachesInitializationCommand = new ViewPageCommand(page.getId());
            String cachesInitializationUrl = new PortalURLImpl(cachesInitializationCommand, context, null, null).toString();
            cachesInitializationUrl += "?init-cache=true";

            Element cachesInitialization = new DOMElement(QName.get(HTMLConstants.A));
            cachesInitialization.addAttribute(QName.get(HTMLConstants.HREF), cachesInitializationUrl);
            cachesInitialization.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CACHES_INITIALIZATION, locale));
            this.addSubMenuElement(templateEditionMenuUl, cachesInitialization);
        }


        if (CMSEditionPageCustomizerInterceptor.checkWritePermission(context, page) && CMSEditionPageCustomizerInterceptor.checkWebPagePermission(context, page)) {
            // CMS edition menu root element
            Element cmsEditionMenu = new DOMElement(QName.get(HTMLConstants.DIV));
            cmsEditionMenu.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU);
            administration.add(cmsEditionMenu);

            this.formatHtmlCmsMenu(context, page, locale, cmsEditionMenu);
        }


        String htmlData = this.writeHtmlData(administration);
        return htmlData;
    }

    /**
     * Utility method used to generate cms menu in HTML content.
     *
     * @param context controller context
     * @param page current page
     * @param locale current locale
     * @param cmsEditionMenu the menu object
     * @return HTML data
     * @throws Exception
     */
    private void formatHtmlCmsMenu(ControllerContext context, Page page, Locale locale, Element cmsEditionMenu) throws Exception {

        String version = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION);
        String editionMode = (String) context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_EDITION_MODE);
        URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

        // CMS edition menu title
        // Element cmsDocumentIcon = new DOMElement(QName.get(HtmlConstants.IMG));
        Element cmsDocumentSpan = new DOMElement(QName.get(HTMLConstants.SPAN));
        Element cmsEditionMenuTitle = new DOMElement(QName.get(HTMLConstants.A));

        cmsEditionMenuTitle.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_TOOLBAR_MENU_TITLE);
        cmsEditionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE, locale));

        // cmsEditionMenuTitle.add(cmsDocumentIcon);


        if (InternalConstants.CMS_VERSION_PREVIEW.equals(version)) {
            // cmsDocumentIcon.addAttribute(HtmlConstants.SRC, "/osivia-portal-custom-web-assets/images/icone_doc_travail2.gif");
            cmsDocumentSpan.addAttribute(QName.get(HTMLConstants.CLASS), CLASSES_PREVIEW);
            cmsDocumentSpan.setText(HTMLConstants.TEXT_DEFAULT.concat(this.internationalizationService.getString(
                    InternationalizationConstants.KEY_CMS_PAGE_PREVIEW, locale)));
        } else {
            // cmsDocumentIcon.addAttribute(HtmlConstants.SRC, "/osivia-portal-custom-web-assets/images/icone_doc_valide2.gif");
            cmsDocumentSpan.addAttribute(QName.get(HTMLConstants.CLASS), CLASSES_ONLINE);
            cmsDocumentSpan.setText(HTMLConstants.TEXT_DEFAULT.concat(this.internationalizationService.getString(
                    InternationalizationConstants.KEY_CMS_PAGE_ONLINE, locale)));
        }


        cmsEditionMenu.add(cmsEditionMenuTitle);
        cmsEditionMenu.add(cmsDocumentSpan);


        // document state icon


        // Template edition menu "ul" node
        Element templateEditionMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        cmsEditionMenu.add(templateEditionMenuUl);

        // CMS icons display


        // ========== Switch live / online version

        ChangeCMSEditionModeCommand changeVersion;
        ChangeCMSEditionModeCommand changeEditionMode = null;
        String strChangeVersion = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_DISPLAY_LIVE_VERSION, locale);
        String strChangeEditionMode = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_DISPLAY_EDITION_MODE, locale);
        String cssChangeVersion, cssChangeEditionMode = null;


        if (InternalConstants.CMS_VERSION_PREVIEW.equals(version)) {
            changeVersion = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), InternalConstants.CMS_VERSION_ONLINE,
                    InternalConstants.CMS_EDITION_MODE_OFF);
            //strChangeVersion = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_DISPLAY_LIVE_VERSION, locale);
            cssChangeVersion = HTMLConstants.CLASS_CHECK;

            if (InternalConstants.CMS_EDITION_MODE_ON.equals(editionMode)) {
                changeEditionMode = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                        InternalConstants.CMS_VERSION_PREVIEW, InternalConstants.CMS_EDITION_MODE_OFF);
                // strChangeEditionMode = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_HIDE_EDITION_MODE, locale);
                cssChangeEditionMode = HTMLConstants.CLASS_CHECK;
            } else {
                changeEditionMode = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT),
                        InternalConstants.CMS_VERSION_PREVIEW, InternalConstants.CMS_EDITION_MODE_ON);
                // strChangeEditionMode = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_DISPLAY_EDITION_MODE, locale);
                cssChangeEditionMode = HTMLConstants.CLASS_UNCHECK;
            }
        } else {
            changeVersion = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), InternalConstants.CMS_VERSION_PREVIEW,
                    InternalConstants.CMS_EDITION_MODE_OFF);
            //strChangeVersion = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_DISPLAY_LIVE_VERSION, locale);
            cssChangeVersion = HTMLConstants.CLASS_UNCHECK;
        }


        String changeCmsVersionUrl = new PortalURLImpl(changeVersion, context, null, null).toString();

        Element cmsChangeVersion = new DOMElement(QName.get(HTMLConstants.A));
        cmsChangeVersion.addAttribute(QName.get(HTMLConstants.HREF), changeCmsVersionUrl);
        cmsChangeVersion.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "p");
        cmsChangeVersion.addAttribute(QName.get(HTMLConstants.CLASS), cssChangeVersion);
        cmsChangeVersion.setText(strChangeVersion);
        this.addSubMenuElement(templateEditionMenuUl, cmsChangeVersion);

        if (changeEditionMode != null) {

            String changeCmsEditionModeUrl = new PortalURLImpl(changeEditionMode, context, null, null).toString();

            Element cmsChangeEditionMode = new DOMElement(QName.get(HTMLConstants.A));
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.HREF), changeCmsEditionModeUrl);
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "m");
            cmsChangeEditionMode.addAttribute(QName.get(HTMLConstants.CLASS), cssChangeEditionMode);
            cmsChangeEditionMode.setText(strChangeEditionMode);
            this.addSubMenuElement(templateEditionMenuUl, cmsChangeEditionMode);
        }


        // HR
        this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        // ========== create / modify doc

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());

        // test si mode assistant activé
        if (InternalConstants.CMS_VERSION_PREVIEW.equals(context.getAttribute(ControllerCommand.SESSION_SCOPE, InternalConstants.ATTR_TOOLBAR_CMS_VERSION))) {
            cmsCtx.setDisplayLiveVersion("1");
        }

        Map<String, String> requestParameters = new HashMap<String, String>();

        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        CMSItem liveDoc = getCMSService().getContent(cmsCtx, pagePath);

        String path = liveDoc.getPath();
        String createPageUrl = getCMSService().getEcmUrl(cmsCtx, EcmCommand.createPage, path, requestParameters);

        Element cmsCreatePage = new DOMElement(QName.get(HTMLConstants.A));
        cmsCreatePage.addAttribute(QName.get(HTMLConstants.HREF), createPageUrl);
        cmsCreatePage.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "n");
        cmsCreatePage.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
        cmsCreatePage.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_CREATE, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsCreatePage);

        String editPageUrl = getCMSService().getEcmUrl(cmsCtx, EcmCommand.editPage, path, requestParameters);

        Element cmsEditPage = new DOMElement(QName.get(HTMLConstants.A));
        cmsEditPage.addAttribute(QName.get(HTMLConstants.HREF), editPageUrl);
        cmsEditPage.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "e");
        cmsEditPage.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
        cmsEditPage.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_OPTIONS, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsEditPage);

        // Publish document
        
        CMSPublishDocumentCommand publish = new CMSPublishDocumentCommand(page.getId()
                .toString(PortalObjectPath.SAFEST_FORMAT), path);
        String publishURL = context.renderURL(publish, urlContext, URLFormat.newInstance(true, true));

        Element cmsPublishDoc = new DOMElement(QName.get(HTMLConstants.A));
        cmsPublishDoc.addAttribute(QName.get(HTMLConstants.HREF), publishURL);
        cmsPublishDoc.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_PUBLISH, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsPublishDoc);

        // HR
        this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        // ========== to ECM....

        /*
         * // Link "to ECM disabled
         * String ecmUrl = getCMSService().getEcmUrl(cmsCtx, EcmCommand.viewSummary, path, requestParameters);
         *
         * Element cmsViewEcm = new DOMElement(QName.get(HtmlConstants.A));
         * cmsViewEcm.addAttribute(QName.get(HtmlConstants.HREF), ecmUrl);
         * cmsViewEcm.addAttribute(QName.get(HtmlConstants.ACCESSKEY), "b");
         * cmsViewEcm.addAttribute(QName.get(HtmlConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
         * cmsViewEcm.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_TO_ECM, locale));
         * this.addSubMenuElement(templateEditionMenuUl, cmsViewEcm);
         */

        // ========== sitemap
        // TODO do not use basePath
        Map<String, String> windowProps = new HashMap<String, String>();
        windowProps.put("osivia.cms.basePath", page.getProperty("osivia.cms.basePath"));
        Map<String, String> params = new HashMap<String, String>();

        String siteMapPopupURL = this.getUrlFactory().getStartPortletUrl(new PortalControllerContext(context),
                "osivia-portal-custom-web-assets-sitemapPortletInstance", windowProps, params, true);

        Element cmsViewSitemap = new DOMElement(QName.get(HTMLConstants.A));
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.HREF), siteMapPopupURL);
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
        cmsViewSitemap.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_SITEMAP, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsViewSitemap);
    }

    /**
     * Add sub-menu Fancybox link
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
                    if (PageType.NON_DEFAULT_TEMPLATE_CMS_ROOT_PAGE.equals(pageType)) {
                        page = (Page) page.getParent();
                    }

                    URLContext urlContext = context.getServerInvocation().getServerContext().getURLContext();

                    // Internationalization service
                    dispatcher.setAttribute(InternalConstants.ATTR_INTERNATIONALIZATION_SERVICE, this.internationalizationService);

                    // Formatter
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_FORMATTER, this);

                    // Controller context
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CONTROLLER_CONTEXT, context);

                    // Generic command URL
                    String serverContext = context.getServerInvocation().getServerContext().getPortalContextPath();
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_COMMAND_URL, serverContext + "/commands");

                    // Page
                    dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_PAGE, page);

                    if (pageType.isEditable()) {
                        // Default page indicator
                        Boolean defaultPage = page.getName().equals(page.getPortal().getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME));
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DEFAULT_PAGE, defaultPage);

                        if (!pageType.isTemplated()) {
                            // Draft page indicator
                            Boolean draftPage = "1".equals(page.getDeclaredProperty("osivia.draftPage"));
                            dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_DRAFT_PAGE, draftPage);

                            // Layouts
                            List<PortalLayout> layouts = new ArrayList<PortalLayout>(this.layoutService.getLayouts());
                            Collections.sort(layouts, new PortalLayoutComparator());
                            dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_LAYOUTS_LIST, layouts);

                            // Current layout
                            dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_LAYOUT,
                                    page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT));

                            // Themes
                            List<PortalTheme> themes = new ArrayList<PortalTheme>(this.themeService.getThemes());
                            Collections.sort(themes, new PortalThemeComparator());
                            dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_THEMES_LIST, themes);

                            // Current theme
                            dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_CURRENT_THEME,
                                    page.getDeclaredProperty(ThemeConstants.PORTAL_PROP_THEME));
                        }

                        // Roles
                        List<Role> roles = this.profilManager.getFilteredRoles();
                        dispatcher.setAttribute(InternalConstants.ATTR_TOOLBAR_SETTINGS_ROLES, roles);

                        // Actions for roles
                        DomainConfigurator domainConfigurator = this.authorizationDomainRegistry.getDomain("portalobject").getConfigurator();
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
