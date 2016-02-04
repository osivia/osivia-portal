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
package org.osivia.portal.core.dynamic;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.theming.UserPortal;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.TabsCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


/* COMPLETEMENT REFACTORE PAR JSS : REPRENDRE INTEGRALEMENT lors de la migration en 3.3 */

public class StopDynamicPageCommand extends DynamicCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(StopDynamicPageCommand.class);

    IPortalUrlFactory urlFactory;

    public IPortalUrlFactory getUrlFactory()throws Exception {

        if (this.urlFactory == null) {
            this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        }

        return this.urlFactory;
    }


	@Override
    public CommandInfo getInfo() {
		return info;
	}

	private String pageId;

	public StopDynamicPageCommand() {
	}

	public StopDynamicPageCommand(String pageId) {
		this.pageId = pageId;
	}

	@Override
    public ControllerResponse execute() throws ControllerException {

		try {

			PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
			Page page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

			Page redirectPage = null;
			String redirectUrl = null;

			Page pageToRefresh = null;

			if (page == null) {
				// The session can have expired, no actions
				redirectPage = this.getControllerContext().getController().getPortalObjectContainer().getContext()
						.getDefaultPortal().getDefaultPage();
			} else {


                String domainDeleted = TabsCustomizerInterceptor.getInheritedPageDomain( page);


				PortalObject parent = page.getParent();


                /* Get current page before it is deleted */

                Page currentPage = null;

                PortalObjectId currentPageId = (PortalObjectId) this.getControllerContext().getAttribute(
                        ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
                if( currentPageId != null)  {
                    currentPage  = (Page) this.getControllerContext()
                            .getController()
                            .getPortalObjectContainer()
                            .getObject(
                                    currentPageId);
                }
                Page topCurrentPage = currentPage;

                if( currentPage.getParent() instanceof Portal) {
                    topCurrentPage = currentPage;
                } else {
                    topCurrentPage = (Page) currentPage.getParent();
                }

                Page topDeletedPage = null;
                if( page.getParent() instanceof Portal) {
                    topDeletedPage = page;
                } else {
                    topDeletedPage = (Page) page.getParent();
                }


                // Remove public state of dynamic child
                NavigationalStateContext nsContext = (NavigationalStateContext) this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                nsContext.setPageNavigationalState(poid.toString(PortalObjectPath.CANONICAL_FORMAT) + "/" + CMSTemplatePage.PAGE_NAME, null);


                boolean hiddenPage = false;
                if (page instanceof ITemplatePortalObject) {
                    ITemplatePortalObject cmsPage = (ITemplatePortalObject) page;
                    hiddenPage = !cmsPage.isClosable();
                }

                if (hiddenPage) {
                    String pageId = page.getId().toString();
                    this.context.setAttribute(Scope.REQUEST_SCOPE, "osivia.tab.hide", pageId);
                } else {
                    IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");

                    dynamicCOntainer.removeDynamicPage(this.pageId);

                    // Remove other pages from same domain
                    if (domainDeleted != null) {


                        List<String> domainPageIDs = new ArrayList<String>();

                        Collection<PortalObject> sisters = parent.getChildren(PortalObject.PAGE_MASK);
                        for (PortalObject sister : sisters) {
                            String sisterDomain = TabsCustomizerInterceptor.getInheritedPageDomain((Page) sister);
                            if (domainDeleted.equals(sisterDomain)) {
                                domainPageIDs.add(sister.getId().toString(PortalObjectPath.SAFEST_FORMAT));
                            }
                        }

                        for (String domainPageID : domainPageIDs) {
                            dynamicCOntainer.removeDynamicPage(domainPageID);
                        }
                    }
                }


				/* Check if current page deleted */

                boolean currentPageDeleted = false;

				if( domainDeleted != null)  {
				    if( currentPage != null)    {
				        String curDomain = TabsCustomizerInterceptor.getInheritedPageDomain( currentPage);
				        if( domainDeleted.equals(curDomain)) {
                            currentPageDeleted = true;
                        }
				    }
				}   else    {
				    if( currentPage != null)    {
                         currentPageDeleted = topDeletedPage.getId().equals(topCurrentPage.getId() );
				    }
				}



				/* Compute the url */

				if( ! currentPageDeleted){
				    pageToRefresh = currentPage;

				}   else    {

				    UserPortal tabbedNavUserPortal = (UserPortal) this.getControllerContext().getAttribute(
                            ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");

                    // On cherche l'item courant
                    int indiceCurrentPage = -1;
                    for (int i = 0; i < tabbedNavUserPortal.getUserPages().size(); i++) {
                        if(  ( (domainDeleted != null) && (  tabbedNavUserPortal.getUserPages().get(i).getId().equals(domainDeleted)))
                                ||
                                (tabbedNavUserPortal.getUserPages().get(i).getId().equals(poid))
                                )   {
                            indiceCurrentPage = i;
                            break;
                        }
                    }



                    if (indiceCurrentPage != -1) {

                        // Si c'est le dernier item, on prend le précédent
                        // sinon le suivant

                        if (indiceCurrentPage == (tabbedNavUserPortal.getUserPages().size() - 1)) {
                            indiceCurrentPage = indiceCurrentPage - 1;
                        }


                        Object redirectID = tabbedNavUserPortal.getUserPages().get(indiceCurrentPage).getId();
                        if( redirectID instanceof PortalObjectId) {
                            redirectPage = (Page) this.getControllerContext()
                                    .getController()
                                    .getPortalObjectContainer()
                                    .getObject((PortalObjectId) redirectID);


                        }   else    {
                            // Domaine
                            redirectUrl = this.getUrlFactory().getCMSUrl(new PortalControllerContext(this.getControllerContext()), null, "/" + (String) redirectID + "/" + TabsCustomizerInterceptor.getDomainPublishSiteName(), null, null, null, null,
                                    null, null, null);
                        }
                      }
				}
			}


	         // rafaichir la bandeau
            this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");


            if( pageToRefresh != null) {
                return new UpdatePageResponse(pageToRefresh.getId());
            }

            if( redirectUrl == null)    {
                if( redirectPage == null) {
                    redirectPage = ((Portal) page.getParent()).getDefaultPage();
                }

                if( redirectPage != null)   {
                    ViewPageCommand pageCmd = new ViewPageCommand(redirectPage.getId());
                    PortalURL url = new PortalURLImpl(pageCmd,this.getControllerContext(), null, null);
                    redirectUrl = url.toString() + "?init-state=true";
                }


            }


			return new RedirectionResponse(redirectUrl);


			//return new UpdatePageResponse(redirectPage.getId());

		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

}
