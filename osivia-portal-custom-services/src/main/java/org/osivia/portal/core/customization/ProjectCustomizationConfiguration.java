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
    /** Controller context. */
    private final ControllerContext controllerContext;
    /** Current page. */
    private final Page page;

    /** Redirection URL. */
    private String redirectionURL;


    /**
     * Constructor.
     *
     * @param controllerContext controller context
     * @param page current page
     */
    public ProjectCustomizationConfiguration(ControllerContext controllerContext, Page page) {
        super();
        this.controllerContext = controllerContext;
        this.page = page;

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
    }


    /**
     * {@inheritDoc}
     */
    public void createCMSRedirection(String cmsPath, String redirectionURL) {
        // Page CMS path
        String pageCMSPath = this.getPublicationPath();

        if (StringUtils.equals(cmsPath, pageCMSPath)) {
            this.redirectionURL = redirectionURL;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createWebRedirection(String webId, String redirectionURL) {
        this.createWebRedirection(null, webId, redirectionURL);
    }


    /**
     * {@inheritDoc}
     */
    public void createWebRedirection(String domainId, String webId, String redirectionURL) {
        // Current CMS base path
        String basePath = this.page.getProperty("osivia.cms.basePath");
        // Current publication path
        String publicationPath = this.getPublicationPath();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(this.controllerContext);

        try {
            CMSItem cmsItem = cmsService.getPortalNavigationItem(cmsContext, basePath, publicationPath);
            String pageDomainId = cmsItem.getProperties().get(IWebIdService.DOMAIN_ID);
            String pageWebId = cmsItem.getWebId();

            if ((StringUtils.isEmpty(domainId) || StringUtils.equals(domainId, pageDomainId)) && StringUtils.equals(webId, pageWebId)) {
                this.redirectionURL = redirectionURL;
            }
        } catch (CMSException e) {
            // Do nothing
        }
    }


    /**
     * Get publication path.
     *
     * @return publication path
     */
    private String getPublicationPath() {
        // State context
        NavigationalStateContext stateContext = (NavigationalStateContext) this.controllerContext
                .getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
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

}
