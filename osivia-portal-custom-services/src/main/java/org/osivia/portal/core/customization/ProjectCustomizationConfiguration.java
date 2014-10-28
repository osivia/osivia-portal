package org.osivia.portal.core.customization;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.customization.IProjectCustomizationConfiguration;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.web.IWebIdService;

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
    /** Current page. */
    private final Page page;

    /** Redirection URL. */
    private String redirectionURL;


    /**
     * Constructor.
     *
     * @param portalControllerContext portal controller context
     * @param page current page
     */
    public ProjectCustomizationConfiguration(PortalControllerContext portalControllerContext, Page page) {
        super();
        this.portalControllerContext = portalControllerContext;
        this.page = page;

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
    }


    /**
     * {@inheritDoc}
     */
    public boolean equalsCMSPath(String cmsPath) {
        // Page CMS path
        String pageCMSPath = this.getPublicationPath();

        return StringUtils.equals(cmsPath, pageCMSPath);
    }


    /**
     * {@inheritDoc}
     */
    public boolean equalsWebId(String webId) {
        return this.equalsWebId(null, webId);
    }


    /**
     * {@inheritDoc}
     */
    public boolean equalsWebId(String domainId, String webId) {
        boolean result = false;

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
                String pageDomainId = cmsItem.getProperties().get(IWebIdService.DOMAIN_ID);
                String pageWebId = cmsItem.getWebId();

                result = (StringUtils.isEmpty(domainId) || StringUtils.equals(domainId, pageDomainId)) && StringUtils.equals(webId, pageWebId);
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
     * Getter for redirectionURL.
     *
     * @return the redirectionURL
     */
    public String getRedirectionURL() {
        return this.redirectionURL;
    }


    /**
     * {@inheritDoc}
     */
    public void setRedirectionURL(String redirectionURL) {
        this.redirectionURL = redirectionURL;
    }

}
