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
package org.osivia.portal.core.page;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

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
    STATIC_CMS_PAGE(true, true, false),
    /** Static CMS sub item. */
    STATIC_CMS_SUB_ITEM(true, false, false),
    /** Dynamic page. */
    DYNAMIC_PAGE(false, false, true),
    /** CMS templated page. */
    CMS_TEMPLATED_PAGE(true, false, true);


    /** Space indicator. */
    private final boolean space;
    /** Portal page indicator. */
    private final boolean portalPage;
    /** Templated page indicator. */
    private final boolean templated;


    /**
     * Constructor.
     *
     * @param space is space indicator
     * @param portalPage is portal page indicator
     * @param templated is templated page indicator
     */
    private PageType(boolean space, boolean portalPage, boolean templated) {
        this.space = space;
        this.portalPage = portalPage;
        this.templated = templated;
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
            if (StringUtils.equals(InternalConstants.PROP_VALUE_ON, page.getProperty(InternalConstants.PAGE_PROP_NAME_DYNAMIC))) {
                type = DYNAMIC_PAGE;
            } else {
                type = CMS_TEMPLATED_PAGE;
            }
        } else {
            // Non-templated page
            if (StringUtils.isBlank(basePath)) {
                type = STATIC_PAGE;
            } else if ((statePath != null) && StringUtils.equals(basePath, statePath[0])) {
                type = STATIC_CMS_PAGE;
            } else {
                type = STATIC_CMS_SUB_ITEM;
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
     * Getter for templated.
     *
     * @return the templated
     */
    public boolean isTemplated() {
        return this.templated;
    }

}
