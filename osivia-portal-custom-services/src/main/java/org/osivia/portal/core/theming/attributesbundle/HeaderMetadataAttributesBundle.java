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
package org.osivia.portal.core.theming.attributesbundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;

/**
 * Generator of the <head> meta datas informations as title, meta:author, meta:description...
 *
 * @see IAttributesBundle
 */
public class HeaderMetadataAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static HeaderMetadataAttributesBundle instance;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;

    /** Header metadata attributes names. */
    private final Set<String> names;


    /**
     * Default constructor.
     */
    private HeaderMetadataAttributesBundle() {
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");

        // Properties
        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_HEADER_TITLE);
        this.names.add(Constants.ATTR_HEADER_METADATA);
    }


    /**
     * Generate html.
     *
     * @param renderPageCommand command
     * @param pageRendition ..
     * @param attributes attributes
     *
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        Page page = renderPageCommand.getPage();


        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(controllerContext.getServerInvocation());
        cmsCtx.setControllerContext(controllerContext);

        // Get content path
        String contentPath = PagePathUtils.getContentPath(controllerContext, page.getId());

        // Get ECM object
        CMSItem doc = null;

        if (contentPath != null) {
            try {
                if (CmsPermissionHelper.getCurrentPageSecurityLevel(controllerContext, contentPath) == Level.allowPreviewVersion) {
                    cmsCtx.setDisplayLiveVersion("1");
                }

                doc = this.cmsServiceLocator.getCMSService().getContent(cmsCtx, contentPath);

            } catch (CMSException e) {
                // Do nothing
            }
        }

        if (doc != null) {
            Map<String, String> metas = new HashMap<String, String>();
            // if ECM document exists
            for (Map.Entry<String, String> property : doc.getMetaProperties().entrySet()) {
                if (property.getKey().equals(Constants.HEADER_TITLE)) {
                    attributes.put(Constants.ATTR_HEADER_TITLE, property.getValue());
                } else if (property.getKey().startsWith(Constants.HEADER_META)) {
                    metas.put(property.getKey().replace(Constants.HEADER_META.concat("."), ""), property.getValue());
                }
            }
            attributes.put(Constants.ATTR_HEADER_METADATA, metas);

        } else {
            // else get the default display name of the page
            String name = PortalObjectUtils.getDisplayName(page, controllerContext.getServerInvocation().getRequest().getLocales());
            attributes.put(Constants.ATTR_HEADER_TITLE, name);
        }

    }


    /**
     * Public attributes for this bundle.
     *
     * @return Public attributes for this bundle
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static HeaderMetadataAttributesBundle getInstance() {
        if (instance == null) {
            instance = new HeaderMetadataAttributesBundle();
        }
        return instance;
    }

}
