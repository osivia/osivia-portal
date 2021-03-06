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
 */
package org.osivia.portal.core.cms;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.cache.CacheLevel;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cms.VirtualNavigationUtils;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.player.Player;
import org.osivia.portal.api.statistics.IStatisticsService;
import org.osivia.portal.api.theming.TabGroup;
import org.osivia.portal.api.trace.ITraceServiceLocator;
import org.osivia.portal.api.trace.Trace;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.error.UserNotificationsException;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;
import org.osivia.portal.core.web.IWebIdService;

/**
 *
 * C'est ici qu'est centralisée la gestion de l'affichage des contenus CMS
 *
 * - choix de la page - contextualisation - choix du gabarit d'affichage
 *
 * @author jeanseb
 *
 */
public class CmsCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(CmsCommand.class);

    public static final String LAYOUT_TYPE_CURRENT_PAGE = "0";
    public static final String LAYOUT_TYPE_SCRIPT = "1";
    
    public static final String PROCEDURE_TYPE="ProcedureInstance";
    public static final String PROCEDURE_LABEL_KEY="PROCEDURE_LABEL";
    /**
	 * Id of action (for stats)
	 */
	private static final String TRACE_CMS = "CMS";

    private static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

    private static INotificationsService notifService = NotificationsUtils.getNotificationsService();

    private static ICMSServiceLocator cmsServiceLocator;


    private String pagePath;

    private String cmsPath;
    private String contentPath;

    private String contextualization;

    private String displayContext;
    private String hideMetaDatas;
    private String itemScope;
    private String displayLiveVersion;
    private String windowPermReference;
    private String addToBreadcrumb;
    private String portalPersistentName;
    private boolean insertPageMarker = true;
    private boolean skipPortletInitialisation = false;
    private String ecmActionReturn;

    /** Page parameters. */
    private Map<String, String> pageParams;

    private IProfilManager profilManager;

    private IPortalUrlFactory urlFactory;

    private IWebIdService webIdService;


    /** Statistics service. */
    private final IStatisticsService statisticsService;
    
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;


    /**
     * Default constructor.
     */
    public CmsCommand() {
        super();

        // Statistics service
        this.statisticsService = Locator.findMBean(IStatisticsService.class, IStatisticsService.MBEAN_NAME);
        
        IInternationalizationService internationalizationService = InternationalizationUtils.getInternationalizationService();
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        
        
    }


    /**
     * Constructor.
     * TODO : supprimer parametre scope en 2.1 (pas d'impact car plus utilise)
     *
     * @param pagePath
     * @param cmsPath
     * @param pageParams
     * @param contextualization
     * @param displayContext
     * @param hideMetaDatas
     * @param itemScope
     * @param displayLiveVersion
     * @param windowPermReference
     * @param addToBreadcrumb
     * @param portalPersistentName
     */
    public CmsCommand(String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization, String displayContext, String hideMetaDatas,
            String itemScope, String displayLiveVersion, String windowPermReference, String addToBreadcrumb, String portalPersistentName) {
        this();
        this.pagePath = pagePath;
        this.cmsPath = cmsPath;
        this.pageParams = pageParams;
        this.contextualization = contextualization;
        this.displayContext = displayContext;
        this.hideMetaDatas = hideMetaDatas;
        this.itemScope = itemScope;
        if (this.itemScope == null) {
            this.itemScope = "__nocache";
        }
        this.displayLiveVersion = displayLiveVersion;
        this.windowPermReference = windowPermReference;
        this.addToBreadcrumb = addToBreadcrumb;
        this.portalPersistentName = portalPersistentName;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return info;
    }


    public static ICMSService getCMSService() {
        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();
    }


    public IProfilManager getProfilManager() throws Exception {
        if (this.profilManager == null) {
            this.profilManager = Locator.findMBean(IProfilManager.class, "osivia:service=ProfilManager");
        }

        return this.profilManager;
    }


    public IPortalUrlFactory getUrlFactory() throws Exception {
        if (this.urlFactory == null) {
            this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        }

        return this.urlFactory;
    }


    /**
     * Get Webid service
     *
     * @return
     */
    public IWebIdService getWebIdService() {
        if (this.webIdService == null) {
            this.webIdService = Locator.findMBean(IWebIdService.class, "osivia:service=webIdService");
        }

        return this.webIdService;
    }


    private static boolean checkScope(String pageScope, IProfilManager profilManager) {
        if (pageScope == null) {
            return true;
        }
        if ("__nocache".equals(pageScope)) {
            return true;
        }
        if ("anonymous".equals(pageScope)) {
            return true;
        }

        return profilManager.verifierProfilUtilisateur(pageScope);
    }


    public static CMSItem getPagePublishSpaceConfig(ControllerContext ctx, PortalObject currentPage) throws Exception {
        CMSItem publishSpaceConfig = null;

        if (currentPage == null) {
            return null;
        }

        String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

        if (pageBasePath != null) {
            CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
            cmsReadItemContext.setControllerContext(ctx);

            publishSpaceConfig = getCMSService().getSpaceConfig(cmsReadItemContext, currentPage.getProperty("osivia.cms.basePath"));
        }

        return publishSpaceConfig;
    }


    public static boolean isContentAlreadyContextualizedInPage(ControllerContext ctx, PortalObject currentPage, String cmsPath) throws Exception {
        String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

        if ((pageBasePath != null) && (cmsPath != null) && cmsPath.contains(pageBasePath)) {
            CMSItem publishSpaceConfig = getPagePublishSpaceConfig(ctx, currentPage);

            if ((publishSpaceConfig != null) && "1".equals(publishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {
                return true;
            }
        }

        return false;
    }


    public static String computeNavPath(String path) {
        String result = path;
        if (path.endsWith(".proxy")) {
            result = result.substring(0, result.length() - 6);
        }
        return result;
    }


    private static PortalObject searchPublicationPageForPub(CMSServiceCtx cmsCtx, CMSPublicationInfos pubInfos, PortalObject po, String searchPath,
            IProfilManager profilManager) throws Exception {
        String path = po.getDeclaredProperty("osivia.cms.basePath");

        if (path != null) {

            if ((path.equals(pubInfos.getPublishSpacePath()))) {

                if (checkScope(po.getProperty(Constants.WINDOW_PROP_SCOPE), profilManager)) {

                    CMSItem publishSpaceConfig = getCMSService().getSpaceConfig(cmsCtx, pubInfos.getPublishSpacePath());

                    if ("1".equals(publishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {

                        return po;
                    }
                }
            }
        }

        // Children
        if (po instanceof Portal) {
            for (PortalObject page : po.getChildren(PortalObject.PAGE_MASK)) {
                PortalObject child = searchPublicationPageForPub(cmsCtx, pubInfos, page, searchPath, profilManager);
                if (child != null) {
                    return child;
                }
            }
        }

        return null;
    }


    public static PortalObject searchPublicationPage(ControllerContext ctx, PortalObject po, String searchPath, IProfilManager profilManager) throws Exception {
        CMSServiceCtx cmsReadItemContext = null;

        cmsReadItemContext = new CMSServiceCtx();
        cmsReadItemContext.setControllerContext(ctx);

        CMSPublicationInfos pubInfos = getCMSService().getPublicationInfos(cmsReadItemContext, searchPath.toString());

        if (pubInfos.getPublishSpacePath() == null) {
            return null;
        }

        PortalObject page = searchPublicationPageForPub(cmsReadItemContext, pubInfos, po, searchPath, profilManager);


        // On regarde dans les autres portails
        if ((po instanceof Portal) && (page == null)) {
            Collection<PortalObject> portals = ctx.getController().getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);
            for (PortalObject portal : portals) {
                // Uniquement si ils sont en mode publication satellite

                // if( "satellite".equals(portal.getProperty("osivia.portal.publishingPolicy"))) {
                if (!portal.getId().equals(po.getId())) {
                    page = searchPublicationPageForPub(cmsReadItemContext, pubInfos, portal, searchPath, profilManager);
                }
                if (page != null) {
                    break;
                    // }
                }
            }
        }


        return page;

    }


    /**
     * Get sharing page.
     *
     * @param portal portal
     * @param sharingRootPath sharing root path
     * @return page
     * @throws InvocationException
     * @throws ControllerException
     */
    private Page getSharingPage(Portal portal, String sharingRootPath) throws InvocationException, ControllerException {
        // Portal object container
        PortalObjectContainer portalObjectContainer = this.context.getController().getPortalObjectContainer();
        // Internationalization bundle
        Locale locale = this.context.getServerInvocation().getRequest().getLocale();
        Bundle bundle = this.bundleFactory.getBundle(locale);

        // Page display names
        Map<Locale, String> displayNames = new HashMap<>(1);
        displayNames.put(Locale.FRENCH, bundle.getString("PAGE_SHARING_DISPLAY_NAME"));

        // Template
        PortalObjectId templateObjectId = PortalObjectId.parse("/default/templates/sharing", PortalObjectPath.CANONICAL_FORMAT);
        Page template = portalObjectContainer.getObject(templateObjectId, Page.class);
        if (template == null) {
            templateObjectId = PortalObjectId.parse("/default/templates/publish", PortalObjectPath.CANONICAL_FORMAT);
        }
        String templateId = templateObjectId.toString(PortalObjectPath.SAFEST_FORMAT);

        // Page properties
        Map<String, String> properties = new HashMap<>(2);
        properties.put("osivia.cms.basePath", sharingRootPath);
        properties.put("osivia.cms.directContentPublisher", "1");

        // Command
        String parentId = portal.getId().toString(PortalObjectPath.SAFEST_FORMAT);
        String pageName = "sharing";
        Map<String, String> parameters = new HashMap<>(0);
        ControllerCommand command = new StartDynamicPageCommand(parentId, pageName, displayNames, templateId, properties, parameters);

        // Page identifier
        UpdatePageResponse response = (UpdatePageResponse) this.context.execute(command);
        PortalObjectId pageId = response.getPageId();

        return portalObjectContainer.getObject(pageId, Page.class);
    }


    /**
     *
     * Page de publication des contenus par défaut
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws InvocationException
     * @throws ControllerException
     */
    private Page getContentPublishPage(Portal portal, CMSItem cmsItem, String displayContext) throws UnsupportedEncodingException, IllegalArgumentException, InvocationException,
            ControllerException {
        Map<Locale, String> displayNames = new HashMap<Locale, String>();
        String displayName = cmsItem.getProperties().get("displayName");
        if (StringUtils.isNotEmpty(displayName)) {
            displayNames.put(Locale.FRENCH, displayName);
        }	else if(PROCEDURE_TYPE.equals(cmsItem.getProperties().get("type")))	{
        	// Procedures have no title
            Locale locale = this.getControllerContext().getServerInvocation().getRequest().getLocale();
            Bundle bundle = this.bundleFactory.getBundle(locale);
        	
        	displayNames.put(Locale.FRENCH, bundle.getString(PROCEDURE_LABEL_KEY));
        }

        Map<String, String> props = new HashMap<String, String>();

        props.put("osivia.cms.basePath", cmsItem.getNavigationPath());
        props.put("osivia.cms.directContentPublisher", "1");

        String templateName = "/default/templates/publish";
        if( "uncluttered".equals(displayContext)) {
            templateName = "/default/templates/uncluttered";
        }



        // V2.0-rc7
        // props.put("osivia.cms.pageContextualizationSupport", "0");
        props.put("osivia.cms.layoutType", CmsCommand.LAYOUT_TYPE_SCRIPT);
        props.put("osivia.cms.layoutRules", "return \""+templateName+"\"");




        StartDynamicPageCommand cmd = new StartDynamicPageCommand(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), "publish", displayNames,
                PortalObjectId.parse(templateName, PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), props,
                new HashMap<String, String>());

        PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(cmd)).getPageId();

        return (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);
    }


    /**
     * Page de publication des contenus par défaut.
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException
     * @throws InvocationException
     * @throws ControllerException
     */
    private Page getPortalSitePublishPage(PortalObject portal, CMSItem portalSite, String displayName) throws UnsupportedEncodingException,
            IllegalArgumentException, InvocationException, ControllerException {

        // No template
        if (portalSite.getProperties().get("pageTemplate") == null) {
            return null;
        }

        // No contextualization
        if (!"1".equals(portalSite.getProperties().get("contextualizeInternalContents"))) {
            return null;
        }


        String pageName = "portalSite"
                + (new CMSObjectPath(portalSite.getNavigationPath(), CMSObjectPath.CANONICAL_FORMAT)).toString(CMSObjectPath.SAFEST_FORMAT);

        Page publishPage = (Page) portal.getChild(pageName);
        if (publishPage == null) {
            Map<String, String> props = new HashMap<String, String>();

            props.put("osivia.cms.basePath", portalSite.getNavigationPath());

            // if(
            // "true".equals(portalSite.getProperties().get("contextualizeInternalContents")))

            // v 2.0.rc7
            // props.put("osivia.cms.pageContextualizationSupport", "1");


            /*
             * v 2.0.rc7
             * if ("1".equals(portalSite.getProperties().get("contextualizeExternalContents")))
             * props.put("osivia.cms.outgoingRecontextualizationSupport", "1");
             */
            props.put("osivia.cms.layoutType", CmsCommand.LAYOUT_TYPE_SCRIPT);
            props.put("osivia.cms.layoutRules", "return ECMPageTemplate;");
            props.put(TabGroup.NAME_PROPERTY, portalSite.getProperties().get(TabGroup.NAME_PROPERTY));
            props.put(TabGroup.MAINTAINS_PROPERTY, portalSite.getProperties().get(TabGroup.MAINTAINS_PROPERTY));


            Map<Locale, String> displayNames = new HashMap<Locale, String>();
            if (displayName != null) {
                displayNames.put(Locale.FRENCH, displayName);
            } else {
                displayNames.put(Locale.FRENCH, portalSite.getProperties().get("displayName"));
            }


            StartDynamicPageCommand cmd = new StartDynamicPageCommand(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), pageName, displayNames,
                    PortalObjectId.parse("/default/templates/publish", PortalObjectPath.CANONICAL_FORMAT).toString(PortalObjectPath.SAFEST_FORMAT), props,
                    new HashMap<String, String>());

            cmd.setCmsParameters(this.pageParams);


            PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(cmd)).getPageId();

            publishPage = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);
        }

        return publishPage;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            // Controller context
            ControllerContext controllerContext = this.getControllerContext();
            // Portal controller context
            PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);

            // CMS service
            ICMSService cmsService = getCMSService();

            // Profiler
            if (this.cmsPath != null) {
                controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.profiler.cmsPath", this.cmsPath);
            }

            

            String explicitNavigationPath = null;
            
            
            // Virtual staples
            String virtualStapleId =  VirtualNavigationUtils.getWebId(this.cmsPath);
            if (StringUtils.isNotEmpty(virtualStapleId)) {
                explicitNavigationPath = this.cmsPath;
                // Recompose webId path
                this.cmsPath = IWebIdService.CMS_PATH_PREFIX + "/" + virtualStapleId;
                
            }
            

            Page currentPage = null;
            Level level = null;

            // Récupération page
            if (this.pagePath != null) {
                PortalObjectId poid = PortalObjectId.parse(this.pagePath, PortalObjectPath.CANONICAL_FORMAT);
                currentPage = (Page) controllerContext.getController().getPortalObjectContainer().getObject(poid);

                if (currentPage == null) {
                    // La page n'est plus accessible, on tente de contextualiser dans le portail (cas de la perte de session)
                    this.contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTAL;
                }
            }

            Page contextualizationPage = null;

            // Current items informations
            CMSServiceCtx cmsReadItemContext = null;
            CMSItem cmsItem = null;
            CMSPublicationInfos pubInfos = null;

            /* Gestion de la contextualisation */

            // Contexualisation non précisée

            // if (contextualization == null && currentPage != null) {
            //
            // // COntextualization doesn't support non published items
            // if ("1".equals(displayLiveVersion))
            // contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTLET;
            //
            // }

            // LBI : refresh navigation when a cms page is created or edited
            if (IPortalUrlFactory.DISPLAYCTX_REFRESH.equals(this.displayContext) || InternalConstants.FANCYBOX_PROXY_CALLBACK.equals(this.displayContext)
                    || InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext)) {
                PageProperties.getProperties().setRefreshingPage(true);
            }

            // Decode webid paths if given
            if (StringUtils.startsWith(this.cmsPath, IWebIdService.CMS_PATH_PREFIX)) {
                this.cmsPath = this.getWebIdService().cmsPathToFetchPath(this.cmsPath);
            }


            // Lecture de l'item
            // Permet entre autre d'afficher un message d'erreur ou une redirection
            if (cmsReadItemContext == null) {
                cmsReadItemContext = new CMSServiceCtx();
                cmsReadItemContext.setControllerContext(controllerContext);


                if ("1".equals(this.displayLiveVersion)) {
                    cmsReadItemContext.setDisplayLiveVersion(this.displayLiveVersion);

                }


                // test si mode assistant activé
                if (level == Level.allowPreviewVersion) {
                    cmsReadItemContext.setDisplayLiveVersion("1");
                }


                if (InternalConstants.PROXY_PREVIEW.equals(this.displayContext)
                        || IPortalUrlFactory.DISPLAYCTX_PREVIEW_LIVE_VERSION.equals(this.displayContext)
                        || InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext)) {
                    cmsReadItemContext.setForcedLivePath(this.cmsPath);
                    controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT, this.cmsPath);
                }
            }


            // Lecture des informations de publication
            if (this.cmsPath != null) {
                try {
                    // Publication infos
                    pubInfos = getPublicationInfos(cmsReadItemContext, false);

                    // Le path eventuellement en ID a été retranscrit en chemin
                    this.cmsPath = pubInfos.getDocumentPath();

                    level = CmsPermissionHelper.getCurrentPageSecurityLevel(controllerContext, this.cmsPath);

                    // if access is denied, continue with the path of the last page visited the user will see a notification
                    if (level == Level.deny) {
                        throw new UserNotificationsException();
                    }
                } catch (CMSException e) {
                    if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                        return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                    }

                    if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                        return new UnavailableResourceResponse(this.cmsPath, false);
                    }

                    throw e;
                    // TODO : gerer les cas d'erreurs
                }

            }
            
            // Case of document saved as Draft:
            // its state must be updated
            // (no need to catch ERROR_FORBIDDEN and ERROR_NOTFOUND CMSException
            if(InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext)){
                if(pubInfos.isDraft() && pubInfos.isNotOrphanDraft()){
                    String docHavingDraftName = StringUtils.substringAfterLast(this.cmsPath, "/");
                    String docHavingDraftPath = pubInfos.getDraftContextualizationPath().concat("/").concat(docHavingDraftName);
                    
                    boolean forceReload = cmsReadItemContext.isForceReload();
                    cmsReadItemContext.setForceReload(true);
                    getCMSService().getContent(cmsReadItemContext, docHavingDraftPath);
                    cmsReadItemContext.setForceReload(forceReload);
                    
                }
            }
            
            // Les liens applicativement posés en LIVE sont interprétés en mode PROXY_PREVIEW
            // Requete vers des documents LIVE alors qu'ils sont dans un espace de publication
            if ("1".equals(this.displayLiveVersion)) {
                if ((pubInfos.getPublishSpacePath() != null) && !pubInfos.isLiveSpace()) {
                    // Affichage en mode preview pour les éléments d'une liste positionnée sur version live
                    this.displayContext = InternalConstants.PROXY_PREVIEW;
                    cmsReadItemContext.setForcedLivePath(this.cmsPath);
                    controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT, this.cmsPath);
                }
            }


            // Lecture de la configuration de l'espace
            CMSItem pagePublishSpaceConfig = getPagePublishSpaceConfig(controllerContext, currentPage);


            // Lecture de l'item
            if (this.cmsPath != null) {
                try {
                    // Attention, cet appel peut modifier si nécessaire le scope de cmsReadItemContext
                    if (level == Level.allowPreviewVersion) {
                        cmsReadItemContext.setDisplayLiveVersion("1");
                    }

                    cmsItem = cmsService.getContent(cmsReadItemContext, this.cmsPath);
                } catch (CMSException e) {
                    if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                        return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                    }

                    if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                        return new UnavailableResourceResponse(this.cmsPath, false);
                    }
                }
            }


            // Statistics
            if (StringUtils.isNotEmpty(this.cmsPath)) {
                this.statisticsService.incrementsUserStatistics(portalControllerContext, this.cmsPath);
            }


            // Adapatation des paths à la navigation pilotée par le contenu

            // Le path du contenu est le cmsPath
            this.contentPath = this.cmsPath;
            String itemPublicationPath = this.cmsPath;
            String virtualNavigationPath = null;
            String realNavigationPath = null;


            if( explicitNavigationPath != null) {
                itemPublicationPath = explicitNavigationPath;
                virtualNavigationPath = explicitNavigationPath;
                realNavigationPath = explicitNavigationPath.substring(0, explicitNavigationPath.lastIndexOf('/'));

            }   else    {
            
                if (cmsItem != null) {
                    cmsReadItemContext.setDoc(cmsItem.getNativeItem());
    
                    if (!"portal".equals(this.contextualization)) {
                        // Adapt navigation path
                        virtualNavigationPath = cmsService.getAdaptedNavigationPath(cmsReadItemContext);
                        if (virtualNavigationPath != null) {
                            realNavigationPath = virtualNavigationPath;
                            boolean realPath = false;
                            while (!realPath && StringUtils.isNotEmpty(realNavigationPath)) {
                                try {
                                    cmsService.getPublicationInfos(cmsReadItemContext, realNavigationPath);
                                    realPath = true;
                                } catch (CMSException e) {
                                    PortalObjectPath objectPath = PortalObjectPath.parse(realNavigationPath, PortalObjectPath.CANONICAL_FORMAT);
                                    realNavigationPath = objectPath.getParent().toString(PortalObjectPath.CANONICAL_FORMAT);
                                }
                            }
    
                            // FIXME
                            itemPublicationPath = virtualNavigationPath + "/_" + StringUtils.substringAfterLast(this.cmsPath, "/");
                        }
                    }
                }
            }
            
            if( realNavigationPath != null) {
                pubInfos = cmsService.getPublicationInfos(cmsReadItemContext, realNavigationPath);
            }


            if ("dashboard".equals(this.displayContext)) {

                Map<String, String> props = new HashMap<String, String>();

                Map<Locale, String> displayNames = new HashMap<Locale, String>();
                displayNames.put(Locale.FRENCH, cmsItem.getProperties().get("displayName"));

                Portal portal = null;

                if (currentPage != null) {
                    portal = currentPage.getPortal();
                } else {
                    // Pas de page courante, c'est un permalink
                    // On regarde si il est associé à un portail
                    if (this.portalPersistentName != null) {
                        portal = controllerContext.getController().getPortalObjectContainer().getContext().getPortal(this.portalPersistentName);
                    } else {
                        portal = controllerContext.getController().getPortalObjectContainer().getContext().getDefaultPortal();
                    }
                }

                String pageName = "procedureDashboard"
                        + (new CMSObjectPath(cmsItem.getNavigationPath(), CMSObjectPath.CANONICAL_FORMAT)).toString(CMSObjectPath.SAFEST_FORMAT);

                props.put("osivia.procedure.dashboard.path", cmsItem.getNavigationPath());

                StartDynamicPageCommand cmd = new StartDynamicPageCommand(portal.getId().toString(PortalObjectPath.SAFEST_FORMAT), pageName, displayNames,
                        PortalObjectId.parse("/default/templates/procedureDashboard", PortalObjectPath.CANONICAL_FORMAT).toString(
                                PortalObjectPath.SAFEST_FORMAT), props, new HashMap<String, String>());

                cmd.setCmsParameters(this.pageParams);

                return this.context.execute(cmd);
            }







            if ("detailedView".equals(this.displayContext)) {
                // Need navigation context to work
                if( currentPage != null)
                    this.contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTLET;
            }


            // Contextualisation du contenu dans la page ou dans le portail (une page dynamique est créée si aucune page n'est trouvée)
            boolean contextualizeinPage = false;
            boolean contextualizedInCurrentPage = false;

            if ((itemPublicationPath != null) && !IPortalUrlFactory.CONTEXTUALIZATION_PORTLET.equals(this.contextualization)) {

                if ((currentPage != null) && (currentPage.getProperty("osivia.cms.basePath") != null)) {

                    if (IPortalUrlFactory.CONTEXTUALIZATION_PAGE.equals(this.contextualization)) {
                        contextualizeinPage = true;
                    }

                    // v2.0-rc7
                    // if ("1".equals(currentPage.getProperty("osivia.cms.pageContextualizationSupport")))
                    if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {
                        contextualizeinPage = true;
                    }
                }

                if (contextualizeinPage) {
                    Page searchPage = null;

                    PortalObject po = currentPage;
                    while ((po instanceof Page) && (po.getDeclaredProperty("osivia.cms.basePath") == null)) {
                        po = po.getParent();
                    }
                    if ((po.getDeclaredProperty("osivia.cms.basePath") != null) && (po instanceof Page)) {
                        searchPage = (Page) po;
                    }

                    if (searchPage != null) {
                        String searchPath = itemPublicationPath;
                        if (virtualNavigationPath != null) {
                            searchPath = realNavigationPath;
                        }

                        PortalObject publicationPage = searchPublicationPage(controllerContext, searchPage, searchPath, this.getProfilManager());

                        if (publicationPage != null) {
                            contextualizationPage = (Page) publicationPage;
                            contextualizedInCurrentPage = true;
                        }
                    }

                    // Cas de la publication directe
                    // La page courante est reutilise si clique en mode contextualisation page (par exemple depuis le menu)
                    if (currentPage != null) {
                        if ("1".equals(currentPage.getProperty("osivia.cms.directContentPublisher"))) {
                            String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

                            if ((pageBasePath != null) && pageBasePath.equals(itemPublicationPath)) {
                                contextualizationPage = currentPage;
                            }
                        }
                    }
                }

                // Contextualisation dans le portail
                boolean contextualizeinPortal = false;

                if (contextualizationPage == null) {
                    if (IPortalUrlFactory.CONTEXTUALIZATION_PORTAL.equals(this.contextualization)) {
                        // contextualisation explicite dans le portail (lien de recontextualisation)
                        contextualizeinPortal = true;
                    }


                    if ("1".equals(cmsItem.getProperties().get("supportsOnlyPortalContextualization"))) {
                        contextualizeinPortal = true;
                    }

                    if (!contextualizeinPortal) {
                        // contextualisation iumplicite dans le portail (lien inter-contenus)
                        // on regarde comment est gérée la contextualisation des contenus externes dans la page
                        if (currentPage != null) {
                            // v 2.0-rc7
                            contextualizeinPortal = false;

                            // v2.1 : dafpic contextualisation portail sur les pages non contextualisés (template /publish)
                            if ("1".equals(currentPage.getProperty("osivia.cms.outgoingRecontextualizationSupport"))) {
                                contextualizeinPortal = true;
                            }

                            if (currentPage.getProperty("osivia.cms.basePath") != null) {

                                if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeExternalContents"))) {
                                    contextualizeinPortal = true;
                                }
                            }
                        } else {
                            // Pas de page dans le contexte, on contextualise dans le portail (exemple : permalink)
                            contextualizeinPortal = true;
                            
                            
                            // Cas des sites web 
                            // (ou on a par défaut pas de contexte de navigation et donc tout est permalien ...)
                            
                            // Si je n'ai pas de navigation virtuelle , il faut être capable d'afficher le contenu 
                            // On l'affiche alors dans la page d'accueil
                            if( virtualNavigationPath == null)    {
                                Portal portal = PortalObjectUtils.getPortal(controllerContext);
                                if( portal != null) {
                                    if (PortalObjectUtils.isSpaceSite( portal)) {
                                          String baseCMSPath = portal.getDefaultPage().getDeclaredProperty("osivia.cms.basePath");
                                          virtualNavigationPath = baseCMSPath;
                                          realNavigationPath = virtualNavigationPath;
                                          itemPublicationPath = virtualNavigationPath + "/_" + StringUtils.substringAfterLast(this.cmsPath, "/");
                                    }
                                }
                            }
                        }
                    }
                }


                if (contextualizeinPortal) {
                    Portal portal = null;

                    if (currentPage != null) {
                        portal = currentPage.getPortal();
                    } else {
                        // Pas de page courante, c'est un permalink
                        // On regarde si il est associé à un portail
                        if (this.portalPersistentName != null) {
                            portal = controllerContext.getController().getPortalObjectContainer().getContext().getPortal(this.portalPersistentName);
                        } else {
                            portal = controllerContext.getController().getPortalObjectContainer().getContext().getDefaultPortal();
                        }
                    }

                    String searchPath = itemPublicationPath;
                    if (virtualNavigationPath != null) {
                        searchPath = realNavigationPath;
                    }

                    PortalObject publicationPage = searchPublicationPage(controllerContext, portal, searchPath, this.getProfilManager());


                    // Redirected portal
                    Portal redirectedPortal;
                    if ((publicationPage != null) && (publicationPage instanceof Page)) {
                        Page page = (Page) publicationPage;
                        redirectedPortal = page.getPortal();
                    } else {
                        // Protected search path
                        String protectedSearchPath = searchPath + "/";
                        
                        // Root object
                        Context root = controllerContext.getController().getPortalObjectContainer().getContext();
                        // Portals
                        Collection<PortalObject> portals = root.getChildren(PortalObject.PORTAL_MASK);
                        Iterator<PortalObject> iterator = portals.iterator();
                        
                        // Redirected portal
                        redirectedPortal = null;
                        while (iterator.hasNext() && (redirectedPortal == null)) {
                            Portal testedPortal = (Portal) iterator.next();

                            // Portal domains
                            String[] domains = StringUtils.split(testedPortal.getDeclaredProperty("osivia.site.domains"), ", ");
                            if (ArrayUtils.isNotEmpty(domains)) {
                                for (int i = 0; i < domains.length; i++) {
                                    String domain = domains[i] + "/";
                                    if (StringUtils.startsWith(protectedSearchPath, domain)) {
                                        redirectedPortal = testedPortal;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    
                    // Controle du host
                    // Si le host est différent du host courant, on redirige sur le nouveau host
                    if (redirectedPortal != null) {
                        String host = redirectedPortal.getDeclaredProperty("osivia.site.hostName");
                        String reqHost = controllerContext.getServerInvocation().getServerContext().getClientRequest().getServerName();

                        if ((host != null) && !reqHost.equals(host)) {
                            String url = this.getUrlFactory().getPermaLink(portalControllerContext, null, null, itemPublicationPath,
                                    IPortalUrlFactory.PERM_LINK_TYPE_CMS);
                            url = url.replaceFirst(reqHost, host);
                            return new RedirectionResponse(url.toString());
                        }
                    }


                    if (publicationPage != null) {
                        contextualizationPage = (Page) publicationPage;
                    }

                    if (contextualizationPage == null) {
                        // Recherche de l'espace du publication pour instanciation dynamique
                        CMSServiceCtx userCtx = new CMSServiceCtx();

                        userCtx.setControllerContext(controllerContext);


                        CMSItem publishSpace = null;
                        try {
                            // publishSpace = getCMSService().getPortalPublishSpace(userCtx, cmsPath.toString());
                            if (pubInfos.getPublishSpacePath() != null) {
                                // juste pour tester les droits à l'espace
                                cmsService.getPublicationInfos(cmsReadItemContext, pubInfos.getPublishSpacePath());

                                publishSpace = cmsService.getSpaceConfig(cmsReadItemContext, pubInfos.getPublishSpacePath());
                            }
                        } catch (CMSException e) {
                            if ((e.getErrorCode() != CMSException.ERROR_FORBIDDEN) && (e.getErrorCode() != CMSException.ERROR_NOTFOUND)) {
                                throw e;
                            }
                        }

                        if (publishSpace != null) {
                            // Domain contextualization
                            String domainName = StringUtils.substringBefore(StringUtils.removeStart(publishSpace.getNavigationPath(), "/"), "/");
                            String domainPath = "/" + domainName;
                            DomainContextualization domainContextualization = cmsService.getDomainContextualization(userCtx, domainPath);

                            // Domain display name
                            String domainDisplayName = null;
                            if (domainContextualization != null) {
                                CMSItem domain = cmsService.getContent(userCtx, domainPath);
                                if (domain != null) {
                                    domainDisplayName = domain.getProperties().get("displayName");
                                }
                            }

                            contextualizationPage = this.getPortalSitePublishPage(portal, publishSpace, domainDisplayName);
                        }

                        // Create empty page if no current page spécified
                        if ((contextualizationPage == null) && (currentPage == null)) {
                            CMSItem sharingRoot = cmsService.getSharingRoot(cmsReadItemContext);
                            if (sharingRoot == null) {
                                contextualizationPage = this.getContentPublishPage(portal, cmsItem, displayContext);
                            } else {
                                contextualizationPage = this.getSharingPage(portal, sharingRoot.getNavigationPath());
                            }
                        }
                    }
                }
            }

            // Pas contextualisable dans la page ni dans le portail, on enchaine le portlet
            if (contextualizationPage == null) {
                this.contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTLET;
            }

            // Get base page if no explicit contextualization (appel direct de l'uri du contenu, par exemple à partir d'un menu)
            // TODO : A tester (je ne sais pas si ca sert encore)
            Page baseCMSPublicationPage = null;
            String basePublishPath = null;

            if (contextualizationPage != null) {
                baseCMSPublicationPage = contextualizationPage;
            }

            if (baseCMSPublicationPage != null) {
                basePublishPath = baseCMSPublicationPage.getDeclaredProperty("osivia.cms.basePath");
            }

            boolean isVirtualNavigation = (virtualNavigationPath != null);
            
            // Page state
            CmsPageState pageState = new CmsPageState(controllerContext, baseCMSPublicationPage, level, this.contextualization, cmsItem, itemPublicationPath,
                    basePublishPath, currentPage, this.pageParams, this.contentPath, pubInfos, contextualizedInCurrentPage, this.cmsPath, this.displayContext,
                    this.skipPortletInitialisation, isVirtualNavigation);
            UnavailableResourceResponse response = pageState.initState();
            if (response != null) {
                return response;
            }

            Page page = pageState.getPage();
            CMSItem cmsNav = pageState.getCmsNav();
            PortalObjectId pageIdToDiplay = pageState.getPageIdToDiplay();
            boolean isPageToDisplayUncontextualized = pageState.isPageToDisplayUncontextualized();


            // Traitement RSS ( à externaliser)
            if ("RSS".equals(this.displayContext)) {
                String windowPermReference = this.getWindowPermReference();

                // Seules sont gérées les listes
                if (windowPermReference != null) {
                    Collection<PortalObject> childs = page.getChildren(PortalObject.WINDOW_MASK);

                    for (PortalObject child : childs) {
                        if (windowPermReference.equals(child.getDeclaredProperty("osivia.rssLinkRef"))) {
                            if (child instanceof Window) {
                                Map<String, String[]> parameters = new HashMap<String, String[]>();

                                parameters.put("type", new String[]{"rss"});

                                // Les paramètres public ne sont pas vus au
                                // niveau des ressources
                                for (Map.Entry<String, String> entry : this.pageParams.entrySet()) {
                                    parameters.put(entry.getKey(), new String[]{entry.getValue()});
                                }

                                if (cmsNav != null) {
                                    parameters.put("osivia.cms.path", new String[]{cmsNav.getNavigationPath()});
                                }

                                ParameterMap params = new ParameterMap(parameters);

                                StateString state = ParametersStateString.create(params);

                                ControllerCommand cmd = new InvokePortletWindowResourceCommand(child.getId(), CacheLevel.PAGE, "rss", state, params);

                                return this.context.execute(cmd);
                            }
                        }
                    }
                }
            }

            // Manage ECM notifications
            String ecmActionReturn = this.getEcmActionReturn();
            if (StringUtils.isNotBlank(ecmActionReturn)) {
                String notification = itlzService.getString(ecmActionReturn, this.getControllerContext().getServerInvocation().getRequest().getLocale());
                notifService.addSimpleNotification(portalControllerContext, notification, NotificationsType.SUCCESS);
            }
            
            
            // Tracking service, #1150
            ITraceServiceLocator findMBean = Locator.findMBean(ITraceServiceLocator.class, ITraceServiceLocator.MBEAN_NAME);
            if(findMBean.getService() != null && findMBean.getService().enabled()) {
            	Trace t = new Trace(controllerContext.getServerInvocation().getServerContext().getClientRequest(), TRACE_CMS, cmsItem.getNativeItem());
            	findMBean.getService().trace(t);
            }
            

            // Doit-on afficher le contenu en mode MAXIMIZED ?
            boolean displayContent = false;
            boolean navigationPlayer = false;

            if (itemPublicationPath != null) {
                if (cmsNav == null) {
                    if (!isPageToDisplayUncontextualized) {
                        displayContent = true;
                    }
                } else {

                    if (!computeNavPath(itemPublicationPath).equals(cmsNav.getNavigationPath())) {
                        // Items détachés du path
                        displayContent = true;
                    } else {
                        if (!cmsNav.getNavigationPath().equals(basePublishPath)) {
                            if (!"1".equals(cmsNav.getProperties().get("pageDisplayMode"))) {
                                displayContent = true;
                                navigationPlayer = true;
                            }
                        }
                    }
                }

                if ("1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {
                    displayContent = true;
                }
            }

            if (displayContent) {
                // Affichage du contenu
                CMSItem cmsItemToDisplay = cmsItem;

                CMSServiceCtx handlerCtx = new CMSServiceCtx();

                handlerCtx.setControllerContext(controllerContext);
                handlerCtx.setScope(cmsReadItemContext.getScope());
                handlerCtx.setPageId(pageIdToDiplay.toString(PortalObjectPath.SAFEST_FORMAT));
                handlerCtx.setDisplayLiveVersion(cmsReadItemContext.getDisplayLiveVersion());
                handlerCtx.setForcedLivePath(cmsReadItemContext.getForcedLivePath());
                handlerCtx.setDoc(cmsItemToDisplay.getNativeItem());
                handlerCtx.setHideMetaDatas(this.getHideMetaDatas());
                handlerCtx.setDisplayContext(this.getDisplayContext());
                handlerCtx.setServletRequest(controllerContext.getServerInvocation().getServerContext().getClientRequest());

                if (contextualizationPage != null) {
                    // Ajout JSS 20130123 : les folders live affichés en mode direct
                    // plantent dans le DefaultCMSCustomier.createFolderRequest
                    if (!"1".equals(page.getProperty("osivia.cms.directContentPublisher"))) {
                        handlerCtx.setContextualizationBasePath(basePublishPath);
                    }
                    if ((pubInfos.getPublishSpacePath() != null) && pubInfos.isLiveSpace()) {
                        handlerCtx.setDisplayLiveVersion("1");
                    }

                }

                Player contentProperties = cmsService.getItemHandler(handlerCtx);

                if (contentProperties.getExternalUrl() != null) {
                    return new RedirectionResponse(contentProperties.getExternalUrl());
                }

                Map<String, String> windowProperties = contentProperties.getWindowProperties();


                // Copy handler context for ulterior refreshes
                CMSPlayHandlerUtils.saveHandlerProperties(handlerCtx, windowProperties);


                // No page params
                Map<String, String> params = new HashMap<String, String>();

                String addPortletToBreadcrumb = "0";
                if ((cmsNav == null) && !"0".equals(this.addToBreadcrumb)) {
                    addPortletToBreadcrumb = "1";
                }
                if (navigationPlayer == true) {
                    addPortletToBreadcrumb = "navigationPlayer";
                }

                if (cmsNav != null) {
                    windowProperties.put("osivia.dynamic.unclosable", "1");
                }

                if (contextualizationPage != null) {
                    windowProperties.put("osivia.cms.contextualization", "1");

                }


                if (windowProperties.get(Constants.WINDOW_PROP_URI) == null) {
                    windowProperties.put(Constants.WINDOW_PROP_URI, this.cmsPath);
                }


                // Edition state update
                PortalObjectId windowID = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString().concat("/").concat("CMSPlayerWindow"),
                        PortalObjectPath.CANONICAL_FORMAT));


                EditionState editionState = pageState.getEditionState(windowID, pubInfos, contextualizedInCurrentPage);

                // No automatic back, it's computed at CMS Level
                if (contextualizationPage != null) {
                    windowProperties.put("osivia.dynamic.disable.close", "1");
                }




                ControllerCommand command = new StartDynamicWindowCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "virtual",
                        contentProperties.getPortletInstance(), "CMSPlayerWindow", windowProperties, params, addPortletToBreadcrumb, editionState);
                ControllerResponse controllerResponse = this.context.execute(command);

                if (IPortalUrlFactory.PERM_LINK_TYPE_PORTLET_RESOURCE.equals(this.displayContext)) {
                    // Refresh page
                    page = controllerContext.getController().getPortalObjectContainer().getObject(page.getId(), Page.class);
                } else {
                    return controllerResponse;
                }
            }


            if (IPortalUrlFactory.PERM_LINK_TYPE_PORTLET_RESOURCE.equals(this.displayContext)) {
                // Page windows
                Collection<PortalObject> windows = page.getChildren(PortalObject.WINDOW_MASK);
                // Window
                PortalObject window = null;

                Iterator<PortalObject> iterator = windows.iterator();
                while ((window == null) && iterator.hasNext()) {
                    PortalObject portalObject = iterator.next();

                    if ("CMSPlayerWindow".equals(portalObject.getName())) {
                        window = portalObject;
                    }
                }

                ControllerResponse controllerResponse;
                if (window == null) {
                    // 404
                    controllerResponse = new UnavailableResourceResponse(this.cmsPath, false);
                } else {
                    // Parameters
                    Map<String, String[]> parameters = new HashMap<>();
                    if (MapUtils.isNotEmpty(this.pageParams)) {
                        for (Entry<String, String> entry : this.pageParams.entrySet()) {
                            parameters.put(entry.getKey(), new String[]{entry.getValue()});
                        }
                    }

                    StateString resourceState = ParametersStateString.create();
                    ParameterMap resourceForm = new ParameterMap(parameters);
                    ControllerCommand command = new InvokePortletWindowResourceCommand(window.getId(), CacheLevel.PAGE, this.windowPermReference, resourceState,
                            resourceForm);
                    
                    controllerResponse = this.context.execute(command);
                }

                return controllerResponse;
            }


            return new UpdatePageResponse(page.getId());
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


    /**
     * Get publication infos.
     * 
     * @param cmsContext CMS context
     * @param retry retry indicator
     * @return publication infos
     * @throws CMSException
     */
    private CMSPublicationInfos getPublicationInfos(CMSServiceCtx cmsContext, boolean retry) throws CMSException {
        // CMS service
        ICMSService cmsService = getCMSService();

        // Saved force reload indicator
        boolean forceReload = cmsContext.isForceReload();

        // Publication infos
        CMSPublicationInfos publicationInfos;

        try {
            // Content may have change ( for example, Content with webid may have been moved,)
            // In any case we recheck the main content of the page
            // No extra-cost because getPublicationInfos is optimized for performance
            cmsContext.setForceReload(true);

            publicationInfos = cmsService.getPublicationInfos(cmsContext, this.cmsPath);
        } catch (CMSException e) {
            if (!retry && (e.getErrorCode() == CMSException.ERROR_FORBIDDEN)) {
                // Reload session
                cmsService.reloadSession(cmsContext);

                // Retry
                publicationInfos = getPublicationInfos(cmsContext, true);
            } else {
                throw e;
            }
        } finally {
            cmsContext.setForceReload(forceReload);
        }

        return publicationInfos;
    }


    public void setSkipPortletInitialisation(boolean skipPortletInitialisation) {
        this.skipPortletInitialisation = skipPortletInitialisation;
    }

    public boolean isInsertPageMarker() {
        return this.insertPageMarker;
    }

    public void setInsertPageMarker(boolean insertPageMarker) {
        this.insertPageMarker = insertPageMarker;
    }

    public String getPortalPersistentName() {
        return this.portalPersistentName;
    }

    public String getAddToBreadcrumb() {
        return this.addToBreadcrumb;
    }

    public String getWindowPermReference() {
        return this.windowPermReference;
    }

    public Map<String, String> getPageParams() {
        return this.pageParams;
    }

    public void setPageParams(Map<String, String> pageParams) {
        this.pageParams = pageParams;
    }

    public String getCmsPath() {
        return this.cmsPath;
    }

    public void setCmsPath(String newCMSPath) {
        this.cmsPath = newCMSPath;
    }

    public String getPagePath() {
        return this.pagePath;
    }

    public String getDisplayContext() {
        return this.displayContext;
    }

    public void setDisplayContext(String displayContext) {
        this.displayContext = displayContext;
    }

    public String getHideMetaDatas() {
        return this.hideMetaDatas;
    }

    public String getContextualization() {
        return this.contextualization;
    }

    public String getEcmActionReturn() {
        return this.ecmActionReturn;
    }

    public void setEcmActionReturn(String ecmActionReturn) {
        this.ecmActionReturn = ecmActionReturn;
    }

    public String getItemScope() {
        return this.itemScope;
    }

    public void setItemScope(String itemScope) {
        this.itemScope = itemScope;
    }

    public String getScope() {
        return this.itemScope;
    }

    public String getDisplayLiveVersion() {
        return this.displayLiveVersion;
    }

}
