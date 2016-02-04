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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.theme.PageRendition;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.IAttributesBundle;
import org.osivia.portal.api.theming.UserPage;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.PortalObjectOrderComparator;

/**
 * Transversal attributes bundle.
 *
 * @author Cédric Krommenhoek
 * @see IAttributesBundle
 */
public class SiteMapAttributesBundle implements IAttributesBundle {

    /** Singleton instance. */
    private static SiteMapAttributesBundle instance;


    /** Portal authorization manager factory. */
    private final PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;

    /** Toolbar attributes names. */
    private final Set<String> names;


    /**
     * Private constructor.
     */
    private SiteMapAttributesBundle() {
        super();

        // Portal authorization manager factory
        this.portalAuthorizationManagerFactory = Locator.findMBean(PortalAuthorizationManagerFactory.class, "portal:service=PortalAuthorizationManagerFactory");
        // URL factory
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");

        this.names = new TreeSet<String>();
        this.names.add(Constants.ATTR_SITE_MAP);
    }


    /**
     * Singleton instance access.
     *
     * @return singleton instance
     */
    public static SiteMapAttributesBundle getInstance() {
        if (instance == null) {
            instance = new SiteMapAttributesBundle();
        }
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public void fill(RenderPageCommand renderPageCommand, PageRendition pageRendition, Map<String, Object> attributes) throws ControllerException {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Portal
        Portal portal = renderPageCommand.getPortal();

        // Portal site map
        UserPortal siteMap = (UserPortal) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_SITE_MAP + "." + portal.getName());

        if (siteMap == null) {
            siteMap = this.computeSiteMap(renderPageCommand);

            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_SITE_MAP + "." + portal.getName(), siteMap);
        }

        attributes.put(Constants.ATTR_SITE_MAP, siteMap);
    }


    /**
     * Utility method used to compute portal site map.
     *
     * @param renderPageCommand render page command
     * @return User portal site map
     */
    private UserPortal computeSiteMap(RenderPageCommand renderPageCommand) {
        // Controller context
        ControllerContext controllerContext = renderPageCommand.getControllerContext();
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(controllerContext);
        // Portal authorization manager
        PortalAuthorizationManager pam = this.portalAuthorizationManagerFactory.getManager();
        // Portal
        Portal portal = renderPageCommand.getPortal();
        // Current locale
        Locale locale = controllerContext.getServerInvocation().getRequest().getLocale();

        // CMS service context
        CMSServiceCtx cmsServiceContext = new CMSServiceCtx();
        cmsServiceContext.setControllerContext(controllerContext);
        cmsServiceContext.setScope("anonymous");

        UserPortal siteMap = new UserPortal();
        siteMap.setName(portal.getName());
        List<UserPage> mainPages = siteMap.getUserPages();

        SortedSet<Page> sortedPages = new TreeSet<Page>(PortalObjectOrderComparator.getInstance());
        for (PortalObject po : portal.getChildren(PortalObject.PAGE_MASK)) {
            sortedPages.add((Page) po);
        }

        // Add anonymous portal pages
        for (Page page : sortedPages) {
            PortalObjectPermission perm = new PortalObjectPermission(page.getId(), PortalObjectPermission.VIEW_MASK);
            if (pam.checkPermission(null, perm)) {
                try {
                    if (page.getDeclaredProperty("osivia.cms.basePath") != null) {
                        CMSItem pagePublishSpaceConfig = CmsCommand.getPagePublishSpaceConfig(controllerContext, page);
                        if ((pagePublishSpaceConfig != null) && "1".equals(pagePublishSpaceConfig.getProperties().get("contextualizeInternalContents"))) {
                            this.addSubpagesToSiteMap(cmsServiceContext, portalControllerContext, page, pagePublishSpaceConfig, mainPages);
                        }
                    } else {
                        // Page statique sans espace de publication
                        UserPage userPage = new UserPage(page.getId().toString());
                        mainPages.add(userPage);

                        ViewPageCommand showSubPage = new ViewPageCommand(page.getId());

                        String subName = page.getDisplayName().getString(locale, true);
                        if (subName == null) {
                            subName = page.getName();
                        }
                        userPage.setName(subName);

                        String url = new PortalURLImpl(showSubPage, controllerContext, null, null).toString();
                        userPage.setUrl(url + "?init-state=true");
                    }
                } catch (Exception e) {
                    // May be a security issue, don't block footer
                }
            }
        }

        return siteMap;
    }


    /**
     * Utility method used to add sub pages to portal site map.
     *
     * @param cmsServiceContext CMS service context
     * @param portalControllerContext portal controller context
     * @param page page
     * @param navItem CMS navigation item
     * @param pagesList pages list
     * @throws CMSException
     */
    private void addSubpagesToSiteMap(CMSServiceCtx cmsServiceContext, PortalControllerContext portalControllerContext, Page page, CMSItem navItem, List<UserPage> pagesList)
            throws CMSException {
        // CMS base path
        String basePath = page.getDeclaredProperty("osivia.cms.basePath");

        UserPage userPage = new UserPage(navItem.getPath());
        userPage.setName(navItem.getProperties().get("displayName"));
        Map<String, String> pageParams = new HashMap<String, String>();
        String url = this.urlFactory.getCMSUrl(portalControllerContext, page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), navItem.getPath(),
                pageParams, null, null,
                null, null, null, null);
        userPage.setUrl(url);

        List<UserPage> subPages = userPage.getChildren();

        List<CMSItem> navItems = this.cmsServiceLocator.getCMSService().getPortalNavigationSubitems(cmsServiceContext, basePath, navItem.getPath());
        if (navItems.size() > 0) {
            for (CMSItem navSubItem : navItems) {
                if ("1".equals(navSubItem.getProperties().get("menuItem"))) {
                    this.addSubpagesToSiteMap(cmsServiceContext, portalControllerContext, page, navSubItem, subPages);
                }
            }
        }

        pagesList.add(userPage);
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getAttributeNames() {
        return this.names;
    }

}
