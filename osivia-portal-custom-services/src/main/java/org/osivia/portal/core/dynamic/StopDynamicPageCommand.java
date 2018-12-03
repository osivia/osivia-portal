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
package org.osivia.portal.core.dynamic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DomainContextualization;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


/**
 * Stop dynamic page command.
 * COMPLETEMENT REFACTORE PAR JSS : REPRENDRE INTEGRALEMENT lors de la migration en 3.3.
 *
 * @see DynamicCommand
 */
public class StopDynamicPageCommand extends DynamicCommand {

    /** Page identifier. */
    private String pageId;
    /** Location. */
    private String location;
    /** Close children indicator. */
    private boolean closeChildren;

    /** Command info. */
    private final CommandInfo info;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Dynamic object container. */
    private final IDynamicObjectContainer dynamicContainer;


    /**
     * Constructor.
     */
    public StopDynamicPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Dynamic object container
        this.dynamicContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");
    }


    /**
     * Constructor.
     *
     * @param pageId page identifier
     */
    public StopDynamicPageCommand(String pageId) {
        this();
        this.pageId = pageId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.info;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Portal object container
        PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);

        try {
            // Page portal object identifier
            PortalObjectId pageObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
            // Page
            Page page = (Page) portalObjectContainer.getObject(pageObjectId);

            Page redirectPage = null;
            String redirectUrl = null;
            Page pageToRefresh = null;

            if (page == null) {
                // The session can have expired, no actions
                redirectPage = portalObjectContainer.getContext().getDefaultPortal().getDefaultPage();
            } else {
                // Portal
                Portal portal = page.getPortal();

                // CMS base path
                String basePath = page.getProperty("osivia.cms.basePath");

                // Domain contextualization
                String domainName = StringUtils.substringBefore(StringUtils.removeStart(basePath, "/"), "/");
                String domainPath = "/" + domainName;
                DomainContextualization domainContextualization = cmsService.getDomainContextualization(cmsContext, domainPath);

                // Get current page before it is deleted
                Page currentPage = null;

                PortalObjectId currentPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.NAVIGATIONAL_STATE_SCOPE, Constants.ATTR_PAGE_ID);
                if (currentPageId != null) {
                    currentPage = (Page) portalObjectContainer.getObject(currentPageId);
                }
                Page topCurrentPage = currentPage;

                if (currentPage.getParent() instanceof Portal) {
                    topCurrentPage = currentPage;
                } else {
                    topCurrentPage = (Page) currentPage.getParent();
                }

                Page topDeletedPage = null;
                if (page.getParent() instanceof Portal) {
                    topDeletedPage = page;
                } else {
                    topDeletedPage = (Page) page.getParent();
                }


                // Remove public state of dynamic child
                NavigationalStateContext nsContext = (NavigationalStateContext) this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                nsContext.setPageNavigationalState(pageObjectId.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + CMSTemplatePage.PAGE_NAME, null);


                boolean hiddenPage = false;
                if (page instanceof ITemplatePortalObject) {
                    ITemplatePortalObject cmsPage = (ITemplatePortalObject) page;
                    hiddenPage = !cmsPage.isClosable();
                }

                if (hiddenPage) {
                    this.context.setAttribute(Scope.SESSION_SCOPE, "osivia.tab.hide", page.toString());
                } else {
                    this.dynamicContainer.removeDynamicPage(this.pageId);

                    // Other removed page identifiers
                    Set<String> otherPageIds = new HashSet<String>();

                    if (domainContextualization != null) {
                        // Remove other pages from same domain
                        Collection<PortalObject> siblings = portal.getChildren(PortalObject.PAGE_MASK);
                        for (PortalObject sibling : siblings) {
                            Page siblingPage = (Page) sibling;
                            String siblingBasePath = siblingPage.getProperty("osivia.cms.basePath");
                            String siblingDomainName = StringUtils.substringBefore(StringUtils.removeStart(siblingBasePath, "/"), "/");

                            if ((siblingDomainName != null) && siblingDomainName.equals(domainName)) {
                                String siblingId = sibling.getId().toString(PortalObjectPath.SAFEST_FORMAT);
                                otherPageIds.add(siblingId);
                            }
                        }
                    }

                    if ((this.closeChildren) && StringUtils.isNotEmpty(basePath)) {
                        Collection<PortalObject> siblings = portal.getChildren(PortalObject.PAGE_MASK);
                        for (PortalObject sibling : siblings) {
                            Page siblingPage = (Page) sibling;
                            String siblingBasePath = siblingPage.getProperty("osivia.cms.basePath");
                            if (StringUtils.startsWith(siblingBasePath, basePath)) {
                                String siblingId = sibling.getId().toString(PortalObjectPath.SAFEST_FORMAT);
                                otherPageIds.add(siblingId);
                            }
                        }
                    }

                    for (String id : otherPageIds) {
                        this.dynamicContainer.removeDynamicPage(id);
                    }
                }


                // Check if current page deleted
                boolean currentPageDeleted = false;

                if (domainContextualization != null) {
                    if (currentPage != null) {
                        String currentBasePath = currentPage.getProperty("osivia.cms.basePath");
                        String currentDomainName = StringUtils.substringBefore(StringUtils.removeStart(currentBasePath, "/"), "/");

                        currentPageDeleted = (currentDomainName != null) && currentDomainName.equals(domainName);
                    }
                } else if ((this.closeChildren) && StringUtils.isNotEmpty(basePath)) {
                    String currentBasePath = currentPage.getProperty("osivia.cms.basePath");
                    currentPageDeleted = StringUtils.startsWith(currentBasePath, basePath);
                } else if (currentPage != null) {
                    currentPageDeleted = topDeletedPage.getId().equals(topCurrentPage.getId());
                }


                // Compute URL
                if (!currentPageDeleted) {
                    pageToRefresh = currentPage;
                } else {
                    // Redirection page path
                    String redirectionPagePath = currentPage.getProperty("osivia.dynamic.close_page_path");
                    if (StringUtils.isNotEmpty(redirectionPagePath)) {
                        // Redirection page object identifier
                        PortalObjectId redirectionPageObjectId = PortalObjectId.parse(redirectionPagePath, PortalObjectPath.CANONICAL_FORMAT);
                        // Redirection page object path
                        PortalObjectPath redirectionPageObjectPath = redirectionPageObjectId.getPath();

                        if (redirectionPageObjectPath.getLastComponentName().equals(CMSTemplatePage.PAGE_NAME)) {
                            // If CMS template page, get parent page
                            redirectionPageObjectId = new PortalObjectId(StringUtils.EMPTY, redirectionPageObjectPath.getParent());
                        }

                        redirectPage = portalObjectContainer.getObject(redirectionPageObjectId, Page.class);
                    }
                }
            }


            // Refresh tabs
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");

            if (pageToRefresh != null) {
                return new UpdatePageResponse(pageToRefresh.getId());
            }


            if (this.location != null) {
                redirectUrl = this.location;
            }


            if (redirectUrl == null) {
                if (redirectPage == null) {
                    redirectPage = page.getPortal().getDefaultPage();
                }

                ViewPageCommand pageCmd = new ViewPageCommand(redirectPage.getId());
                PortalURL url = new PortalURLImpl(pageCmd, controllerContext, null, null);
                redirectUrl = url.toString() + "?init-state=true";
            }


            return new RedirectionResponse(redirectUrl);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }


    /**
     * Setter for location.
     *
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Setter for closeChildren.
     *
     * @param closeChildren the closeChildren to set
     */
    public void setCloseChildren(boolean closeChildren) {
        this.closeChildren = closeChildren;
    }

}
