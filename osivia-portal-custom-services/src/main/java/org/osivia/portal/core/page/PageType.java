package org.osivia.portal.core.page;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

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
    STATIC_PAGE,
    /** Template page. */
    TEMPLATE_PAGE,
    /** CMS sub page. */
    CMS_SUB_PAGE;


    /**
     * Static access to page type from page and controller context.
     * 
     * @param page type checked page
     * @param controllerContext controller context
     * @return page type
     */
    public static PageType getPageType(Page page, ControllerContext controllerContext) {
        if (page instanceof ITemplatePortalObject) {
            // Template page
            return TEMPLATE_PAGE;
        } else {
            NavigationalStateContext nsContext = (NavigationalStateContext) controllerContext.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            PageNavigationalState pageState = nsContext.getPageNavigationalState(page.getId().toString());

            String sPath[] = null;
            if (pageState != null) {
                QName qName = new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path");
                sPath = pageState.getParameter(qName);
            }

            String basePath = page.getProperty("osivia.cms.basePath");

            if ((sPath != null) && (sPath.length == 1) && (!sPath[0].equals(basePath))) {
                // CMS sub page
                return CMS_SUB_PAGE;
            } else {
                return STATIC_PAGE;
            }
        }
    }

}
