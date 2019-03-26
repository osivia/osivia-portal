package org.osivia.portal.core.customization;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
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
    /** Controller context. */
    private final ControllerContext controllerContext;
    /** Page. */
    private final Page page;
    /** HTTP servlet response. */
    private final HttpServletResponse httpServletResponse;
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
     * @param page page
     */
    public ProjectCustomizationConfiguration(PortalControllerContext portalControllerContext, Page page) {
        super();
        this.portalControllerContext = portalControllerContext;
        this.page = page;

        // Controller context
        this.controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);
        // HTTP client response
        this.httpServletResponse = controllerContext.getServerInvocation().getServerContext().getClientResponse();

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
    public String getWebId() {
        // WebId
        String webId;

        if (this.page == null) {
            webId = null;
        } else {
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
                if (cmsItem == null) {
                    webId = null;
                } else {
                    webId = cmsItem.getWebId();
                }
            } catch (CMSException e) {
                webId = null;
            }
        }

        return webId;
    }


    /**
     * Get publication path.
     *
     * @return publication path
     */
    private String getPublicationPath() {
        // Publication path
        String publicationPath;

        if (this.page == null) {
            publicationPath = null;
        } else {
            // State context
            NavigationalStateContext stateContext = (NavigationalStateContext) this.controllerContext
                    .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            // Current page state
            PageNavigationalState pageState = stateContext.getPageNavigationalState(this.page.getId().toString());

            String[] sPath = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            if (ArrayUtils.isEmpty(sPath)) {
                publicationPath = null;
            } else {
                publicationPath = sPath[0];
            }
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
        return this.controllerContext.getServerInvocation().getServerContext().getClientRequest();
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletResponse getHttpServletResponse() {
        return this.httpServletResponse;
    }


    /**
     * {@inheritDoc}
     */
    public String getThemeName() {
        // Theme name
        String themeName;

        if (this.page == null) {
            themeName = null;
        } else {
            ThemeService themeService = this.controllerContext.getController().getPageService().getThemeService();
            String themeId = this.page.getProperty(ThemeConstants.PORTAL_PROP_THEME);
            PortalTheme theme = themeService.getThemeById(themeId);
            themeName = theme.getThemeInfo().getName();
        }

        return themeName;
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
     * Get URL to replay once redirection is done.
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
