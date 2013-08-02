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
import org.osivia.portal.core.constants.InternalConstants;
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
    STATIC_CMS_PAGE(false, true, false),
    /** Static CMS sub page. */
    STATIC_CMS_SUB_PAGE(false, false, false),
    /** Dynamic page (without CMS). */
    DYNAMIC_PAGE(true, false, false),
    /** Dynamic CMS page. */
    DYNAMIC_CMS_PAGE(true, false, true),
    /** Dynamic CMS page without portal page anchor. */
    DYNAMIC_CMS_PAGE_WITHOUT_PORTAL_PAGE(true, false, false);


    /** Page templated indicator. */
    private final boolean templated;
    /** Page editable indicator. */
    private final boolean editable;
    /** Portal page available indicator. */
    private final boolean portalPageAvailable;


    /**
     * Constructor using fields.
     *
     * @param templated page templated indicator
     * @param editable page editable indicator
     * @param portalPageAvailable portal page available indicator
     */
    private PageType(boolean templated, boolean editable, boolean portalPageAvailable) {
        this.templated = templated;
        this.editable = editable;
        this.portalPageAvailable = portalPageAvailable;
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
            } else if (StringUtils.equals(InternalConstants.PROP_VALUE_ON, page.getProperty(InternalConstants.PAGE_PROP_NAME_DYNAMIC))) {
                type = DYNAMIC_CMS_PAGE_WITHOUT_PORTAL_PAGE;
            } else {
                type = DYNAMIC_CMS_PAGE;
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
     * Getter for templated.
     *
     * @return the templated
     */
    public boolean isTemplated() {
        return this.templated;
    }

    /**
     * Getter for editable.
     *
     * @return the editable
     */
    public boolean isEditable() {
        return this.editable;
    }

    /**
     * Getter for portalPageAvailable.
     * 
     * @return the portalPageAvailable
     */
    public boolean isPortalPageAvailable() {
        return this.portalPageAvailable;
    }

}
