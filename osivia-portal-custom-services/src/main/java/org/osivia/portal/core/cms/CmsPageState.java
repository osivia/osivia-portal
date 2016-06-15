package org.osivia.portal.core.cms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.taskbar.ITaskbarService;
import org.osivia.portal.api.taskbar.TaskbarItem;
import org.osivia.portal.api.taskbar.TaskbarItems;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.portalobjects.DynamicTemplatePage;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;

import bsh.Interpreter;

/**
 * CMS page state.
 *
 * @author Cédric Krommenhoek
 */
public class CmsPageState {

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Taskbar service. */
    private final ITaskbarService taskbarService;


    private Page page;

    private PortalObjectId pageIdToDiplay;

    private boolean isPageToDisplayUncontextualized;

    private CMSItem cmsNav;


    private final Log logger;

    private final ControllerContext controllerContext;

    private final Page baseCMSPublicationPage;

    private final Level level;

    private final String contextualization;

    private final CMSItem cmsItem;

    private final String itemPublicationPath;

    private final String basePublishPath;

    private final Page currentPage;

    private final Map<String, String> pageParams;

    private final String contentPath;

    private final CMSPublicationInfos pubInfos;

    private final boolean contextualizedInCurrentPage;

    private final String cmsPath;

    private final String displayContext;

    private final boolean skipPortletInitialisation;


    /**
     * Constructor.
     *
     * @param basePage base page
     */
    public CmsPageState(ControllerContext controllerContext, Page baseCMSPublicationPage, Level level, String contextualization, CMSItem cmsItem,
            String itemPublicationPath, String basePublishPath, Page currentPage, Map<String, String> pageParams, String contentPath,
            CMSPublicationInfos pubInfos, boolean contextualizedInCurrentPage, String cmsPath, String displayContext, boolean skipPortletInitialisation) {
        super();
        this.logger = LogFactory.getLog(this.getClass());
        this.controllerContext = controllerContext;
        this.baseCMSPublicationPage = baseCMSPublicationPage;
        this.level = level;
        this.contextualization = contextualization;
        this.cmsItem = cmsItem;
        this.itemPublicationPath = itemPublicationPath;
        this.basePublishPath = basePublishPath;
        this.currentPage = currentPage;
        this.pageParams = pageParams;
        this.contentPath = contentPath;
        this.pubInfos = pubInfos;
        this.contextualizedInCurrentPage = contextualizedInCurrentPage;
        this.cmsPath = cmsPath;
        this.displayContext = displayContext;
        this.skipPortletInitialisation = skipPortletInitialisation;

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Taskbar service
        this.taskbarService = Locator.findMBean(ITaskbarService.class, ITaskbarService.MBEAN_NAME);
    }


    /**
     * Init page state.
     *
     * @return unavaible resource response
     * @throws ControllerException
     */
    public UnavailableResourceResponse initState() throws ControllerException {
        try {
            /*
             * Calcul des éléments de navigation
             *
             * - rubrique (cmsNav) - template de page - scope de page - scope
             * d'item
             *
             * On verifie également que tout l'arbre de navigation est
             * accessible (en terme de droit)
             */

            boolean disableCMSLocationInPage = false;
            String ecmPageTemplate = null;
            String ecmPageTheme = null;
            String computedPageScope = null;

            String portalSiteScope = null;
            final CMSServiceCtx cmsReadNavContext = new CMSServiceCtx();


            if (this.baseCMSPublicationPage != null) {
                portalSiteScope = this.baseCMSPublicationPage.getProperty("osivia.cms.navigationScope");
                cmsReadNavContext.setControllerContext(this.controllerContext);
                cmsReadNavContext.setScope(portalSiteScope);
                if (this.level == Level.allowPreviewVersion) {
                    cmsReadNavContext.setDisplayLiveVersion("1");
                }
            }

            if ((this.baseCMSPublicationPage != null) && !IPortalUrlFactory.CONTEXTUALIZATION_PORTLET.equals(this.contextualization)) {
                if ("1".equals(this.baseCMSPublicationPage.getProperty("osivia.cms.directContentPublisher"))) {
                    /* publication directe d'un contenu sans le publishsite */
                    this.cmsNav = this.cmsItem;
                    // ECMPageTemplate =
                    // cmsItem.getProperties().get("pageTemplate");
                } else {
                    boolean errorDuringCheck = false;
                    String pathToCheck = this.itemPublicationPath;

                    do {
                        try {
                            final CMSItem cmsItemNav = this.getCMSService().getPortalNavigationItem(cmsReadNavContext, this.basePublishPath, pathToCheck);

                            if (cmsItemNav == null) {
                                // Pb de droits, on coupe la branche
                                this.cmsNav = null;
                                ecmPageTemplate = null;
                                computedPageScope = null;
                            } else {
                                boolean isNavigationElement = false;

                                final String navigationElement = cmsItemNav.getProperties().get("navigationElement");

                                if ((pathToCheck.equals(this.basePublishPath) || "1".equals(navigationElement))) {
                                    isNavigationElement = true;
                                }

                                if (!isNavigationElement) {
                                    this.cmsNav = null;
                                    ecmPageTemplate = null;
                                    computedPageScope = null;
                                }

                                if ((this.cmsNav == null) && isNavigationElement) {
                                    this.cmsNav = cmsItemNav;
                                }

                                boolean computePageTemplate = true;

                                // Sur les pages statiques, on ignore le template par défaut
                                if (!(this.baseCMSPublicationPage instanceof DynamicTemplatePage)) {
                                    if ("1".equals(cmsItemNav.getProperties().get("defaultTemplate"))) {
                                        computePageTemplate = false;
                                    }
                                }

                                if (ecmPageTemplate == null) {
                                    final boolean isChildPath = (this.itemPublicationPath.contains(pathToCheck))
                                            && !(this.itemPublicationPath.equalsIgnoreCase(pathToCheck));
                                    if (isChildPath) {
                                        final String childrenPageTemplate = cmsItemNav.getProperties().get("childrenPageTemplate");
                                        if (StringUtils.isNotEmpty(childrenPageTemplate)) {
                                            ecmPageTemplate = childrenPageTemplate;
                                        }
                                    }
                                }

                                if (computePageTemplate) {
                                    if (ecmPageTemplate == null) {
                                        // Template
                                        String template = cmsItemNav.getProperties().get("pageTemplate");
                                        if (template == null) {
                                            // Staple taskbar item indicator
                                            boolean staple = BooleanUtils.toBoolean(cmsItemNav.getProperties().get("staple"));
                                            if (staple) {
                                                // Portal controller context
                                                PortalControllerContext portalControllerContext = new PortalControllerContext(this.controllerContext);
                                                // Taskbar items
                                                TaskbarItems items = this.taskbarService.getItems(portalControllerContext);
                                                // Taskbar item identifier
                                                String id = StringUtils.upperCase(StringUtils.substringAfter(cmsItemNav.getWebId(), "_"));
                                                // Taskbar item
                                                TaskbarItem item = items.get(id);
                                                if (item != null) {
                                                    template = item.getTemplate();
                                                }
                                            }
                                        }

                                        if (template != null) {
                                            // TODO workspace template

                                            ecmPageTemplate = template;
                                        }
                                    }

                                    if (ecmPageTheme == null) {
                                        String theme = cmsItemNav.getProperties().get("theme");
                                        if (theme != null) {
                                            ecmPageTheme = theme;
                                        }
                                    }
                                }

                                if (computedPageScope == null) {
                                    String scope = cmsItemNav.getProperties().get("pageScope");
                                    if (scope != null) {
                                        computedPageScope = scope;
                                    }
                                }
                            }

                            // One level up
                            final CMSObjectPath parentPath = CMSObjectPath.parse(pathToCheck).getParent();
                            pathToCheck = parentPath.toString();
                        } catch (final CMSException e) {
                            // Probleme d'acces aux items de navigation de niveau supérieur ; on decontextualise
                            errorDuringCheck = true;
                        }
                    } while (!errorDuringCheck && pathToCheck.contains(this.basePublishPath));


                    // Get parameterized template
                    if ((this.pageParams != null) && this.pageParams.containsKey("osivia.template")) {
                        ecmPageTemplate = "/default/templates/" + this.pageParams.get("osivia.template");
                    }


                    if (errorDuringCheck) {
                        // Pb droits sur la navigation : le contenu doit être publié dans la page mais sans tenir compte de son path ...
                        this.cmsNav = null;
                        disableCMSLocationInPage = true;
                        ecmPageTemplate = null;
                    }
                }
            }

            // Recupération du layout
            String layoutType = null;
            String layoutPath = null;

            if ((this.baseCMSPublicationPage != null) && (this.cmsNav != null)) {
                layoutType = this.baseCMSPublicationPage.getProperty("osivia.cms.layoutType");

                String layoutRules = null;

                if (layoutType == null) {
                    // On applique la gestion des layouts pour les sous-pages des espaces statiques

                    // Page statique
                    if (!(this.baseCMSPublicationPage instanceof DynamicTemplatePage)) {
                        // En mode navigation cms, toutes les pages sont dynamiques, meme la page d'accueil statique
                        // Sinon les Sous-pages des pages statiques sont également dynamiques

                        // v2.0-rc7
                        if (ecmPageTemplate != null) {
                            layoutType = CmsCommand.LAYOUT_TYPE_SCRIPT;
                            layoutRules = "return ECMPageTemplate;";
                        }
                    }
                } else {
                    // Gestion des layouts standards
                    if (CmsCommand.LAYOUT_TYPE_SCRIPT.equals(layoutType)) {
                        layoutRules = this.baseCMSPublicationPage.getProperty("osivia.cms.layoutRules");
                    }
                }

                if (CmsCommand.LAYOUT_TYPE_SCRIPT.equals(layoutType)) {
                    if (layoutRules != null) {
                        // Evaluation beanshell
                        final Interpreter i = new Interpreter();
                        i.set("doc", this.cmsNav.getNativeItem());

                        i.set("ECMPageTemplate", ecmPageTemplate);

                        layoutPath = (String) i.eval(layoutRules);

                        if (layoutPath == null) {
                            if ((this.baseCMSPublicationPage instanceof CMSTemplatePage)) {
                                layoutPath = this.baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                            }
                        } else {
                            if (!layoutPath.startsWith("/")) {
                                // Path Relatif
                                layoutPath = this.baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + layoutPath;
                            }
                        }
                    } else {
                        layoutPath = this.baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                    }
                }
            }

            // Get real page to display
            this.pageIdToDiplay = null;

            if (this.baseCMSPublicationPage != null) {
                if (layoutPath != null) {
                    this.pageIdToDiplay = new PortalObjectId("", this.baseCMSPublicationPage.getId().getPath().getChild(CMSTemplatePage.PAGE_NAME));
                } else {
                    this.pageIdToDiplay = this.baseCMSPublicationPage.getId();
                }

                if (this.cmsNav == null) {
                    this.cmsNav = this.getCMSService().getPortalNavigationItem(cmsReadNavContext, this.basePublishPath, this.basePublishPath);
                }
            } else {
                this.pageIdToDiplay = this.currentPage.getId();
            }

            // Préparation des paramètres de la page
            PageNavigationalState previousPNS = null;

            final NavigationalStateContext nsContext = (NavigationalStateContext) this.controllerContext
                    .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

            final Map<QName, String[]> pageState = new HashMap<QName, String[]>();

            if (this.cmsNav != null) {
                // Mise à jour paramètre public page courante
                previousPNS = nsContext.getPageNavigationalState(this.pageIdToDiplay.toString());

                if (this.pageParams != null) {
                    for (final Map.Entry<String, String> entry : this.pageParams.entrySet()) {
                        pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[]{entry.getValue()});
                    }
                }

                // Mise à jour du path de navigation
                if (this.itemPublicationPath.startsWith(this.basePublishPath) && !disableCMSLocationInPage) {
                    // String relPath = computeNavPath(cmsPath.substring(basePublishPath.length()));
                    final String relPath = this.itemPublicationPath.substring(this.basePublishPath.length());
                    pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.itemRelPath"), new String[]{relPath});
                }

                pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"), new String[]{this.cmsNav.getPath()});

                final String webId = this.cmsNav.getWebId();

                if (webId != null) {
                    pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.webid"), new String[]{this.cmsNav.getWebId()});
                }

                // Mise à jour du path de contenu
                pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"), new String[]{this.contentPath});


                // Le path CMS identifie de manière unique la session puisqu'il s'agit d'une nouvelle page
                pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.uniqueID"), new String[]{String.valueOf(System.currentTimeMillis())});


                // Parameterized template
                final Object parameterizedTemplate = this.controllerContext.getAttribute(Scope.REQUEST_SCOPE, InternalConstants.PARAMETERIZED_TEMPLATE_ATTRIBUTE);
                if (parameterizedTemplate != null) {
                    layoutPath = "/default/templates/" + parameterizedTemplate.toString();
                }

                if (layoutPath != null) {
                    pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.layout_path"), new String[]{layoutPath});
                }

                if ((layoutPath != null) && (ecmPageTheme != null)) {
                    pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.theme_path"), new String[]{ecmPageTheme});
                }

                // Mise à jour du scope de la page
                if ((computedPageScope != null) && (computedPageScope.length() > 0)) {
                    pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageScope"), new String[]{computedPageScope});
                }


                final EditionState editionState = this.getEditionState(this.pageIdToDiplay, this.pubInfos, this.contextualizedInCurrentPage);
                ContributionService.updateNavigationalState(this.controllerContext, pageState, editionState);


                nsContext.setPageNavigationalState(this.pageIdToDiplay.toString(), new PageNavigationalState(pageState));

                // Layout has been computed and copied to page state It's time to initialize dynamic page
                if (layoutPath != null) {
                    DynamicPortalObjectContainer.clearCache();

                    // TODO : remettre le cmsedition à null

                    PageProperties.getProperties().getPagePropertiesMap().remove("osivia.fetchedPortalProperties");
                }
            }


            // Instanciation de la page
            this.page = (Page) this.controllerContext.getController().getPortalObjectContainer().getObject(this.pageIdToDiplay);
            if (this.page == null) {
                this.page = (Page) this.controllerContext.getController().getPortalObjectContainer().getObject(this.currentPage.getId());
            }
            if (this.page == null) {
                if (layoutPath != null) {
                    this.logger.error("Le template " + layoutPath + " n'a pas pu être instancié");
                }
                return new UnavailableResourceResponse(this.itemPublicationPath, false);
            }


            // Propagation des selecteurs si les paramètres ne sont pas explicites
            if ((previousPNS != null) && ((this.pageParams == null) || (this.pageParams.size() == 0))) {
                if ("1".equals(this.page.getProperty("osivia.cms.propagateSelectors"))) {
                    final String[] selectors = previousPNS.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"));

                    if (selectors != null) {
                        previousPNS = nsContext.getPageNavigationalState(this.pageIdToDiplay.toString());
                        pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"), selectors);
                        nsContext.setPageNavigationalState(this.pageIdToDiplay.toString(), new PageNavigationalState(pageState));
                    }
                }
            }

            // #1160 - Propagate cmsItem selectors to portal selectors if exists
            // #1160 - Check if this.cmsNav is not null
            if ((this.cmsNav != null) && this.cmsNav.getProperties().containsKey("selectors")) {
                final String cmsSelector = this.cmsNav.getProperties().get("selectors");
                final Map<String, List<String>> decodeCmsProperties = PageParametersEncoder.decodeProperties(cmsSelector);

                final String[] pageSelectors = pageState.get(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"));
                if ((pageSelectors != null) && (pageSelectors.length > 0)) {

                    final Map<String, List<String>> decodePageProperties = PageParametersEncoder.decodeProperties(pageSelectors[0]);
                    decodeCmsProperties.putAll(decodePageProperties);
                }

                final String encodeProperties = PageParametersEncoder.encodeProperties(decodeCmsProperties);

                pageState.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "selectors"), new String[]{encodeProperties});
                nsContext.setPageNavigationalState(this.pageIdToDiplay.toString(), new PageNavigationalState(pageState));
            }

            this.isPageToDisplayUncontextualized = false;

            if (this.cmsNav == null) {
                // Pb sur la navigation, on affiche le contenu sauf si le contenu est la page
                // auquel cas, on reste sur la page (cas d'une page mal définie pointant vers un path cms qui n'est pas un espace de publication)
                if ((this.itemPublicationPath != null) && this.itemPublicationPath.equals(this.page.getProperty("osivia.cms.basePath"))) {
                    this.isPageToDisplayUncontextualized = true;
                }
            }


            if ((((this.cmsNav != null) || this.isPageToDisplayUncontextualized)) && !this.skipPortletInitialisation) {

                // Reinitialisation des renders parameters et de l'état
                final Iterator<PortalObject> i = this.page.getChildren(Page.WINDOW_MASK).iterator();
                while (i.hasNext()) {
                    final Window window = (Window) i.next();

                    final NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

                    final WindowNavigationalState windowNavState = WindowNavigationalState.create();

                    // On la force en vue NORMAL
                    final WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL, windowNavState.getMode(),
                            ParametersStateString.create());
                    this.controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
                }

                // Réinitialisation des états des fenêtres
                final Collection<PortalObject> windows = this.page.getChildren(PortalObject.WINDOW_MASK);

                // et des anciens caches
                // (si 2 pages P1 et P2 ont des templates différents dans une même page dynamique,
                // les caches de P1 sont conservés quand va sur P2 et on revient sur P1
                for (final PortalObject po : this.page.getChildren(PortalObject.WINDOW_MASK)) {
                    this.controllerContext.removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, po.getId().toString());
                    this.controllerContext.removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, "cached_markup." + po.getId().toString());
                }

                // Mode normal
                PageCustomizerInterceptor.unsetMaxMode(windows, this.controllerContext);

            }
        } catch (final Exception e) {
            throw new ControllerException(e);
        }

        return null;
    }


    /**
     * compute edition state relevantly to cms command
     *
     * @param poid
     * @param pubInfos
     * @return
     */
    public EditionState getEditionState(PortalObjectId poid, CMSPublicationInfos pubInfos, boolean contextualizedInCurrentPage) {

        EditionState editionState = null;


        // Default initialization of editionState
        if (pubInfos.isLiveSpace()) {
            editionState = new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, this.cmsPath);
        } else {
            if (InternalConstants.PROXY_PREVIEW.equals(this.displayContext) || IPortalUrlFactory.DISPLAYCTX_PREVIEW_LIVE_VERSION.equals(this.displayContext)
                    || InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext)) {
                editionState = new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, this.cmsPath);
            } else {
                editionState = new EditionState(EditionState.CONTRIBUTION_MODE_ONLINE, this.cmsPath);
            }
        }


        // Modified indicator
        final boolean fancyBox = InternalConstants.FANCYBOX_PROXY_CALLBACK.equals(this.displayContext)
                || InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext);

        if (fancyBox) {
            this.controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.refreshBack", true);
            this.controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.mobileRefreshBack", true);
        }

        // page marker initialization ( means an applicative back button)
        if (("menu".equals(this.displayContext) || "taskbar".equals(this.displayContext) || "breadcrumb".equals(this.displayContext)
                || "destroyedChild".equals(this.displayContext) || "tabs".equals(this.displayContext))) {
            PageCustomizerInterceptor.initPageBackInfos(this.controllerContext);
        } else {
            // Preserve back in case of fancybox
            if (!fancyBox && !InternalConstants.PROXY_PREVIEW.equals(this.displayContext)) {
                // reset back in case of change of tab
                if (!contextualizedInCurrentPage) {
                    // Except in mobile mode
                    PageCustomizerInterceptor.initPageBackInfos(this.controllerContext, false);
                    final String backPageMarker = (String) this.controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                    this.controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", backPageMarker);
                } else {
                    final String backPageMarker = (String) this.controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                    this.controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backPageMarker", backPageMarker);
                    this.controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.backMobilePageMarker", backPageMarker);

                }

            }
        }

        return editionState;
    }


    /**
     * Get CMS service.
     *
     * @return CMS service
     */
    private ICMSService getCMSService() {
        return this.cmsServiceLocator.getCMSService();
    }


    /**
     * Getter for page.
     *
     * @return the page
     */
    public Page getPage() {
        return this.page;
    }


    /**
     * Getter for pageIdToDiplay.
     *
     * @return the pageIdToDiplay
     */
    public PortalObjectId getPageIdToDiplay() {
        return this.pageIdToDiplay;
    }


    /**
     * Getter for isPageToDisplayUncontextualized.
     *
     * @return the isPageToDisplayUncontextualized
     */
    public boolean isPageToDisplayUncontextualized() {
        return this.isPageToDisplayUncontextualized;
    }


    /**
     * Getter for cmsNav.
     *
     * @return the cmsNav
     */
    public CMSItem getCmsNav() {
        return this.cmsNav;
    }

}
