package org.osivia.portal.core.page;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
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
    STATIC_PAGE(false, true),
    /** Template page. */
    TEMPLATE_PAGE(true, false),
    /** Static CMS sub page. */
    STATIC_CMS_SUB_PAGE(false, false),
    /** Non-default portal home template CMS root page. */
    NON_DEFAULT_TEMPLATE_CMS_ROOT_PAGE(true, true);

    
    private boolean templated;
    
    private boolean editable;
    
    
    private PageType(boolean templated, boolean editable) {
        this.templated = templated;
        this.editable = editable;
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

        String sPath[] = null;
        if (pageState != null) {
            QName qName = new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path");
            sPath = pageState.getParameter(qName);
        }

        String basePath = page.getProperty("osivia.cms.basePath");

        if (page instanceof ITemplatePortalObject) {
            if ((sPath != null) && (sPath.length == 1) && (sPath[0].equals(basePath))
                    && !page.getName().equals(page.getPortal().getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME))) {
                // Non-default portal home template CMS root page
                return NON_DEFAULT_TEMPLATE_CMS_ROOT_PAGE;
            } else {
                // Template page
                return TEMPLATE_PAGE;
            }
        } else {
            if ((sPath != null) && (sPath.length == 1) && (!sPath[0].equals(basePath))) {
                // Static CMS sub page
                return STATIC_CMS_SUB_PAGE;
            } else {
                // Static page
                return STATIC_PAGE;
            }
        }
    }
    
    public final boolean isTemplated() {
        return templated;
    }
    
    public final boolean isEditable() {
        return editable;
    }
    
}
