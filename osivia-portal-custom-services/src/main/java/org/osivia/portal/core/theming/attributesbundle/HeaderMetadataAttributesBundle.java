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
 */
package org.osivia.portal.core.theming.attributesbundle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PagePathUtils;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;
import org.osivia.portal.core.security.CmsPermissionHelper.Level;
import org.osivia.portal.core.theming.IPageHeaderResourceService;
import org.osivia.portal.core.web.IWebIdService;

/**
 * Generator of the <head> meta datas informations as title, meta:author, meta:description...
 *
 * @see IAttributesBundle
 */
public final class HeaderMetadataAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static HeaderMetadataAttributesBundle instance;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Page header resource service. */
    private final IPageHeaderResourceService pageHeaderResourceService;
    /** Portal URL factory. */
    private final IPortalUrlFactory portalURLFactory;
    /** WebId service. */
    private final IWebIdService webIdService;
    /** Bundle factory. */
    private final IBundleFactory bundleFactory;

    /** Header metadata attributes names. */
    private final Set<String> names;


    /**
     * Default constructor.
     */
    private HeaderMetadataAttributesBundle() {
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        // Page header resource service
        this.pageHeaderResourceService = Locator.findMBean(IPageHeaderResourceService.class, IPageHeaderResourceService.MBEAN_NAME);
        // Portal URL factory
        this.portalURLFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // WebId service
        this.webIdService = Locator.findMBean(IWebIdService.class, IWebIdService.MBEAN_NAME);
        // Bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

        // Properties
        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_HEADER_TITLE);
        this.names.add(Constants.ATTR_HEADER_METADATA);
        this.names.add(Constants.ATTR_HEADER_PORTAL_BASE_URL);
        this.names.add(Constants.ATTR_HEADER_CANONICAL_URL);
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


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // Current page
        Page page = renderPageCommand.getPage();
        // Current locale
        Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();
        // Bundle
        Bundle bundle = this.bundleFactory.getBundle(locale);


        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setServerInvocation(controllerContext.getServerInvocation());
        cmsCtx.setControllerContext(controllerContext);

        // Get content path
        String contentPath = PagePathUtils.getContentPath(controllerContext, page.getId());

        // Get ECM object
        CMSItem document = null;
        if (contentPath != null) {
            try {
                Boolean pageInEditionMode =  (Boolean) controllerContext.getAttribute(Scope.REQUEST_SCOPE, "osivia.cms.isPageInEditionMode");
                if( pageInEditionMode)  {
                    cmsCtx.setDisplayLiveVersion("1");
                }

                document = this.cmsServiceLocator.getCMSService().getContent(cmsCtx, contentPath);
            } catch (CMSException e) {
                // Do nothing
            }
        }


        // Portal base URL
        String portalBaseURL = this.portalURLFactory.getBasePortalUrl(portalControllerContext)
                + controllerContext.getServerInvocation().getServerContext().getPortalContextPath();
        attributes.put(Constants.ATTR_HEADER_PORTAL_BASE_URL, portalBaseURL);

        // Metadata
        Map<String, String> metadata = new HashMap<String, String>();
        attributes.put(Constants.ATTR_HEADER_METADATA, metadata);

        // Application name
        metadata.put("application-name", bundle.getString("BRAND"));
        // Generator
        StringBuilder generator = new StringBuilder("OSIVIA Portal");
        String version = this.pageHeaderResourceService.getPortalVersion(controllerContext);
        if (version != null) {
            generator.append(" ");
            generator.append(version);
        }
        metadata.put("generator", generator.toString());

        if (document == null) {
            // Title
            attributes.put(Constants.ATTR_HEADER_TITLE, PortalObjectUtils.getDisplayName(page, locale));

            // Canonical URL
            String pagePath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
            attributes.put(Constants.ATTR_HEADER_CANONICAL_URL, portalBaseURL + pagePath);
        } else {
            Map<String, String> documentMetaProperties = document.getMetaProperties();

            // Title
            attributes.put(Constants.ATTR_HEADER_TITLE, documentMetaProperties.get(Constants.HEADER_TITLE));

            // Author
            metadata.put("author", documentMetaProperties.get("osivia.header.meta.author"));
            // Description
            metadata.put("description", documentMetaProperties.get("osivia.header.meta.description"));
            // Keywords
            metadata.put("keywords", documentMetaProperties.get("osivia.header.meta.keywords"));

            // CMS path
            String cmsPath;
            if (PortalObjectUtils.isSpaceSite(page) && StringUtils.isNotEmpty(document.getWebId())) {
                // Web URL
                cmsPath = this.webIdService.itemToPageUrl(cmsCtx, document);
            } else {
                // CMS permalink
                cmsPath = document.getPath();
            }

            // Canonical URL
            String canonicalURL;
            try {
                canonicalURL = this.portalURLFactory.getPermaLink(portalControllerContext, null, null, cmsPath, IPortalUrlFactory.PERM_LINK_TYPE_CMS);
            } catch (PortalException e) {
                canonicalURL = null;
            }
            attributes.put(Constants.ATTR_HEADER_CANONICAL_URL, canonicalURL);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
