package org.osivia.portal.core.customization;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeConstants;
import org.jboss.portal.theme.ThemeService;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageCustomizerInterceptor;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;

/**
 * Project customization configuration implementation.
 *
 * @author Cédric Krommenhoek
 * @see IProjectCustomizationConfiguration
 */
public class ProjectCustomizationConfiguration implements IProjectCustomizationConfiguration {

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Portal controller context. */
    private final PortalControllerContext portalControllerContext;
    /** Render page command. */
    private final RenderPageCommand renderPageCommand;
    /** Page. */
    private final Page page;
    /** HTTP servlet request. */
    private final HttpServletRequest httpServletRequest;
    /** Administrator indicator. */
    private final boolean administrator;

    /** Before invocation indicator. */
    private boolean beforeInvocation;
    /** Redirection URL. */
    private String redirectionURL;


    /**
     * Constructor.
     *
     * @param portalControllerContext portal controller context
     * @param renderPageCommand render page command
     */
    public ProjectCustomizationConfiguration(PortalControllerContext portalControllerContext, RenderPageCommand renderPageCommand) {
        super();
        this.portalControllerContext = portalControllerContext;
        this.renderPageCommand = renderPageCommand;
        this.page = renderPageCommand.getPage();

        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // HTTP client request
        this.httpServletRequest = controllerContext.getServerInvocation().getServerContext().getClientRequest();

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");

        // Administrator indicator
        this.administrator = PageCustomizerInterceptor.isAdministrator(controllerContext);
    }


    /**
     * {@inheritDoc}
     */
    public String getCMSPath() {
        return this.getPublicationPath();
    }


    /**
     * {@inheritDoc}
     */
    public String[] getDomainAndWebId() {
        String[] result = null;

        // Current CMS base path
        String basePath = this.page.getProperty("osivia.cms.basePath");
        // Current publication path
        String publicationPath = this.getPublicationPath();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setPortalControllerContext(this.portalControllerContext);

        try {
            CMSItem cmsItem = cmsService.getPortalNavigationItem(cmsContext, basePath, publicationPath);
            if (cmsItem != null) {
                String domainId = cmsItem.getDomainId();
                String webId = cmsItem.getWebId();

                result = new String[]{domainId, webId};
            }
        } catch (CMSException e) {
            // Do nothing
        }

        return result;
    }


    /**
     * Get publication path.
     *
     * @return publication path
     */
    private String getPublicationPath() {
        // Controller context
        ControllerContext controllerContext = (ControllerContext) this.portalControllerContext.getControllerCtx();
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        // Current page state
        PageNavigationalState pageState = stateContext.getPageNavigationalState(this.page.getId().toString());

        String[] sPath = null;
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
    public Page getPage() {
        return this.page;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isBeforeInvocation() {
        return this.beforeInvocation;
    }


    /**
     * {@inheritDoc}
     */
    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }


    /**
     * {@inheritDoc}
     */
    public String getThemeName() {
        ThemeService themeService = this.renderPageCommand.getControllerContext().getController().getPageService().getThemeService();
        String themeId = this.page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
        PortalTheme theme = themeService.getThemeById(themeId);
        return theme.getThemeInfo().getName();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAdministrator() {
        return administrator;
    }


    /**
     * {@inheritDoc}
     */
    public void setRedirectionURL(String redirectionURL) {
        this.redirectionURL = redirectionURL;
    }


    /**
     * Setter for beforeInvocation.
     *
     * @param beforeInvocation the beforeInvocation to set
     */
    public void setBeforeInvocation(boolean beforeInvocation) {
        this.beforeInvocation = beforeInvocation;
    }

    /**
     * Getter for redirectionURL.
     *
     * @return the redirectionURL
     */
    public String getRedirectionURL() {
        return this.redirectionURL;
    }

    
    
    /**
     * get URL to replay once redirection is done
     *
     * @param redirectionURL redirection URL
     */
    public String buildRestorableURL() {
        
        String redirectionURL = null;

        redirectionURL = getHttpServletRequest().getRequestURL().toString();
        redirectionURL += "?";
        if (StringUtils.isNotBlank(getHttpServletRequest().getQueryString()))
            redirectionURL +=  getHttpServletRequest().getQueryString();
        redirectionURL +=  "&InterceptedURL=true";

        return redirectionURL;

    }

}
