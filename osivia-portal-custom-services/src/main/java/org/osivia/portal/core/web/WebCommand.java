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
package org.osivia.portal.core.web;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.server.ServerInvocation;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.urls.ExtendedParameters;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.dynamic.DynamicCommand;
import org.osivia.portal.core.dynamic.StartDynamicWindowCommand;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.page.PagePathUtils;

/**
 *
 * Format unifié d'url pour les PORTAL_SITE
 *
 * @author jeanseb
 *
 */
public class WebCommand extends DynamicCommand {

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(WebCommand.class);

    public static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();


    @Override
    public CommandInfo getInfo() {
        return info;
    }


    private String webPath;
    private String windowName;
    private boolean supportingPageMarker = true;
    private ExtendedParameters extendedParameters;



    public boolean isSupportingPageMarker() {
        return this.supportingPageMarker;
    }



    public void setSupportingPageMarker(boolean supportingPageMarker) {
        this.supportingPageMarker = supportingPageMarker;
    }


    public String getWindowName() {
        return this.windowName;
    }


    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public String getWebPath() {
        return this.webPath;
    }

    public WebCommand() {
    }

    public WebCommand(String webPath) {

        this.webPath = webPath;
    }

    public ExtendedParameters getExtendedParameters() {
        return this.extendedParameters;
    }

    public void setExtendedParameters(ExtendedParameters extendedParameters) {
        this.extendedParameters = extendedParameters;
    }


    private static ICMSServiceLocator cmsServiceLocator;

    IPortalUrlFactory urlFactory;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }


    public IPortalUrlFactory getUrlFactory() throws Exception {

        if (this.urlFactory == null) {
            this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        }

        return this.urlFactory;
    }


    ControllerResponse pageResponse;




    private ControllerResponse getPageResponse(ControllerContext controllerCtx) throws InvocationException, ControllerException, Exception {
        if (this.pageResponse == null) {

            // Transformation du requestpath
            CmsCommand cmsCmd = new CmsCommand();
            cmsCmd.setExtendedParameters(this.extendedParameters);

            // Case of possible Many Remote proxies
            
            //FIXME: move to csmCommand because /cms/_webId url may be unconsistent
            
            if (this.extendedParameters == null) {
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setControllerContext(controllerCtx);
                
                String pathToFetch = this.webPath;
                
                if (pathToFetch.length() <= 1) {
                    pathToFetch = WebURLFactory.getWebPortalBasePath(controllerCtx);
                }   else    {
                    pathToFetch =  WebIdService.PREFIX_WEBID_FETCH_PUB_INFO.concat(pathToFetch);
                }
                
                
                CMSPublicationInfos publicationInfos = getCMSService().getPublicationInfos(cmsContext,pathToFetch);

                if (publicationInfos.hasManyPublications()) {
                    PortalObjectId pageId = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
                    Page currentPage = (Page) controllerCtx.getController().getPortalObjectContainer().getObject(pageId);
                    if (currentPage != null) {
                        return this.displayManyPublications(currentPage, publicationInfos.getDocumentPath());
                    }
                }
            }

            // CMS path
            // Must reload (webId may have be moved, so cms path might be false)
            String cmsPath = WebURLFactory.adaptWebURLToCMSPath(controllerCtx, this.webPath, this.extendedParameters, true);
            cmsCmd.setCmsPath(cmsPath);


            // Page parameters
            Map<String, String> pageParameters = new HashMap<String, String>();
            cmsCmd.setPageParams(pageParameters);

            // Query parameters
            ParameterMap parameterMap = controllerCtx.getServerInvocation().getServerContext().getQueryParameterMap();

            // Search query and filters parameters
            if (parameterMap.containsKey("q") || parameterMap.containsKey("f")) {
                // Selectors
                Map<String, List<String>> selectors = PageParametersEncoder.decodeProperties(null);

                // Search query
                String[] searchQueryParameter = parameterMap.get("q");
                if (ArrayUtils.isNotEmpty(searchQueryParameter)) {
                    String searchQuery = URLDecoder.decode(searchQueryParameter[0], CharEncoding.UTF_8);
                    pageParameters.put("osivia.keywords", searchQuery);

                    // Value
                    selectors.put("q", Arrays.asList(StringUtils.split(searchQuery)));
                }

                // Search filters
                String[] searchFiltersParameter = parameterMap.get("f");
                if (ArrayUtils.isNotEmpty(searchFiltersParameter)) {
                    String searchFilters = URLDecoder.decode(searchFiltersParameter[0], CharEncoding.UTF_8);

                    // Values
                    String[] filters = StringUtils.split(searchFilters, "&");
                    for (String filter : filters) {
                        String[] selector = StringUtils.split(filter, "=");
                        if (selector.length == 2) {
                            selectors.put(selector[0], Arrays.asList(new String[]{selector[1]}));
                        }
                    }
                }

                pageParameters.put("selectors", PageParametersEncoder.encodeProperties(selectors));
            }

            // Page template parameter
            if (parameterMap.containsKey("page")) {
                String[] pageParameter = parameterMap.get("page");
                if (ArrayUtils.isNotEmpty(pageParameter)) {
                    String page = URLDecoder.decode(pageParameter[0], CharEncoding.UTF_8);
                    pageParameters.put("osivia.template", page);
                }
            }


            this.pageResponse = controllerCtx.execute(cmsCmd);
        }

        return this.pageResponse;
    }


    /**
     * Checks and returns the correct PageId.
     * If necessary, executes a cmsCommand
     * 
     * @param controllerCtx
     * @return
     * @throws InvocationException
     * @throws ControllerException
     * @throws Exception
     */
    private PortalObjectId getPageId(ControllerContext controllerCtx) throws InvocationException, ControllerException, Exception {

       // Transformation du requestpath
       CmsCommand cmsCmd = new CmsCommand();
       cmsCmd.setExtendedParameters(this.extendedParameters);

       String cmsPath = WebURLFactory.adaptWebURLToCMSPath(controllerCtx, this.webPath, this.extendedParameters, false);
       cmsCmd.setCmsPath(cmsPath);


        // on regarde si la page courante pointe déja sur le contenu
        PortalObjectId pageId = (PortalObjectId) controllerCtx.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
        if( pageId != null){
            String currentNavPath = PagePathUtils.getNavigationPath(controllerCtx, pageId);

            // for resources, there is no currentNavPath. otherwise test that navpath = cmspath
            if (currentNavPath != null) {
                if (!currentNavPath.equals(cmsPath)) {
                    // Le path de navigation a changé, il faut recréer la page technique
                    pageId = null;
                }
            }
         }

        if( pageId == null ){
                // Pour obtenir la page de contextualisation courante
            ControllerResponse ctrlResp = this.getPageResponse(controllerCtx);

            if( ctrlResp instanceof UpdatePageResponse) {
                pageId = ((UpdatePageResponse) ctrlResp).getPageId();
            }
        }

        return pageId;

    }


    public PortalObjectId getWindowId( ControllerContext controllerCtx)  {

        try {

        if (this.windowName != null) {

             PortalObjectId pageId = this.getPageId(controllerCtx );

             PortalObjectId windowID =  new PortalObjectId("", new PortalObjectPath( pageId.getPath().toString().concat("/").concat(this.windowName), PortalObjectPath.CANONICAL_FORMAT));
             return windowID;
         }

        return null;
        } catch( Exception e){
            throw new RuntimeException(e);
        }
    }



    @Override
    public ControllerResponse execute() throws ControllerException {

        try {

            PortalObjectId windowID = this.getWindowId( this.context);

            if( windowID != null)   {

                String originalPath = "/portal" + windowID;

                // create original command

                // remove non specific parameters

                ServerInvocation invocation = this.getControllerContext().getServerInvocation();


                ControllerCommand originalCmd = this.getControllerContext()
                        .getController()
                        .getCommandFactory()
                        .doMapping(this.getControllerContext(), invocation, invocation.getServerContext().getPortalHost(),
                                invocation.getServerContext().getPortalContextPath(), originalPath);




                return this.context.execute(originalCmd);

            }

            // Affichage de la commande CMS
            return this.getPageResponse(this.context);


        } catch (Exception e) {

            if (e instanceof CMSException) {
                CMSException cmsException = (CMSException) e;
                if (cmsException.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                    return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
                }

                if (cmsException.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                    return new UnavailableResourceResponse(this.webPath, false);
                }
            }

            throw new ControllerException(e);
        }

    }

    /**
     * Display the publications list for current document.
     *
     * @param currentPage
     * @return ControllerResponse
     * @throws ControllerException
     */
    private ControllerResponse displayManyPublications(Page currentPage, String currentPath) throws ControllerException {

        String pageId = currentPage.getId().toString(PortalObjectPath.SAFEST_FORMAT);
        String portletInstance = "toutatice-portail-cms-nuxeo-viewDocumentPortletInstance";
        Map<String, String> windowProperties = new HashMap<String, String>(1);
        windowProperties.put(Constants.WINDOW_PROP_URI, currentPath.toString());
        windowProperties.put("osivia.document.onlyRemoteSections", "true");
        windowProperties.put("osivia.document.remoteSectionsPage", "true");
        windowProperties.put("osivia.cms.contextualization", "1");

        windowProperties.put("osivia.hideTitle", "1");
        String title = itlzService.getString("SECTIONS_PORTLET_LINK", this.getControllerContext().getServerInvocation().getRequest().getLocale());
        windowProperties.put("osivia.title", title);

        StartDynamicWindowCommand windowCmd = new StartDynamicWindowCommand(pageId, "virtual", portletInstance, "PlayerPublicationsWindow", windowProperties, new HashMap<String, String>(), "1", null);

        return this.context.execute(windowCmd);
    }


}
