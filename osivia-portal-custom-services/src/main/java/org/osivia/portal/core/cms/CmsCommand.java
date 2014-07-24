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
package org.osivia.portal.core.cms;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
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
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.action.InvokePortletWindowResourceCommand;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.cache.CacheLevel;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.contribution.IContributionService.EditionState;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.contribution.ContributionService;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.error.UserNotificationsException;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.TabsCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.portalobjects.DynamicTemplatePage;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;
import org.osivia.portal.core.web.IWebIdService;

import bsh.Interpreter;

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


    @Override
    public CommandInfo getInfo() {
        return info;
    }

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

    private Map<String, String> pageParams;

    // HACK FOR RSS
    private Map<String, String> pictureParams;

    public Map<String, String> getPageParams() {
        return this.pageParams;
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
        return ecmActionReturn;
    }

    public void setEcmActionReturn(String ecmActionReturn) {
        this.ecmActionReturn = ecmActionReturn;
    }

    public CmsCommand() {
    }

    // TODO : supprimer parametre scope en 2.1 (pas d'impact car plus utilise)
    public CmsCommand(String pagePath, String cmsPath, Map<String, String> pageParams, String contextualization,
            String displayContext, String hideMetaDatas, String itemScope, String displayLiveVersion,
            String windowPermReference, String addToBreadcrumb, String portalPersistentName) {

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


    IProfilManager profilManager;


    private static ICMSServiceLocator cmsServiceLocator ;

    IPortalUrlFactory urlFactory;

    IWebIdService webIdService;

    public static ICMSService getCMSService() throws Exception {

        if( cmsServiceLocator == null){
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

    public IPortalUrlFactory getUrlFactory()throws Exception {

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
    
    public static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();
    
    public static INotificationsService notifService = NotificationsUtils.getNotificationsService();

    /**
     * compare 2 pages en fonction de leur path de publication
     * 
     */
    private static class PublicationComparator implements Comparator<PortalObject> {

        public int compare(PortalObject e1, PortalObject e2) {

            String p1 = e1.getDeclaredProperty("osivia.cms.basePath");
            String p2 = e1.getDeclaredProperty("osivia.cms.basePath");
            if (p1.length() == p2.length()) {
                return 0;
            }
            if (p1.length() < p2.length()) {
                return -1;
            }
            if (p1.length() > p2.length()) {
                return 1;
            }

            return 0;

        }

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

    private static PublicationComparator publicationComparator = new PublicationComparator();



    public static CMSItem getPagePublishSpaceConfig(ControllerContext ctx, PortalObject currentPage) throws Exception {

        CMSItem publishSpaceConfig = null;

        if( currentPage == null) {
            return null;
        }

        String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

        if (pageBasePath != null )	{

            CMSServiceCtx cmsReadItemContext = new CMSServiceCtx();
            cmsReadItemContext.setControllerContext(ctx);

            publishSpaceConfig =  getCMSService().getSpaceConfig(cmsReadItemContext, currentPage.getProperty("osivia.cms.basePath"));

        }
        return publishSpaceConfig;
    }


    public static boolean isContentAlreadyContextualizedInPage(ControllerContext ctx, PortalObject currentPage, String cmsPath) throws Exception {

        String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

        if (pageBasePath != null && cmsPath != null && cmsPath.contains(pageBasePath))	{


            CMSItem publishSpaceConfig =  getPagePublishSpaceConfig(ctx,  currentPage);

            if( publishSpaceConfig != null && "1".equals(publishSpaceConfig.getProperties().get("contextualizeInternalContents")))	{
                return true;
            }
        }

        return false;
    }






    public static String  computeNavPath(String path){
        String result = path;
        if( path.endsWith(".proxy")) {
            result = result.substring(0, result.length() - 6);
        }
        return result;
    }

    private static PortalObject searchPublicationPageForPub(CMSServiceCtx cmsCtx, CMSPublicationInfos pubInfos, PortalObject po,
            String searchPath, IProfilManager profilManager) throws Exception {

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
        for (PortalObject page : po.getChildren(PortalObject.PAGE_MASK)) {
            PortalObject child = searchPublicationPageForPub(cmsCtx, pubInfos, page, searchPath, profilManager);
            if (child != null) {
                return child;
            }
        }

        return null;

    }

    public static PortalObject searchPublicationPage(ControllerContext ctx, PortalObject po, String searchPath, IProfilManager profilManager) throws Exception	{

        CMSServiceCtx cmsReadItemContext = null;

        cmsReadItemContext = new CMSServiceCtx();
        cmsReadItemContext.setControllerContext(ctx);

        CMSPublicationInfos	pubInfos = getCMSService().getPublicationInfos(cmsReadItemContext, searchPath.toString());
        PortalObject page =  searchPublicationPageForPub(cmsReadItemContext, pubInfos,  po,  searchPath,  profilManager) ;


        // On regarde dans les autres portails
        if( po instanceof Portal && page == null){
            Collection<PortalObject> portals = ctx.getController().getPortalObjectContainer().getContext().getChildren(PortalObject.PORTAL_MASK);
            for(PortalObject portal: portals){
                // Uniquement si ils sont en mode publication satellite

                //				if( "satellite".equals(portal.getProperty("osivia.portal.publishingPolicy")))	{
                if( !portal.getId().equals(po.getId())) {
                    page = searchPublicationPageForPub( cmsReadItemContext, pubInfos,  portal,  searchPath,  profilManager) ;
                }
                if( page != null)
                {
                    break;
                    //				}
                }
            }
        }

        return page;

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
    private Page getContentPublishPage(Portal portal, CMSItem cmsItem) throws UnsupportedEncodingException, IllegalArgumentException,
    InvocationException, ControllerException {



        Page publishPage = (Page) portal.getChild("publish");
        // if (publishPage == null) {

        Map displayNames = new HashMap();
        displayNames.put(Locale.FRENCH, cmsItem.getProperties().get("displayName"));

        Map<String, String> props = new HashMap<String, String>();

        props.put("osivia.cms.basePath", cmsItem.getPath());
        props.put("osivia.cms.directContentPublisher", "1");

        //V2.0-rc7
        //props.put("osivia.cms.pageContextualizationSupport", "0");
        props.put("osivia.cms.layoutType", CmsCommand.LAYOUT_TYPE_SCRIPT);
        props.put("osivia.cms.layoutRules", "return \"/default/templates/publish\"");

        StartDynamicPageCommand cmd = new StartDynamicPageCommand(portal.getId().toString(
                PortalObjectPath.SAFEST_FORMAT), "publish", displayNames, PortalObjectId.parse(
                        "/default/templates/publish", PortalObjectPath.CANONICAL_FORMAT).toString(
                                PortalObjectPath.SAFEST_FORMAT), props, new HashMap<String, String>());

        PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(cmd)).getPageId();

        publishPage = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);
        // }

        return publishPage;
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
                + (new CMSObjectPath(portalSite.getPath(), CMSObjectPath.CANONICAL_FORMAT))
                .toString(CMSObjectPath.SAFEST_FORMAT);

        Page publishPage = (Page) portal.getChild(pageName);
        if (publishPage == null) {
            Map<String, String> props = new HashMap<String, String>();

            props.put("osivia.cms.basePath", portalSite.getPath());

            // if(
            // "true".equals(portalSite.getProperties().get("contextualizeInternalContents")))

            //  v 2.0.rc7
            //props.put("osivia.cms.pageContextualizationSupport", "1");


            /*
             * v 2.0.rc7
			if ("1".equals(portalSite.getProperties().get("contextualizeExternalContents")))
				props.put("osivia.cms.outgoingRecontextualizationSupport", "1");
             */
            props.put("osivia.cms.layoutType", CmsCommand.LAYOUT_TYPE_SCRIPT);
            props.put("osivia.cms.layoutRules", "return ECMPageTemplate;");


            Map displayNames = new HashMap();
            if( displayName != null)
                displayNames.put(Locale.FRENCH, displayName);
            else
                displayNames.put(Locale.FRENCH, portalSite.getProperties().get("displayName"));




            StartDynamicPageCommand cmd = new StartDynamicPageCommand(portal.getId().toString(
                    PortalObjectPath.SAFEST_FORMAT), pageName, displayNames, PortalObjectId.parse(
                            "/default/templates/publish", PortalObjectPath.CANONICAL_FORMAT).toString(
                                    PortalObjectPath.SAFEST_FORMAT), props, new HashMap<String, String>());

            PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(cmd)).getPageId();

            publishPage = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);
        }

        return publishPage;
    }

    @Override
    public ControllerResponse execute() throws ControllerException {

        try {
            // Controller context
            ControllerContext controllerContext = this.getControllerContext();
            
            // Profiler
            if( cmsPath != null)  {
                controllerContext.getServerInvocation().getServerContext().getClientRequest().setAttribute("osivia.profiler.cmsPath", cmsPath);
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

            CMSItem cmsNav = null;

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

            // decode webid paths if given
            if (cmsPath.startsWith(IWebIdService.PREFIX_WEBPATH)) {
                cmsPath = getWebIdService().pageUrlToFetchInfoService(cmsPath);
            }


            /*
             * Lecture de l'item
             * 
             * Permet entre autre d'afficher un message d'erreur ou une
             * redirection
             */

            if (cmsReadItemContext == null) {

                cmsReadItemContext = new CMSServiceCtx();
                cmsReadItemContext.setControllerContext(controllerContext);

                // test si mode assistant activé
                if (level == Level.allowPreviewVersion) {
                    cmsReadItemContext.setDisplayLiveVersion("1");
                }


                if (InternalConstants.PROXY_PREVIEW.equals(this.displayContext) || IPortalUrlFactory.DISPLAYCTX_PREVIEW_LIVE_VERSION.equals(this.displayContext)|| InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext)) {
                    cmsReadItemContext.setForcedLivePath(this.cmsPath);
                    controllerContext.setAttribute(Scope.REQUEST_SCOPE, InternalConstants.ATTR_LIVE_DOCUMENT, this.cmsPath);
                }

            }


            /* Lecture des informations de publication */
            if (this.cmsPath != null) {
                try {

                    level = CmsPermissionHelper.getCurrentPageSecurityLevel(controllerContext, this.cmsPath);

                    // if access is denied, continue with the path of the last page visited
                    // the user will see a notification
                    if (level == Level.deny) {
                        throw new UserNotificationsException();
                    }


                    // Attention, cet appel peut modifier si nécessaire le
                    // scope de cmsReadItemContext
                    pubInfos = getCMSService().getPublicationInfos(cmsReadItemContext, this.cmsPath.toString());

                    // Le path eventuellement en ID a été retranscrit en chemin
                    this.cmsPath = pubInfos.getDocumentPath();



                } catch (CMSException e) {

                    if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                        return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                    }

                    if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                        return new UnavailableResourceResponse(this.cmsPath, false);
                    }

                    throw e;
                    // TODO : gerer les cas d'erreurs
                    // return new
                    // UpdatePageResponse(currentPage.getId());
                }

            }


            // Lecture de la configuration de l'espace

            CMSItem pagePublishSpaceConfig = getPagePublishSpaceConfig(controllerContext, currentPage);


            /* Lecture de l'item */

            if (this.cmsPath != null) {
                try {
                    // Attention, cet appel peut modifier si nécessaire le
                    // scope de cmsReadItemContext
                    if (level == Level.allowPreviewVersion) {
                        cmsReadItemContext.setDisplayLiveVersion("1");
                    }

                    cmsItem = getCMSService().getContent(cmsReadItemContext, this.cmsPath);
                } catch (CMSException e) {
                    if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                        return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                    }

                    if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                        return new UnavailableResourceResponse(this.cmsPath, false);
                    }
                }
            }


            /* Adapatation des paths à la navigation pilotée par le contenu */

            // Le path du contenu est le cmsPath
            this.contentPath = this.cmsPath;
            String itemPublicationPath = this.cmsPath;
            String virtualNavigationPath = null;


            if( cmsItem != null){
                virtualNavigationPath = cmsItem.getProperties().get("navigationPath");
                if (virtualNavigationPath != null) {
                    //Le pub infos devient celui de la navigation
                    try {
                        pubInfos = getCMSService().getPublicationInfos(cmsReadItemContext, virtualNavigationPath);


                        // Le path eventuellement en ID a été retranscrit en chemin
                        itemPublicationPath = pubInfos.getDocumentPath() + "/" + StringUtils.substringAfterLast(this.cmsPath, "/");
                    } catch (CMSException e) {

                        if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                            return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                        }

                        if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                            return new UnavailableResourceResponse(this.cmsPath, false);
                        }

                        throw e;
                        // TODO : gerer les cas d'erreurs
                        // return new
                        // UpdatePageResponse(currentPage.getId());
                    }
                }
            }




            // }

            if( "detailedView".equals(this.displayContext))	{
                this.contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTLET;
            }

            /*
             * 
             * Contextualisation du contenu dans la page ou dans le portail (une
             * page dynamique est créée si aucune page n'est trouvée)
             */

            boolean contextualizeinPage = false;

            if (itemPublicationPath != null && !IPortalUrlFactory.CONTEXTUALIZATION_PORTLET.equals(this.contextualization)) {

                if (currentPage != null && currentPage.getProperty("osivia.cms.basePath") != null) {

                    if (IPortalUrlFactory.CONTEXTUALIZATION_PAGE.equals(this.contextualization)) {
                        contextualizeinPage = true;
                    }

                    // v2.0-rc7
                    //if ("1".equals(currentPage.getProperty("osivia.cms.pageContextualizationSupport")))
                    if( pagePublishSpaceConfig != null && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {
                        contextualizeinPage = true;
                    }

                }

                if (contextualizeinPage) {

                    // contextualizationPage = (Page) currentPage;

                    Page searchPage = null;

                    PortalObject po = currentPage;
                    while (po instanceof Page && po.getDeclaredProperty("osivia.cms.basePath") == null) {
                        po = po.getParent();
                    }
                    if (po.getDeclaredProperty("osivia.cms.basePath") != null && po instanceof Page) {
                        searchPage = (Page) po;
                    }

                    if (searchPage != null) {
                        String searchPath = itemPublicationPath;
                        if (virtualNavigationPath != null) {
                            searchPath = virtualNavigationPath;
                        }

                        PortalObject publicationPage = searchPublicationPage(controllerContext, searchPage, searchPath, this.getProfilManager());

                        if (publicationPage != null) {
                            contextualizationPage = (Page) publicationPage;
                        }
                    }

                    /*
                     * Cas de la publication directe
                     * 
                     * La page courante est reutilise si clique en mode
                     * contextualisation page (par exemple depuis le menu)
                     */

                    if (currentPage != null) {

                        if ("1".equals(currentPage.getProperty("osivia.cms.directContentPublisher"))) {
                            String pageBasePath = currentPage.getProperty("osivia.cms.basePath");

                            if (pageBasePath != null && pageBasePath.equals(itemPublicationPath)) {
                                contextualizationPage = currentPage;
                            }
                        }

                    }

                }

                /* Contextualisation dans le portail */

                boolean contextualizeinPortal = false;

                if (contextualizationPage == null) {



                    if (IPortalUrlFactory.CONTEXTUALIZATION_PORTAL.equals(this.contextualization)) {
                        // contextualisation explicite dans le portail (lien de recontextualisation)
                        contextualizeinPortal = true;
                    }


                    if( "1".equals(cmsItem.getProperties().get("supportsOnlyPortalContextualization"))) {
                        contextualizeinPortal = true;
                    }

                    if( ! contextualizeinPortal)

                    {
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

                                if (pagePublishSpaceConfig != null
                                        && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeExternalContents"))) {
                                    contextualizeinPortal = true;
                                }
                            } //else {
                            //								if ("1".equals(currentPage.getProperty("osivia.cms.outgoingRecontextualizationSupport")))
                            //									contextualizeinPortal = true;
                            //							}
                        }	else	{
                            // Pas de page, on contextualise dans le portail
                            // (exemple : permalink)
                            contextualizeinPortal = true;
                        }
                    }

                }

                if (contextualizeinPortal) {

                    /*
					Portal portal = getControllerContext().getController().getPortalObjectContainer().getContext()
							.getDefaultPortal();
                     */

                    Portal portal = null;

                    if( currentPage != null) {
                        portal = currentPage.getPortal();
                    } else	{
                        // Pas de page courante, c'est un permalink
                        // On regarde si il est associé à un portail
                        if( this.portalPersistentName != null) {
                            portal = controllerContext.getController().getPortalObjectContainer().getContext().getPortal(this.portalPersistentName);
                        } else {
                            portal = controllerContext.getController().getPortalObjectContainer().getContext().getDefaultPortal();
                        }
                    }

                    String searchPath = itemPublicationPath;
                    if (virtualNavigationPath != null) {
                        searchPath = virtualNavigationPath;
                    }

                    PortalObject publicationPage = searchPublicationPage(controllerContext, portal, searchPath, this.getProfilManager());



                    /* Controle du host
                     * 
                     *  si le host est différent du host courant, on redirige sur le nouveau host
                     */

                    if( publicationPage != null){
                        Portal pubPortal = ((Page) publicationPage).getPortal();
                        String host = pubPortal.getDeclaredProperty("osivia.site.hostName");
                        String reqHost = controllerContext.getServerInvocation().getServerContext().getClientRequest().getServerName();

                        if( host != null && !reqHost.equals(host))	{
                            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext);

                            String url = this.getUrlFactory().getPermaLink(
                                    portalCtx, null,
                                    null, itemPublicationPath, IPortalUrlFactory.PERM_LINK_TYPE_CMS);
                            url = url.replaceFirst(reqHost, host);
                            return new RedirectionResponse(url.toString());
                        }
                    }









                    if (publicationPage != null) {
                        contextualizationPage = (Page) publicationPage;
                    }

                    if (contextualizationPage == null) {

                        /*
                         * 
                         * Recherche de l'espace du publication pour instanciation dynamique
                         */

                        CMSServiceCtx userCtx = new CMSServiceCtx();

                        userCtx.setControllerContext(controllerContext);


                        CMSItem publishSpace = null;
                        try {
                            //publishSpace = getCMSService().getPortalPublishSpace(userCtx, cmsPath.toString());
                            if (pubInfos.getPublishSpacePath() != null) {
                                publishSpace =  getCMSService().getSpaceConfig(cmsReadItemContext, pubInfos.getPublishSpacePath());
                            }

                        } catch (CMSException e) {

                            if (e.getErrorCode() != CMSException.ERROR_FORBIDDEN
                                    && e.getErrorCode() != CMSException.ERROR_NOTFOUND) {
                                throw e;
                            }

                        }

                        if (publishSpace != null) {
                            
                            // Lecture du domaine pour affichage du nom
                            String pubDomain = TabsCustomizerInterceptor.getDomain(publishSpace.getPath());
                            String domainDisplayName = null;
                            
                            if( pubDomain != null) {
                                CMSItem domain = getCMSService().getContent(userCtx, "/" + pubDomain);
                                if( domain != null){
                                    domainDisplayName = domain.getProperties().get("displayName");
                                }
                            }
                            
                            contextualizationPage = getPortalSitePublishPage(portal, publishSpace, domainDisplayName);
                        }

                        // Create empty page if no current page spécified

                        if (contextualizationPage == null && currentPage == null) {

                            contextualizationPage = this.getContentPublishPage(portal, cmsItem);

                        }
                    }
                }

            }

            // Pas contextualisable dans la page ni dans le portail, on enchaine
            // le portlet
            if (contextualizationPage == null) {
                this.contextualization = IPortalUrlFactory.CONTEXTUALIZATION_PORTLET;
            }

            // Get base page if no explicit contextualization
            // (appel direct de l'uri du contenu, par exemple à partir d'un
            // menu)
            // TODO : A tester (je ne sais pas si ca sert encore)
            Page baseCMSPublicationPage = null;
            String basePublishPath = null;

            if (contextualizationPage != null) {
                baseCMSPublicationPage = contextualizationPage;
            }

            if (baseCMSPublicationPage != null) {
                basePublishPath = baseCMSPublicationPage.getDeclaredProperty("osivia.cms.basePath");
            }

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
            String ECMPageTemplate = null;
            String ECMPageTheme = null;
            String computedPageScope = null;

            String portalSiteScope = null;
            CMSServiceCtx cmsReadNavContext = new CMSServiceCtx();

            if (baseCMSPublicationPage != null) {
                portalSiteScope = baseCMSPublicationPage.getProperty("osivia.cms.navigationScope");
                cmsReadNavContext.setControllerContext(controllerContext);
                cmsReadNavContext.setScope(portalSiteScope);
                if (level == Level.allowPreviewVersion) {
                    cmsReadNavContext.setDisplayLiveVersion("1");
                }
            }

            if (baseCMSPublicationPage != null
                    && !IPortalUrlFactory.CONTEXTUALIZATION_PORTLET.equals(this.contextualization)) {

                if ("1".equals(baseCMSPublicationPage.getProperty("osivia.cms.directContentPublisher"))) {
                    /* publication directe d'un contenu sans le publishsite */
                    cmsNav = cmsItem;
                    // ECMPageTemplate =
                    // cmsItem.getProperties().get("pageTemplate");
                } else {

                    boolean errorDuringCheck = false;

                    String pathToCheck = itemPublicationPath;

                    do {

                        try {

                            CMSItem cmsItemNav = getCMSService().getPortalNavigationItem(cmsReadNavContext,
                                    basePublishPath, pathToCheck);

                            if (cmsItemNav == null) {
                                // Pb de droits, on coupe la branche
                                cmsNav = null;
                                ECMPageTemplate = null;
                                computedPageScope = null;
                            }

                            else {

                                boolean isNavigationElement = false;

                                String navigationElement = cmsItemNav.getProperties().get("navigationElement");

                                if ((pathToCheck.equals(basePublishPath) || "1".equals(navigationElement))) {
                                    isNavigationElement = true;
                                }

                                if (!isNavigationElement) {

                                    cmsNav = null;
                                    ECMPageTemplate = null;
                                    computedPageScope = null;

                                }

                                if (cmsNav == null && isNavigationElement) {
                                    cmsNav = cmsItemNav;
                                }

                                boolean computePageTemplate = true;


                                // Sur les pages statiques, on ignore le template par défaut

                                if (!(baseCMSPublicationPage instanceof DynamicTemplatePage)) {
                                    if( "1".equals(cmsItemNav.getProperties().get("defaultTemplate"))) {
                                        computePageTemplate = false;
                                    }

                                }

                                if (ECMPageTemplate == null) {
                                    boolean isChildPath = (itemPublicationPath.contains(pathToCheck))
                                            && !(itemPublicationPath.equalsIgnoreCase(pathToCheck));
                                    if (isChildPath) {
                                        String childrenPageTemplate = cmsItemNav.getProperties().get(
                                                "childrenPageTemplate");
                                        if (StringUtils.isNotEmpty(childrenPageTemplate)) {
                                            ECMPageTemplate = childrenPageTemplate;
                                        }
                                    }
                                }

                                if (computePageTemplate) {

                                    if (cmsItemNav.getProperties().get("pageTemplate") != null) {

                                        if (ECMPageTemplate == null) {
                                            ECMPageTemplate = cmsItemNav.getProperties().get("pageTemplate");
                                        }

                                    }
                                    
                                    if (cmsItemNav.getProperties().get("theme") != null) {
                                        if (ECMPageTheme == null) {
                                            ECMPageTheme = cmsItemNav.getProperties().get("theme");
                                        }
                                   }
                                    
                                }

                                if (computedPageScope == null) {
                                    if (cmsItemNav.getProperties().get("pageScope") != null) {
                                        computedPageScope = cmsItemNav.getProperties().get("pageScope");
                                    }
                                }

                            }

                            // One level up
                            CMSObjectPath parentPath = CMSObjectPath.parse(pathToCheck).getParent();
                            pathToCheck = parentPath.toString();

                        } catch (CMSException e) {
                            // Probleme d'acces aux items de navigation de
                            // niveau supérieur
                            // on decontextualise
                            errorDuringCheck = true;

                        }

                    } while (!errorDuringCheck && pathToCheck.contains(basePublishPath));

                    if (errorDuringCheck) {
                        // pb droits sur la navigation
                        // le contenu doit être publié dans la page mais sans
                        // tenir compte de son path ...
                        cmsNav = null;
                        disableCMSLocationInPage = true;
                        ECMPageTemplate = null;
                    }
                }
            }

            /* Recupération du layout */

            String layoutType = null;
            String layoutPath = null;

            if (baseCMSPublicationPage != null && cmsNav != null) {

                layoutType = baseCMSPublicationPage.getProperty("osivia.cms.layoutType");

                String layoutRules = null;

                if (layoutType == null) {
                    // On applique la gestion des layouts pour les sous-pages
                    // des espaces statiques

                    // Page statique
                    if (!(baseCMSPublicationPage instanceof DynamicTemplatePage)) {
                        // En mde navigation cms, toutes les pages sont dynamiques, meme la page d'accueil statique
                        // Sinon  les Sous-pages des pages statiques sont également dynamiques

                        // v2.0-rc7
                        if( ECMPageTemplate != null)	{
                            layoutType = CmsCommand.LAYOUT_TYPE_SCRIPT;
                            layoutRules = "return ECMPageTemplate;";
                        }

                    }
                } else {
                    // Gestion des layouts standards

                    if (CmsCommand.LAYOUT_TYPE_SCRIPT.equals(layoutType)) {
                        layoutRules = baseCMSPublicationPage.getProperty("osivia.cms.layoutRules");
                    }
                }

                if (CmsCommand.LAYOUT_TYPE_SCRIPT.equals(layoutType)) {

                    if (layoutRules != null) {

                        // Evaluation beanshell
                        Interpreter i = new Interpreter();
                        i.set("doc", cmsNav.getNativeItem());

                        i.set("ECMPageTemplate", ECMPageTemplate);

                        layoutPath = (String) i.eval(layoutRules);

                        if (layoutPath == null) {
                            if ((baseCMSPublicationPage instanceof CMSTemplatePage)) {
                                layoutPath = baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                            }
                        } else {

                            if (!layoutPath.startsWith("/")) {
                                // Path Relatif
                                layoutPath = baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT)
                                        + "/" + layoutPath;
                            }
                        }
                    } else {
                        layoutPath = baseCMSPublicationPage.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                    }
                }
            }

            /* Get real page to display */

            PortalObjectId pageIdToDiplay = null;

            if (baseCMSPublicationPage != null) {

                if (layoutPath != null) {
                    pageIdToDiplay = new PortalObjectId("", baseCMSPublicationPage.getId().getPath()
                            .getChild(CMSTemplatePage.PAGE_NAME));

                } else {
                    pageIdToDiplay = baseCMSPublicationPage.getId();

                    // œpagePath =
                    // basePage.getId().toString(PortalObjectPath.CANONICAL_FORMAT);

                }

                if (cmsNav == null) {
                    // cmsNav = getCMSService().getContent(cmsReadNavContext,
                    // baseCMSPublicationPath);
                    cmsNav = getCMSService().getPortalNavigationItem(cmsReadNavContext, basePublishPath,
                            basePublishPath);
                }

            } else {
                pageIdToDiplay = currentPage.getId();
            }

            /*
             * 
             * Préparation des paramètres de la page
             */

            if (cmsNav != null) {

                /* Mise à jour paramètre public page courante */

                NavigationalStateContext nsContext = (NavigationalStateContext) this.context
                        .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                PageNavigationalState previousPNS = nsContext.getPageNavigationalState(pageIdToDiplay.toString());

                //
                Map<QName, String[]> state = new HashMap<QName, String[]>();

                // Clone the previous state if needed

                /*
                 * if (previousPNS != null) {
                 * state.putAll(previousPNS.getParameters()); }
                 */

                if (this.pageParams != null) {
                    for (Map.Entry<String, String> entry : this.pageParams.entrySet()) {
                        state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()),
                                new String[] { entry.getValue() });
                    }
                }

                // Mise à jour du path de navigation

                if (itemPublicationPath.startsWith(basePublishPath) && !disableCMSLocationInPage) {
                    //String relPath = computeNavPath(cmsPath.substring(basePublishPath.length()));
                    String relPath = itemPublicationPath.substring(basePublishPath.length());
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.itemRelPath"),
                            new String[] { relPath });
                }

                state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"), new String[] { cmsNav.getPath() });

                String webId = cmsNav.getWebId();

                if (webId != null) {
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.webid"), new String[]{cmsNav.getWebId()});
                }


                // Mise à jour du path de contenu
                state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"),
                        new String[] { this.contentPath });



                /* Le path CMS identifie de manière unique la session
                 * puisqu'il s'agit d'une nouvelle page
                 * 
                 *  */

                state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.uniqueID"), new String[] { ""+System.currentTimeMillis() });

                if (layoutPath != null) {
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.layout_path"),
                            new String[] { layoutPath });
                }
                
                if( layoutPath != null && ECMPageTheme != null){
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.theme_path"),
                            new String[] { ECMPageTheme });
                }

                // Mise à jour du scope de la page
                if (computedPageScope != null && computedPageScope.length() > 0) {
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pageScope"),
                            new String[] { computedPageScope });
                }

                
                if (InternalConstants.PROXY_PREVIEW.equals(this.displayContext) || IPortalUrlFactory.DISPLAYCTX_PREVIEW_LIVE_VERSION.equals(this.displayContext) || (InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext) && !pubInfos.isLiveSpace())) {
                    state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.pagePreviewPath"),
                            new String[] { this.contentPath });
                }
                
                
                nsContext.setPageNavigationalState(pageIdToDiplay.toString(), new PageNavigationalState(state));

                /*
                 * Layout has been computed and copied to page state It's time
                 * to initialize dynamic page
                 */

                if (layoutPath != null) {
                    DynamicPortalObjectContainer.clearCache();

                    // TODO : remettre le cmsedition à null

                    PageProperties.getProperties().getPagePropertiesMap().remove("osivia.fetchedPortalProperties");
                }
            }

            /* Instanciation de la page */

            Page page = (Page) controllerContext.getController().getPortalObjectContainer()
                    .getObject(pageIdToDiplay);

            if (page == null) {
                page = (Page) controllerContext.getController().getPortalObjectContainer()
                        .getObject(currentPage.getId());
            }

            if (page == null) {
                if (layoutPath != null) {
                    logger.error("Le template " + layoutPath + " n'a pas pu être instancié");
                }
                return new UnavailableResourceResponse(itemPublicationPath, false);
            }



            boolean isPageToDisplayUncontextualized = false;

            if( cmsNav == null) {

                // Pb sur la navigation, on affiche le contenu sauf si le contenu est la page
                // auquel cas, on reste sur la page (cas d'une page mal définie pointant vers un path cms qui n'est pas un espace de publication)
                if(itemPublicationPath != null && itemPublicationPath.equals(page.getProperty("osivia.cms.basePath"))) {
                    isPageToDisplayUncontextualized = true;
                }
            }



            if ( ((cmsNav != null || isPageToDisplayUncontextualized )) && !this.skipPortletInitialisation ){

                /* Reinitialisation des renders parameters et de l'état */

                Iterator i = page.getChildren(Page.WINDOW_MASK).iterator();

                while (i.hasNext()) {

                    Window window = (Window) i.next();

                    NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());

                    WindowNavigationalState windowNavState = WindowNavigationalState.create();

                    // On la force en vue NORMAL
                    WindowNavigationalState newNS = WindowNavigationalState.bilto(windowNavState, WindowState.NORMAL,
                            windowNavState.getMode(), ParametersStateString.create());
                    controllerContext.setAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey, newNS);
                }

                // Réinitialisation des états des fenêtres
                Collection windows = page.getChildren(PortalObject.WINDOW_MASK);

                // Mode normal
                PageCustomizerInterceptor.unsetMaxMode(windows, controllerContext);

            }

            /**
             * Traitement RSS ( à externaliser)
             */

            if ("RSS".equals(this.displayContext)) {

                String windowPermReference = this.getWindowPermReference();

                // Seules sont gérées les listes

                if (windowPermReference != null) {

                    Collection<PortalObject> childs = page.getChildren(PortalObject.WINDOW_MASK);

                    for (PortalObject child : childs) {

                        if (windowPermReference.equals(child.getDeclaredProperty("osivia.rssLinkRef"))) {

                            if (child instanceof Window) {

                                Map<String, String[]> parameters = new HashMap<String, String[]>();

                                parameters.put("type", new String[] { "rss" });

                                // Les paramètres public ne sont pas vus au
                                // niveau des ressources
                                for (Map.Entry<String, String> entry : this.pageParams.entrySet()) {
                                    parameters.put(entry.getKey(), new String[] { entry.getValue() });

                                }

                                if (cmsNav != null) {
                                    parameters.put("osivia.cms.path", new String[] { cmsNav.getPath() });
                                }

                                ParameterMap params = new ParameterMap(parameters);

                                StateString state = ParametersStateString.create(params);

                                ControllerCommand cmd = new InvokePortletWindowResourceCommand(child.getId(),
                                        CacheLevel.PAGE, "rss", state, params);

                                return this.context.execute(cmd);

                            }
                        }
                    }
                }
            }




            /* Doit-on afficher le contenu en mode MAXIMIZED ? */

            boolean displayContent = false;
            boolean navigationPlayer = false;

            if (itemPublicationPath != null) {
                if (cmsNav == null)	{
                    if( !isPageToDisplayUncontextualized) {
                        displayContent = true;
                    }
                }
                else {

                    if (!computeNavPath(itemPublicationPath).equals(cmsNav.getPath())) {
                        // Items détachés du path
                        displayContent = true;
                    } else {

                        if (!cmsNav.getPath().equals(basePublishPath)) {
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



                /* Affichage du contenu */

                CMSItem cmsItemToDisplay = cmsItem;

                CMSServiceCtx handlerCtx = new CMSServiceCtx();

                handlerCtx.setControllerContext(controllerContext);
                handlerCtx.setScope(cmsReadItemContext.getScope());
                handlerCtx.setPageId(pageIdToDiplay.toString(PortalObjectPath.SAFEST_FORMAT));
                handlerCtx.setDisplayLiveVersion(this.displayLiveVersion);
                handlerCtx.setDoc(cmsItemToDisplay.getNativeItem());
                handlerCtx.setHideMetaDatas(this.getHideMetaDatas());
                handlerCtx.setDisplayContext(this.getDisplayContext());

                if (contextualizationPage != null) {
                    // Ajout JSS 20130123 : les folders live affichés en mode direct
                    // plantent dans le DefaultCMSCustomier.createFolderRequest
                    if (!"1".equals(page.getProperty("osivia.cms.directContentPublisher")))	{
                        handlerCtx.setContextualizationBasePath(basePublishPath);
                    }
                    if( pubInfos.getPublishSpacePath() != null && pubInfos.isLiveSpace()) {
                        handlerCtx.setDisplayLiveVersion("1");
                    }

                }

                CMSHandlerProperties contentProperties = getCMSService().getItemHandler(handlerCtx);

                if (contentProperties.getExternalUrl() != null) {
                    return new RedirectionResponse(contentProperties.getExternalUrl());
                }

                Map<String, String> windowProperties = contentProperties.getWindowProperties();


                // Copy handler context for ulterior refreshes
                CMSPlayHandlerUtils.saveHandlerProperties(handlerCtx, windowProperties);


                // No page params

                Map<String, String> params = new HashMap<String, String>();

                String addPortletToBreadcrumb = "0";
                if (cmsNav == null && !"0".equals(this.addToBreadcrumb)) {
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
                
                
                
                /* Edition state update */
                

                EditionState editionState =  null;
                
                
                PortalObjectId windowID = new PortalObjectId("", new PortalObjectPath(page.getId().getPath().toString().concat("/")
                        .concat("CMSPlayerWindow"), PortalObjectPath.CANONICAL_FORMAT));
                
                
                
                // Default initialization of editionState
                if( pubInfos.isLiveSpace()){
                    editionState = new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, this.cmsPath);
                }   else    {
                    if( InternalConstants.PROXY_PREVIEW.equals(this.displayContext)  || IPortalUrlFactory.DISPLAYCTX_PREVIEW_LIVE_VERSION.equals(this.displayContext)|| InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext))
                        editionState = new EditionState(EditionState.CONTRIBUTION_MODE_EDITION, this.cmsPath);
                    else
                        editionState = new EditionState(EditionState.CONTRIBUTION_MODE_ONLINE, this.cmsPath);
                }
                    
                // Restore navigation values   
                EditionState oldState = ContributionService.getWindowEditionState(getControllerContext(), windowID);
                if (oldState != null) {
                    if (oldState.getDocPath().equals(pubInfos.getDocumentPath())) {
                        editionState.setBackPageMarker(oldState.getBackPageMarker());
                        editionState.setHasBeenModified(oldState.isHasBeenModified());
                    }
                }    
                
                // Modified indicator
                boolean fancyBox = InternalConstants.FANCYBOX_PROXY_CALLBACK.equals(this.displayContext) || InternalConstants.FANCYBOX_LIVE_CALLBACK.equals(this.displayContext);
                
                if (fancyBox) {
                    editionState.setHasBeenModified(true);
                }   
                
                // page marker initialization ( means an applicative back button)
                if( ! "breadcrumb".equals(this.displayContext) && ! "menu".equals(displayContext) && !fancyBox && !InternalConstants.PROXY_PREVIEW.equals(this.displayContext))  {
                    String backCMSPageMarker = (String) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "controlledPageMarker");
                    editionState.setBackPageMarker(backCMSPageMarker);
                }
                
                // No automatic back, it's computed at CMS Level
                if( contextualizationPage != null)
                    windowProperties.put("osivia.application.close_url", "1");                
                
                String ecmActionReturn = getEcmActionReturn();
                if(StringUtils.isNotBlank(ecmActionReturn)){
                    PortalControllerContext portalCtx = new PortalControllerContext(controllerContext);
                    String notification = itlzService.getString(ecmActionReturn, getControllerContext().getServerInvocation().getRequest().getLocale());
                    notifService.addSimpleNotification(portalCtx, notification, NotificationsType.SUCCESS);
                }

                StartDynamicWindowCommand cmd = new StartDynamicWindowCommand(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "virtual",
                        contentProperties.getPortletInstance(), "CMSPlayerWindow", windowProperties, params, addPortletToBreadcrumb, editionState);


                return this.context.execute(cmd);
            }

            return new UpdatePageResponse(page.getId());

        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(e);
        }

    }

}
