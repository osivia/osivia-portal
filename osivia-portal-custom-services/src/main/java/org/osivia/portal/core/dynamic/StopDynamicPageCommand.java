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

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import org.osivia.portal.api.theming.UserPage;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.DomainContextualization;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


/* COMPLETEMENT REFACTORE PAR JSS : REPRENDRE INTEGRALEMENT lors de la migration en 3.3 */

public class StopDynamicPageCommand extends DynamicCommand {

    /** Command info. */
    private final CommandInfo info;
    /** Portal URL factory. */
    private final IPortalUrlFactory urlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;

    /** Page identifier. */
    private String pageId;


    /**
     * Constructor.
     */
    public StopDynamicPageCommand() {
        super();
        this.info = new ActionCommandInfo(false);
        this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
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
        return info;
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
                // CMS base path
                String basePath = page.getProperty("osivia.cms.basePath");

                // Domain contextualization
                String domainName = StringUtils.substringBefore(StringUtils.removeStart(basePath, "/"), "/");
                String domainPath = "/" + domainName;
                DomainContextualization domainContextualization = cmsService.getDomainContextualization(cmsContext, domainPath);

                PortalObject parent = page.getParent();

                // Get current page before it is deleted
                Page currentPage = null;

                PortalObjectId currentPageId = (PortalObjectId) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                        Constants.ATTR_PAGE_ID);
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
                    IDynamicObjectContainer dynamicContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");

                    dynamicContainer.removeDynamicPage(this.pageId);

                    if (domainContextualization != null) {
                        // Remove other pages from same domain
                        Set<String> domainPageIds = new HashSet<String>();
                        Collection<PortalObject> siblings = parent.getChildren(PortalObject.PAGE_MASK);
                        for (PortalObject sibling : siblings) {
                            Page siblingPage = (Page) sibling;
                            String siblingBasePath = siblingPage.getProperty("osivia.cms.basePath");
                            String siblingDomainName = StringUtils.substringBefore(StringUtils.removeStart(siblingBasePath, "/"), "/");

                            if ((siblingDomainName != null) && siblingDomainName.equals(domainName)) {
                                String siblingId = sibling.getId().toString(PortalObjectPath.SAFEST_FORMAT);
                                domainPageIds.add(siblingId);
                            }
                        }
                        for (String id : domainPageIds) {
                            dynamicContainer.removeDynamicPage(id);
                        }
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
                } else {
                    if (currentPage != null) {
                        currentPageDeleted = topDeletedPage.getId().equals(topCurrentPage.getId());
                    }
                }


                // Compute URL
                if (!currentPageDeleted) {
                    pageToRefresh = currentPage;
                } else {
                    UserPortal tabbedNavUserPortal = (UserPortal) controllerContext.getAttribute(ControllerCommand.PRINCIPAL_SCOPE,
                            "osivia.tabbedNavUserPortal");

                    // On cherche l'item courant
                    int indiceCurrentPage = -1;
                    List<UserPage> userPages = tabbedNavUserPortal.getUserPages();
                    for (int i = 0; i < userPages.size(); i++) {
                        UserPage userPage = userPages.get(i);
                        if ((domainContextualization != null) && userPage.getId().equals(domainName) || pageObjectId.equals(userPage.getPortalObjectId())) {
                            indiceCurrentPage = i;
                            break;
                        }
                    }


                    if (indiceCurrentPage != -1) {
                        // Si c'est le dernier item, on prend le précédent, sinon le suivant
                        if (indiceCurrentPage == (userPages.size() - 1)) {
                            indiceCurrentPage = indiceCurrentPage - 1;
                        }
                        
                        UserPage userPage = userPages.get(indiceCurrentPage);
                        
                        PortalObjectId portalObjectId = userPage.getPortalObjectId();
                        if (portalObjectId != null) {
                            redirectPage = portalObjectContainer.getObject(portalObjectId, Page.class);
                        }
                    }
                }
            }


            // Refresh tabs
            controllerContext.setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");

            if (pageToRefresh != null) {
                return new UpdatePageResponse(pageToRefresh.getId());
            }

            if (redirectUrl == null) {
                if (redirectPage == null) {
                    redirectPage = ((Portal) page.getParent()).getDefaultPage();
                }

                if (redirectPage != null) {
                    ViewPageCommand pageCmd = new ViewPageCommand(redirectPage.getId());
                    PortalURL url = new PortalURLImpl(pageCmd, controllerContext, null, null);
                    redirectUrl = url.toString() + "?init-state=true";
                }
            }


            return new RedirectionResponse(redirectUrl);
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

}
