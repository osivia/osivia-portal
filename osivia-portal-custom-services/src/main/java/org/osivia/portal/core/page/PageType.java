package org.osivia.portal.core.page;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.core.dynamic.ITemplatePortalObject;


/**
 * Page types enumeration.
 *
 * @author Cédric Krommenhoek
 */
public enum PageType {

    /** Static page. */
    STATIC_PAGE(false, true, false),
    /** Static CMS page. */
    STATIC_CMS_PAGE(true, true, false),
    /** Static CMS sub page. */
    STATIC_CMS_SUB_PAGE(true, false, false),
    /** Dynamic page. */
    DYNAMIC_PAGE(false, false, true),
    /** CMS templated page. */
    CMS_TEMPLATED_PAGE(true, false, true);


    /** Space indicator. */
    private final boolean space;
    /** Portal page indicator. */
    private final boolean portalPage;
    /** CMS templated page indicator. */
    private final boolean cmsTemplated;


    /**
     * Constructor.
     *
     * @param space is space indicator
     * @param portalPage is portal page indicator
     * @param cmsTemplated is CMS templated page indicator
     */
    private PageType(boolean space, boolean portalPage, boolean cmsTemplated) {
        this.space = space;
        this.portalPage = portalPage;
        this.cmsTemplated = cmsTemplated;
    }


    /**
     * Static access to page type from page and controller context.
     *
     * @param page type checked page
     * @param controllerContext controller context
     * @return page type
     */
    public static PageType getPageType(Page page, ControllerContext controllerContext) {
        NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
        PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());

        // Page CMS base path
        String basePath = page.getProperty("osivia.cms.basePath");
        // Page CMS state path
        String statePath[] = null;
        if (pageState != null) {
            QName qName = new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path");
            statePath = pageState.getParameter(qName);
        }

        PageType type;
        if (page instanceof ITemplatePortalObject) {
            // Templated page
            if (ArrayUtils.isEmpty(statePath)) {
                type = DYNAMIC_PAGE;
            } else {
                type = CMS_TEMPLATED_PAGE;
            }
        } else {
            // Non-templated page
            if (ArrayUtils.isEmpty(statePath)) {
                type = STATIC_PAGE;
            } else if (StringUtils.equals(basePath, statePath[0])) {
                type = STATIC_CMS_PAGE;
            } else {
                type = STATIC_CMS_SUB_PAGE;
            }
        }
        return type;
    }


    /**
     * Getter for space.
     *
     * @return the space
     */
    public boolean isSpace() {
        return this.space;
    }

    /**
     * Getter for portalPage.
     *
     * @return the portalPage
     */
    public boolean isPortalPage() {
        return this.portalPage;
    }

    /**
     * Getter for cmsTemplated.
     *
     * @return the cmsTemplated
     */
    public boolean isCMSTemplated() {
        return this.cmsTemplated;
    }

}
