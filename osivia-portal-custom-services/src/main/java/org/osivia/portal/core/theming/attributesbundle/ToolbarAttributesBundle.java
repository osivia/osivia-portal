package org.osivia.portal.core.theming.attributesbundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.HTMLConstants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.assistantpage.CMSDeleteDocumentCommand;
import org.osivia.portal.core.assistantpage.CMSEditionPageCustomizerInterceptor;
import org.osivia.portal.core.assistantpage.CMSPublishDocumentCommand;
import org.osivia.portal.core.assistantpage.ChangeCMSEditionModeCommand;
import org.osivia.portal.core.assistantpage.ChangeModeCommand;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSObjectPath;
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
 * Toolbar attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public final class ToolbarAttributesBundle implements IAttributesBundle {

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

    /** Singleton instance. */
    private static ToolbarAttributesBundle instance;


    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service. */
    private final ICMSService cmsService;

    /** Administration portal identifier. */
    private final PortalObjectId adminPortalId;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private ToolbarAttributesBundle() {
        super();

        // Internationalization service
        this.internationalizationService = Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // CMS service
        ICMSServiceLocator cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        this.cmsService = cmsServiceLocator.getCMSService();

        this.adminPortalId = PortalObjectId.parse("/admin", PortalObjectPath.CANONICAL_FORMAT);

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_TOOLBAR_PRINCIPAL);
        this.names.add(Constants.ATTR_TOOLBAR_LOGIN_URL);
        this.names.add(Constants.ATTR_TOOLBAR_MY_SPACE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT);
        this.names.add(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL);
        this.names.add(Constants.ATTR_TOOLBAR_SIGN_OUT_URL);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static ToolbarAttributesBundle getInstance() {
        if (instance == null) {
            instance = new ToolbarAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) {
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        ServerInvocationContext serverContext = controllerContext.getServerInvocation().getServerContext();
        Page page = renderPageCommand.getPage();

        // Principal
        Principal principal = serverContext.getClientRequest().getUserPrincipal();
        attributes.put(Constants.ATTR_TOOLBAR_PRINCIPAL, principal);

        // My space
        MonEspaceCommand mySpaceCommand = new MonEspaceCommand();
        PortalURL mySpacePortalUrl = new PortalURLImpl(mySpaceCommand, controllerContext, true, null);
        if (principal == null) {
            attributes.put(Constants.ATTR_TOOLBAR_LOGIN_URL, mySpacePortalUrl.toString());
        } else {
            attributes.put(Constants.ATTR_TOOLBAR_MY_SPACE_URL, mySpacePortalUrl.toString());
        }

        // Administration content
        String administrationContent = this.formatHTMLAdministration(controllerContext, page);
        attributes.put(Constants.ATTR_TOOLBAR_ADMINISTRATION_CONTENT, administrationContent);

        // Refresh page
        RefreshPageCommand refreshPageCommand = new RefreshPageCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT));
        PortalURL refreshPagePortalUrl = new PortalURLImpl(refreshPageCommand, controllerContext, false, null);
        attributes.put(Constants.ATTR_TOOLBAR_REFRESH_PAGE_URL, refreshPagePortalUrl.toString());

        // Logout
        SignOutCommand signOutCommand = new SignOutCommand();
        PortalURL signOutPortalUrl = new PortalURLImpl(signOutCommand, controllerContext, false, null);
        attributes.put(Constants.ATTR_TOOLBAR_SIGN_OUT_URL, signOutPortalUrl.toString());
    }


    /**
     * Utility method used to generate administration HTML content.
     *
     * @param context controller context
     * @param page current page
     * @return HTML data
     * @throws Exception
     */
    private String formatHTMLAdministration(ControllerContext context, Page page) {
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

        try {
            if (CMSEditionPageCustomizerInterceptor.checkWritePermission(context, page)
                    && CMSEditionPageCustomizerInterceptor.checkWebPagePermission(context, page)) {
                // Web page menu
                this.generateAdministrationWebPageMenu(context, page, administration);
                this.generateAdministrationToggleVersion(context, page, administration);
            }
        } catch (Exception e) {
            // Do nothing
        }

        return administration.asXML();
    }


    /**
     * Utility method used to generate configuration menu for administration toolbar.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     */
    private void generateAdministrationConfigurationMenu(ControllerContext context, Page page, Element administration) {
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
        configurationMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CONFIGURATION_MENU_TITLE, locale));
        configurationMenu.add(configurationMenuTitle);

        // Configuration menu "ul" node
        Element configurationMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        configurationMenu.add(configurationMenuUl);

        // Home
        Element home = new DOMElement(QName.get(HTMLConstants.A));
        home.addAttribute(QName.get(HTMLConstants.HREF), context.getServerInvocation().getServerContext().getPortalContextPath());
        home.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_HOME, locale));
        this.addSubMenuElement(configurationMenuUl, home);

        // OSIVIA Portal administration
        PortalControllerContext portalControllerContext = new PortalControllerContext(context);
        String osiviaAdministrationUrl = StringUtils.EMPTY;
        try {
            osiviaAdministrationUrl = this.urlFactory.getStartPortletUrl(portalControllerContext, InternalConstants.PORTLET_ADMINISTRATION_INSTANCE_NAME, null,
                    null, true);
        } catch (Exception e) {
            // Do nothing
        }

        Element osiviaAdministration = new DOMElement(QName.get(HTMLConstants.A));
        osiviaAdministration.addAttribute(QName.get(HTMLConstants.HREF), osiviaAdministrationUrl);
        osiviaAdministration.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYFRAME);
        osiviaAdministration.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_OSIVIA_ADMINISTRATION, locale));
        this.addSubMenuElement(configurationMenuUl, osiviaAdministration);

        // JBoss administration
        ViewPageCommand jbossAdministrationCommand = new ViewPageCommand(this.adminPortalId);
        String jbossAdministrationUrl = new PortalURLImpl(jbossAdministrationCommand, context, null, null).toString();

        Element jbossAdministration = new DOMElement(QName.get(HTMLConstants.A));
        jbossAdministration.addAttribute(QName.get(HTMLConstants.HREF), jbossAdministrationUrl);
        jbossAdministration.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_JBOSS_ADMINISTRATION, locale));
        this.addSubMenuElement(configurationMenuUl, jbossAdministration);

        // Pages list
        this.addSubMenuFancyboxLink(configurationMenuUl, URL_PAGES_LIST,
                this.internationalizationService.getString(InternationalizationConstants.KEY_PAGES_LIST, locale));

        // HR
        this.addSubMenuElement(configurationMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        if (InternalConstants.PORTAL_TYPE_STATIC_PORTAL.equals(page.getPortal().getDeclaredProperty(InternalConstants.PORTAL_PROP_NAME_PORTAL_TYPE))) {
            // Page creation
            this.addSubMenuFancyboxLink(configurationMenuUl, URL_PAGE_CREATION,
                    this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_CREATION, locale));
        }

        // Template creation
        this.addSubMenuFancyboxLink(configurationMenuUl, URL_TEMPLATE_CREATION,
                this.internationalizationService.getString(InternationalizationConstants.KEY_TEMPLATE_CREATION, locale));

        // Page template access
        if (pageType.isTemplated()) {
            ITemplatePortalObject templatePortalObject = (ITemplatePortalObject) page;
            ViewPageCommand pageTemplateAccessCommand = new ViewPageCommand(templatePortalObject.getTemplate().getId());
            String pageTemplateAccessUrl = new PortalURLImpl(pageTemplateAccessCommand, context, null, null).toString();
            pageTemplateAccessUrl += "?init-state=true&edit-template-mode=true";

            Element pageTemplateAccessLink = new DOMElement(QName.get(HTMLConstants.A));
            pageTemplateAccessLink.addAttribute(QName.get(HTMLConstants.HREF), pageTemplateAccessUrl);
            pageTemplateAccessLink.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS, locale));
            this.addSubMenuElement(configurationMenuUl, pageTemplateAccessLink);
        } else {
            Element pageTemplateAccessDisable = new DOMElement(QName.get(HTMLConstants.P));
            pageTemplateAccessDisable.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_TEMPLATE_ACCESS, locale));

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
        cachesInitialization.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CACHES_INITIALIZATION, locale));
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
            editionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_SPACE_EDITION_MENU_TITLE, locale));
        } else if (PortalObjectUtils.isTemplate(page)) {
            editionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_TEMPLATE_EDITION_MENU_TITLE, locale));
        } else {
            editionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_PAGE_EDITION_MENU_TITLE, locale));
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
            iconsDisplay.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_ICONS_DISPLAY, locale));
            this.addSubMenuElement(editionMenuUl, iconsDisplay);

            // HR
            this.addSubMenuElement(editionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));
        }

        // Page suppression
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_SUPPRESSION,
                this.internationalizationService.getString(InternationalizationConstants.KEY_SUPPRESSION, locale));

        // Page location
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_LOCATION,
                this.internationalizationService.getString(InternationalizationConstants.KEY_LOCATION, locale));

        // HR
        this.addSubMenuElement(editionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));

        // Page properties
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_PROPERTIES,
                this.internationalizationService.getString(InternationalizationConstants.KEY_PROPERTIES, locale));

        // Page CMS
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_CMS,
                this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_CONFIGURATION, locale));

        // Page rights
        this.addSubMenuFancyboxLink(editionMenuUl, URL_PAGE_RIGHTS,
                this.internationalizationService.getString(InternationalizationConstants.KEY_RIGHTS, locale));
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

        String basePath = page.getProperty("osivia.cms.basePath");

        Map<String, String> requestParameters = new HashMap<String, String>();

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSPublicationInfos publicationInfos = this.cmsService.getPublicationInfos(cmsCtx, pagePath);

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
        cmsEditionMenuTitle.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE, locale));

        cmsEditionMenu.add(cmsEditionMenuTitle);


        // Template edition menu "ul" node
        Element templateEditionMenuUl = new DOMElement(QName.get(HTMLConstants.UL));
        cmsEditionMenu.add(templateEditionMenuUl);


        // messages
        String previewRequired = this.internationalizationService.getString(InternationalizationConstants.KEY_PTITLE_PREVIEW_MODE_REQUIRED, locale);

        // ========== create new page

        // test si mode assistant activé
        if (modePreview) {
            cmsCtx.setDisplayLiveVersion("1");
        }

        String ecmBaseUrl = this.cmsService.getEcmDomain(cmsCtx);

        Element cmsCreatePage = null;
        if (modePreview) {
            cmsCreatePage = new DOMElement(QName.get(HTMLConstants.A));

            String createPageUrl = this.cmsService.getEcmUrl(cmsCtx, EcmCommand.createPage, path, requestParameters);
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

        cmsCreatePage.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_CREATE, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsCreatePage);


        // ========== Edit current page
        Element cmsEditPage = null;
        if (modePreview) {
            String editPageUrl = this.cmsService.getEcmUrl(cmsCtx, EcmCommand.editPage, path, requestParameters);

            cmsEditPage = new DOMElement(QName.get(HTMLConstants.A));
            cmsEditPage.addAttribute(QName.get(HTMLConstants.HREF), editPageUrl);
            cmsEditPage.addAttribute(QName.get(HTMLConstants.ACCESSKEY), "e");
            cmsEditPage.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
            cmsEditPage.addAttribute(QName.get(HTMLConstants.ONCLICK), "javascript:setCallbackFromEcmParams( '' , '" + ecmBaseUrl + "');");
        } else {
            cmsEditPage = new DOMElement(QName.get(HTMLConstants.P));
            cmsEditPage.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }

        cmsEditPage.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_OPTIONS, locale));
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


        cmsPublishDoc.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_PUBLISH, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsPublishDoc);


        // ========== Unpublish document

        Element cmsUnpublishDoc = null;

        if (modePreview) {
            if (published) {

                // if user is not at the root of the web site
                if (!basePath.equals(pagePath)) {

                    CMSPublishDocumentCommand unpublish = new CMSPublishDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                            CMSPublishDocumentCommand.UNPUBLISH);
                    String unpublishURL = context.renderURL(unpublish, urlContext, URLFormat.newInstance(true, true));

                    cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.A));
                    cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.HREF), unpublishURL);
                } else {
                    String notRootPage = this.internationalizationService.getString(InternationalizationConstants.KEY_PTITLE_NOT_ROOTPAGE, locale);
                    cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.P));
                    cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), notRootPage);
                }
            } else {
                String publishRequired = this.internationalizationService.getString(InternationalizationConstants.KEY_PTITLE_PUBLISH_REQUIRED, locale);
                cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.P));
                cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), publishRequired);
            }
        } else {
            cmsUnpublishDoc = new DOMElement(QName.get(HTMLConstants.P));
            cmsUnpublishDoc.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }


        cmsUnpublishDoc.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_UNPUBLISH, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsUnpublishDoc);


        // ========== Delete document


        Element cmsDeleteDoc = null;

        if (modePreview) {

            // user can only delete a document which its parent is editable.
            CMSObjectPath parent = CMSObjectPath.parse(pagePath).getParent();
            String parentPath = parent.toString();

            CMSPublicationInfos parentPubInfos = this.cmsService.getPublicationInfos(cmsCtx, parentPath);
            // if parent is editable and if user is not at the root of the web site 
            if (parentPubInfos.isEditableByUser() && !(basePath.equals(pagePath))) {

                CMSDeleteDocumentCommand delete = new CMSDeleteDocumentCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path);
                String removeURL = context.renderURL(delete, urlContext, URLFormat.newInstance(true, true));
                Element divDeleteFancyBox = generateDeleteFancyBox(locale, removeURL);
                administration.add(divDeleteFancyBox);

                cmsDeleteDoc = new DOMElement(QName.get(HTMLConstants.A));
                cmsDeleteDoc.addAttribute(QName.get(HTMLConstants.HREF), "#delete_cms_page");
                cmsDeleteDoc.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYBOX_INLINE);
            } else {
                String deleteForbidden = this.internationalizationService.getString(InternationalizationConstants.KEY_PTITLE_DELETE_FORBIDDEN, locale);
                cmsDeleteDoc = new DOMElement(QName.get(HTMLConstants.P));
                cmsDeleteDoc.addAttribute(QName.get(HTMLConstants.TITLE), deleteForbidden);
            }
        } else {
            cmsDeleteDoc = new DOMElement(QName.get(HTMLConstants.P));
            cmsDeleteDoc.addAttribute(QName.get(HTMLConstants.TITLE), previewRequired);
        }


        cmsDeleteDoc.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_DELETE, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsDeleteDoc);

        // HR
        this.addSubMenuElement(templateEditionMenuUl, new DOMElement(QName.get(HTMLConstants.HR)));


        // ========== sitemap
        Map<String, String> windowProps = new HashMap<String, String>();
        windowProps.put("osivia.cms.basePath", page.getProperty("osivia.cms.basePath"));
        Map<String, String> params = new HashMap<String, String>();

        String siteMapPopupURL = this.urlFactory.getStartPortletUrl(new PortalControllerContext(context),
                "osivia-portal-custom-web-assets-sitemapPortletInstance", windowProps, params, true);

        Element cmsViewSitemap = new DOMElement(QName.get(HTMLConstants.A));
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.HREF), siteMapPopupURL);
        cmsViewSitemap.addAttribute(QName.get(HTMLConstants.CLASS), HTML_CLASS_FANCYFRAME_REFRESH);
        cmsViewSitemap.setText(this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_SITEMAP, locale));
        this.addSubMenuElement(templateEditionMenuUl, cmsViewSitemap);

    }


    /**
     * Utility method used to generation administration toggle version.
     *
     * @param context controller context
     * @param page current page
     * @param administration administration toolbar element
     * @throws CMSException
     */
    private void generateAdministrationToggleVersion(ControllerContext context, Page page, Element administration) throws CMSException {
        Locale locale = context.getServerInvocation().getRequest().getLocale();

        String editionMode = CmsPermissionHelper.getCurrentCmsEditionMode(context);

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(context.getServerInvocation());
        cmsCtx.setControllerContext(context);

        String pagePath = (String) context.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.path");
        cmsCtx.setDisplayLiveVersion("1");
        CMSItem liveDoc = this.cmsService.getContent(cmsCtx, pagePath);

        String path = liveDoc.getPath();


        // ---------------------
        String toggleTitle = this.internationalizationService.getString(InternationalizationConstants.KEY_PTITLE_TOGGLE_VERSION, locale);
        String editionTxt = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_EDITION, locale);
        String previewTxt = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_PREVIEW, locale);
        String onlineTxt = this.internationalizationService.getString(InternationalizationConstants.KEY_CMS_PAGE_ONLINE, locale);


        Element switches = new DOMElement(QName.get(HTMLConstants.SPAN));
        switches.addAttribute(QName.get(HTMLConstants.CLASS), "cmsSwitches");

        Element swOnline = new DOMElement(QName.get(HTMLConstants.A));
        swOnline.addAttribute(QName.get(HTMLConstants.CLASS), "cmsSwitch");

        // Go online
        Element swOnlineImg = new DOMElement(QName.get(HTMLConstants.IMG));
        swOnlineImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_published_version.png");
        swOnline.add(swOnlineImg);

        swOnline.addAttribute(QName.get(HTMLConstants.TITLE), toggleTitle.concat(onlineTxt));
        ChangeCMSEditionModeCommand changeVersionOnline = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_ONLINE, editionMode);
        String onlineUrl = new PortalURLImpl(changeVersionOnline, context, null, null).toString();
        swOnline.addAttribute(QName.get(HTMLConstants.HREF), onlineUrl);
        switches.add(swOnline);

        // Go preview
        Element swPreview = new DOMElement(QName.get(HTMLConstants.A));
        swPreview.addAttribute(QName.get(HTMLConstants.CLASS), "cmsSwitch");

        Element swPreviewImg = new DOMElement(QName.get(HTMLConstants.IMG));
        swPreviewImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_preview_live_version.png");
        swPreview.add(swPreviewImg);

        swPreview.addAttribute(QName.get(HTMLConstants.TITLE), toggleTitle.concat(previewTxt));
        ChangeCMSEditionModeCommand changeVersionPreview = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_OFF);
        String previewUrl = new PortalURLImpl(changeVersionPreview, context, null, null).toString();
        swPreview.addAttribute(QName.get(HTMLConstants.HREF), previewUrl);
        switches.add(swPreview);

        // Go Edition
        Element swEdition = new DOMElement(QName.get(HTMLConstants.A));
        swEdition.addAttribute(QName.get(HTMLConstants.CLASS), "cmsSwitch");

        Element swEditionImg = new DOMElement(QName.get(HTMLConstants.IMG));
        swEditionImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_live_version.png");
        swEdition.add(swEditionImg);

        swEdition.addAttribute(QName.get(HTMLConstants.TITLE), toggleTitle.concat(editionTxt));
        ChangeCMSEditionModeCommand changeVersionEdition = new ChangeCMSEditionModeCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), path,
                CmsPermissionHelper.CMS_VERSION_PREVIEW, CmsPermissionHelper.CMS_EDITION_MODE_ON);
        String editionUrl = new PortalURLImpl(changeVersionEdition, context, null, null).toString();
        swEdition.addAttribute(QName.get(HTMLConstants.HREF), editionUrl);
        switches.add(swEdition);

        // Status label
        Element lbl = new DOMElement(QName.get(HTMLConstants.SPAN));

        if (CmsPermissionHelper.getCurrentCmsVersion(context).equals(CmsPermissionHelper.CMS_VERSION_ONLINE)) {

            swOnlineImg.addAttribute(QName.get(HTMLConstants.CLASS), "imgActive");
            swOnlineImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_published_version_hover.png");

            lbl.addAttribute(QName.get(HTMLConstants.CLASS), "online-label");
            lbl.setText(onlineTxt);
        } else {

            if (CmsPermissionHelper.getCurrentCmsEditionMode(context).equals(CmsPermissionHelper.CMS_EDITION_MODE_OFF)) {
                swPreviewImg.addAttribute(QName.get(HTMLConstants.CLASS), "imgActive");
                swPreviewImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_preview_live_version_hover.png");
                lbl.addAttribute(QName.get(HTMLConstants.CLASS), "preview-label");
                lbl.setText(previewTxt);
            } else {
                swEditionImg.addAttribute(QName.get(HTMLConstants.CLASS), "imgActive");
                swEditionImg.addAttribute(QName.get(HTMLConstants.SRC), "/osivia-portal-custom-web-assets/images/icons/icon_live_version_hover.png");
                lbl.addAttribute(QName.get(HTMLConstants.CLASS), "edition-label");
                lbl.setText(editionTxt);
            }
        }


        switches.add(lbl);

        administration.add(switches);
    }

    /**
     * Fancy box for delete page
     * 
     * @param locale user locale
     * @param urlDelete the command for delete
     * @return
     * @throws UnsupportedEncodingException
     */
    private Element generateDeleteFancyBox(Locale locale, String urlDelete) throws UnsupportedEncodingException {
        String[] split = urlDelete.split("\\?");
        String action = split[0];
        String[] args = split[1].split("&");

        DOMElement div = new DOMElement(QName.get(HTMLConstants.DIV));
        div.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CONTAINER);

        DOMElement innerDiv = new DOMElement(QName.get(HTMLConstants.DIV));
        innerDiv.addAttribute(QName.get(HTMLConstants.ID), "delete_cms_page");
        div.add(innerDiv);

        Element form = new DOMElement(QName.get(HTMLConstants.FORM));
        form.addAttribute(QName.get(HTMLConstants.METHOD), HTMLConstants.FORM_METHOD_GET);
        form.addAttribute(QName.get(HTMLConstants.ACTION), action);
        form.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_FORM);
        innerDiv.add(form);

        Element divMsg = new DOMElement(QName.get(HTMLConstants.DIV));
        divMsg.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CENTER_CONTENT);
        divMsg.setText(internationalizationService.getString("CMS_DELETE_CONFIRM_MESSAGE", locale));
        form.add(divMsg);

        Element divButton = new DOMElement(QName.get(HTMLConstants.DIV));
        divButton.addAttribute(QName.get(HTMLConstants.CLASS), HTMLConstants.CLASS_FANCYBOX_CENTER_CONTENT);

        for (int i = 0; i < args.length; i++) {
            Element hiddenArg = new DOMElement(QName.get(HTMLConstants.INPUT));
            hiddenArg.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_HIDDEN);
            hiddenArg.addAttribute(QName.get(HTMLConstants.NAME), args[i].split("=")[0]);
            String value = args[i].split("=")[1];
            hiddenArg.addAttribute(QName.get(HTMLConstants.VALUE), URLDecoder.decode(value, "UTF-8"));
            divButton.add(hiddenArg);
        }

        Element btnOk = new DOMElement(QName.get(HTMLConstants.INPUT));
        btnOk.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_SUBMIT);
        btnOk.addAttribute(QName.get(HTMLConstants.VALUE), internationalizationService.getString("YES", locale));
        divButton.add(btnOk);

        Element btnQuit = new DOMElement(QName.get(HTMLConstants.INPUT));
        btnQuit.addAttribute(QName.get(HTMLConstants.TYPE), HTMLConstants.INPUT_TYPE_BUTTON);
        btnQuit.addAttribute(QName.get(HTMLConstants.VALUE), internationalizationService.getString("NO", locale));
        btnQuit.addAttribute(QName.get(HTMLConstants.ONCLICK), "closeFancybox()");
        divButton.add(btnQuit);

        form.add(divButton);

        return div;
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
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
