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
package org.osivia.portal.core.web;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.mapper.CommandFactory;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.page.PageParametersEncoder;
import org.osivia.portal.api.urls.ExtendedParameters;
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
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Web command.
 *
 * @author Jean-Sébastien Steux
 * @see DynamicCommand
 */
public class WebCommand extends DynamicCommand {

    /** Web path. */
    private final String webPath;
    /** Command info. */
    private final CommandInfo info;

    /** WebId service. */
    private final IWebIdService webIdService;
    /** Web URL service. */
    private final IWebUrlService webUrlService;
    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;


    /** Window name. */
    private String windowName;
    /** Supporting page marker. */
    private boolean supportingPageMarker;
    /** Extended parameters. */
    private ExtendedParameters extendedParameters;
    /** Page response. */
    private ControllerResponse pageResponse;


    /**
     * Constructor.
     *
     * @param webPath web path
     */
    public WebCommand(String webPath) {
        super();
        this.webPath = webPath;

        this.supportingPageMarker = true;
        this.info = new ActionCommandInfo(false);

        this.webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);
        this.webUrlService = Locator.findMBean(IWebUrlService.class, IWebUrlService.MBEAN_NAME);
        this.internationalizationService = InternationalizationUtils.getInternationalizationService();
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * Get page response.
     *
     * @param controllerContext controller context
     * @return controller response
     * @throws Exception
     */
    private ControllerResponse getPageResponse(ControllerContext controllerContext) throws Exception {
        if (this.pageResponse == null) {
            // Transformation du requestpath
            CmsCommand cmsCommand = new CmsCommand();
            cmsCommand.setExtendedParameters(this.extendedParameters);

            // Case of possible Many Remote proxies
            // FIXME: move to csmCommand because /cms/_webId url may be unconsistent
            if (this.extendedParameters == null) {
                // CMS service
                ICMSService cmsService = this.cmsServiceLocator.getCMSService();
                // CMS context
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setControllerContext(controllerContext);
                if (CmsPermissionHelper.getCurrentCmsVersion(controllerContext).equals(CmsPermissionHelper.CMS_VERSION_PREVIEW)) {
                    cmsContext.setDisplayLiveVersion("1");
                }

                // Base path
                String basePath = WebURLFactory.getWebPortalBasePath(controllerContext);

                // WebId
                String webId = this.webUrlService.getWebId(cmsContext, basePath, this.webPath);
                if (webId != null) {
                    // Path to fetch
                    String pathToFetch = this.webIdService.webIdToFetchInfoService(webId);

                    // Publication infos
                    CMSPublicationInfos publicationInfos = cmsService.getPublicationInfos(cmsContext, pathToFetch);

                    if (publicationInfos.hasManyPublications()) {
                        PortalObjectId pageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
                        Page currentPage = (Page) controllerContext.getController().getPortalObjectContainer().getObject(pageId);
                        if (currentPage != null) {
                            return this.displayManyPublications(currentPage, publicationInfos.getDocumentPath());
                        }
                    }
                }
            }

            // CMS path
            // Must reload (webId may have be moved, so cms path might be false)
            String cmsPath = WebURLFactory.adaptWebURLToCMSPath(controllerContext, this.webPath, this.extendedParameters, true);
            cmsCommand.setCmsPath(cmsPath);


            // Page parameters
            Map<String, String> pageParameters = new HashMap<String, String>();
            cmsCommand.setPageParams(pageParameters);

            // Query parameters
            ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();

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


            this.pageResponse = controllerContext.execute(cmsCommand);
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
        if (pageId != null) {
            String currentNavPath = PagePathUtils.getNavigationPath(controllerCtx, pageId);

            // for resources, there is no currentNavPath. otherwise test that navpath = cmspath
            if (currentNavPath != null) {
                if (!currentNavPath.equals(cmsPath)) {
                    // Le path de navigation a changé, il faut recréer la page technique
                    pageId = null;
                }
            }
        }

        if (pageId == null) {
            // Pour obtenir la page de contextualisation courante
            ControllerResponse ctrlResp = this.getPageResponse(controllerCtx);

            if (ctrlResp instanceof UpdatePageResponse) {
                pageId = ((UpdatePageResponse) ctrlResp).getPageId();
            }
        }

        return pageId;

    }


    /**
     * Get window portal object identifier.
     *
     * @param controllerCtx controller context
     * @return portal object indentifier
     */
    public PortalObjectId getWindowId(ControllerContext controllerCtx) {
        PortalObjectId windowId = null;
        try {
            if (this.windowName != null) {
                PortalObjectId pageId = this.getPageId(controllerCtx);
                windowId = new PortalObjectId(StringUtils.EMPTY, new PortalObjectPath(pageId.getPath().toString().concat("/").concat(this.windowName),
                        PortalObjectPath.CANONICAL_FORMAT));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return windowId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        try {
            PortalObjectId windowId = this.getWindowId(this.context);
            if (windowId != null) {
                String originalPath = "/portal" + windowId;

                // Controller context
                ControllerContext controllerContext = this.getControllerContext();
                // Server invocation
                ServerInvocation invocation = controllerContext.getServerInvocation();
                // Server context
                ServerInvocationContext serverContext = invocation.getServerContext();

                CommandFactory commandFactory = controllerContext.getController().getCommandFactory();
                ControllerCommand originalCommand = commandFactory.doMapping(controllerContext, invocation, serverContext.getPortalHost(),
                        serverContext.getPortalContextPath(), originalPath);

                return this.context.execute(originalCommand);
            }

            // Affichage de la commande CMS
            return this.getPageResponse(this.context);

        } catch (CMSException e) {
            if (e.getErrorCode() == CMSException.ERROR_FORBIDDEN) {
                return new SecurityErrorResponse(e, SecurityErrorResponse.NOT_AUTHORIZED, false);
            }
            if (e.getErrorCode() == CMSException.ERROR_NOTFOUND) {
                return new UnavailableResourceResponse(this.webPath, false);
            }
            throw new ControllerException(e);
        } catch (Exception e) {
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
        String title = this.internationalizationService.getString("SECTIONS_PORTLET_LINK", this.getControllerContext().getServerInvocation().getRequest()
                .getLocale());
        windowProperties.put("osivia.title", title);

        StartDynamicWindowCommand windowCmd = new StartDynamicWindowCommand(pageId, "virtual", portletInstance, "PlayerPublicationsWindow", windowProperties,
                new HashMap<String, String>(), "1", null);

        return this.context.execute(windowCmd);
    }


    /**
     * Getter for webPath.
     *
     * @return the webPath
     */
    public String getWebPath() {
        return this.webPath;
    }

    /**
     * Getter for windowName.
     *
     * @return the windowName
     */
    public String getWindowName() {
        return this.windowName;
    }

    /**
     * Setter for windowName.
     *
     * @param windowName the windowName to set
     */
    public void setWindowName(String windowName) {
        this.windowName = windowName;
    }

    /**
     * Getter for supportingPageMarker.
     *
     * @return the supportingPageMarker
     */
    public boolean isSupportingPageMarker() {
        return this.supportingPageMarker;
    }

    /**
     * Setter for supportingPageMarker.
     *
     * @param supportingPageMarker the supportingPageMarker to set
     */
    public void setSupportingPageMarker(boolean supportingPageMarker) {
        this.supportingPageMarker = supportingPageMarker;
    }

    /**
     * Getter for extendedParameters.
     *
     * @return the extendedParameters
     */
    public ExtendedParameters getExtendedParameters() {
        return this.extendedParameters;
    }

    /**
     * Setter for extendedParameters.
     *
     * @param extendedParameters the extendedParameters to set
     */
    public void setExtendedParameters(ExtendedParameters extendedParameters) {
        this.extendedParameters = extendedParameters;
    }

    /**
     * Getter for pageResponse.
     *
     * @return the pageResponse
     */
    public ControllerResponse getPageResponse() {
        return this.pageResponse;
    }

    /**
     * Setter for pageResponse.
     *
     * @param pageResponse the pageResponse to set
     */
    public void setPageResponse(ControllerResponse pageResponse) {
        this.pageResponse = pageResponse;
    }

}
