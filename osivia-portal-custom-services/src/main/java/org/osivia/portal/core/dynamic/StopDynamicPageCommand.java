package org.osivia.portal.core.dynamic;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.WindowState;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.AttributeResolver;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.NoSuchResourceException;
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
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.PageService;
import org.jboss.portal.theme.ThemeConstants;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.charte.UserPage;
import org.osivia.portal.api.charte.UserPortal;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.cms.CMSPage;
import org.osivia.portal.core.page.PortalURLImpl;
import org.osivia.portal.core.page.TabsCustomizerInterceptor;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StopDynamicPageCommand extends DynamicCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(StopDynamicPageCommand.class);

	public CommandInfo getInfo() {
		return info;
	}

	private String pageId;

	public StopDynamicPageCommand() {
	}

	public StopDynamicPageCommand(String pageId) {
		this.pageId = pageId;
	}

	public ControllerResponse execute() throws ControllerException {

		try {

			PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
			Page page = (Page) getControllerContext().getController().getPortalObjectContainer().getObject(poid);

			Page redirectPage = null;

			if (page == null) {
				// The session can have expired, no actions
				redirectPage = (Page) getControllerContext().getController().getPortalObjectContainer().getContext()
						.getDefaultPortal().getDefaultPage();
			} else {

			    String domain = TabsCustomizerInterceptor.getInheritedPageDomain( page);
		    
				PortalObject parent = page.getParent();

				IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
						"osivia:service=DynamicPortalObjectContainer");

				dynamicCOntainer.removeDynamicPage(pageId);

				// Remove other pages from same domain 
				if( domain != null){
				    List<String> domainPageIDs = new ArrayList<String>();
				    
				    Collection<PortalObject> sisters = parent.getChildren(PortalObject.PAGE_MASK);
				    for (PortalObject sister: sisters){
				        String sisterDomain = TabsCustomizerInterceptor.getInheritedPageDomain( (Page) sister);
				        if( domain.equals(sisterDomain))
				            domainPageIDs.add(sister.getId().toString( PortalObjectPath.SAFEST_FORMAT));
				    }
				    
				    for (String domainPageID: domainPageIDs)  {
		                dynamicCOntainer.removeDynamicPage(domainPageID);				        
				    }
				}
				
				

				PortalObjectId currentPageId = (PortalObjectId) getControllerContext().getAttribute(
						ControllerCommand.PRINCIPAL_SCOPE, Constants.ATTR_PAGE_ID);
				
				
				
				/* On regarde si la page appelante est mémorisée */
				
				String closePagePath = page.getProperty("osivia.dynamic.close_page_path");

				
				if( closePagePath != null)	{
					
					ViewPageCommand pageCmd = new ViewPageCommand(PortalObjectId.parse(closePagePath, PortalObjectPath.CANONICAL_FORMAT));
					
					PortalURL url = new PortalURLImpl(pageCmd,getControllerContext(), null, null);
					
					// Impact sur les caches du bandeau
//					ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
//					cacheService.incrementHeaderCount();
					
					// V2.0.22 : pas besoin de rafaichir tous les bandeaux ...
					getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");	

					//TODO : ajouter last pagemarker de la page
					
					return new RedirectionResponse(url.toString());
				}

				
				/* Sinon, on prend le dernier onglet */
				
				
				
				
				if( domain != null) {
	                // une page de domaine a été effacée

                    UserPortal tabbedNavUserPortal = (UserPortal) getControllerContext().getAttribute(
                            ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");

                    if (tabbedNavUserPortal != null) {

                        // On cherche l'item courant
                        int indiceCurrentPage = -1;
                        for (int i = 0; i < tabbedNavUserPortal.getUserPages().size(); i++)
                            if (tabbedNavUserPortal.getUserPages().get(i).getId().equals(poid)) {
                                indiceCurrentPage = i;
                            }

                        if (indiceCurrentPage != -1) {

                           int redirectPageIndice = indiceCurrentPage - 1;

                            redirectPage = (Page) getControllerContext()
                                    .getController()
                                    .getPortalObjectContainer()
                                    .getObject(
                                            (PortalObjectId) tabbedNavUserPortal.getUserPages().get(redirectPageIndice)
                                                    .getId());
                        }
                    }
				    
				    
				} else
				
				
				if( currentPageId.toString(PortalObjectPath.CANONICAL_FORMAT).contains(poid.toString(PortalObjectPath.CANONICAL_FORMAT)))	{
				    

					// La page courante est effacée
					// Redirection vers l'item précédent ou suivant dans le menu

					UserPortal tabbedNavUserPortal = (UserPortal) getControllerContext().getAttribute(
							ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavUserPortal");

					if (tabbedNavUserPortal != null) {

						// On cherche l'item courant
						int indiceCurrentPage = -1;
						for (int i = 0; i < tabbedNavUserPortal.getUserPages().size(); i++)
							if (tabbedNavUserPortal.getUserPages().get(i).getId().equals(poid)) {
								indiceCurrentPage = i;
							}

						if (indiceCurrentPage != -1) {

							// Si c'est le dernier item, on prend le précédent
							// sinon le suivant
							int redirectPageIndice = 0;
							if (indiceCurrentPage == tabbedNavUserPortal.getUserPages().size() - 1)
								redirectPageIndice = indiceCurrentPage - 1;
							else
								redirectPageIndice = indiceCurrentPage + 1;
							



							redirectPage = (Page) getControllerContext()
									.getController()
									.getPortalObjectContainer()
									.getObject(
											(PortalObjectId) tabbedNavUserPortal.getUserPages().get(redirectPageIndice)
													.getId());
						}
					}

				} else {
					// On affiche la page courante
					redirectPage = (Page) getControllerContext().getController().getPortalObjectContainer()
							.getObject(currentPageId);

				}

				// Par défaut Redirection vers le parent, ou -si pas de parent-
				// vers la page par défaut du portail

				if (redirectPage == null) {
					if (parent instanceof Page)
						redirectPage = (Page) parent;
					else if (parent instanceof Portal)
						redirectPage = (Page) ((Portal) parent).getDefaultPage();
				}
			}

			// Maj du breadcrumb
			getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, 	"breadcrumb", null);

			
			// Impact sur les caches du bandeau
//			ICacheService cacheService = Locator.findMBean(ICacheService.class, "osivia:service=Cache");
//			cacheService.incrementHeaderCount();
			
			// V2.0.22 : pas besoin de rafaichir tous les bandeaux ...
			getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1");	

			if( redirectPage instanceof CMSTemplatePage)	{
				redirectPage = (Page) redirectPage.getParent();
			}
			
			ViewPageCommand pageCmd = new ViewPageCommand(redirectPage.getId());
			PortalURL url = new PortalURLImpl(pageCmd,getControllerContext(), null, null);



			String redirectUrl = url.toString()	+ "?init-state=true";
			
			
			return new RedirectionResponse(redirectUrl);


			//return new UpdatePageResponse(redirectPage.getId());

		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

}
