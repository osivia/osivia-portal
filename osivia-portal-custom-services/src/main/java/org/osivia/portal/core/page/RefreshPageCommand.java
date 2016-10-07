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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.command.view.ViewPageCommand;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.CMSTemplatePage;
import org.osivia.portal.core.portalobjects.DynamicPersistentPage;


public class RefreshPageCommand extends ControllerCommand {

    public static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

    public static INotificationsService notifService = NotificationsUtils.getNotificationsService();
    
    private IPortalUrlFactory urlFactory;
    
	private String pageId;
	private static final CommandInfo info = new ActionCommandInfo(false);
	
    private String ecmActionReturn;
    private String newDocId;
    
    
    public IPortalUrlFactory getUrlFactory()   {

        if (this.urlFactory == null) {
            this.urlFactory = Locator.findMBean(IPortalUrlFactory.class, "osivia:service=UrlFactory");
        }

        return this.urlFactory;
    }


	public CommandInfo getInfo() {
		return info;
	}


	public String getPageId() {
		return pageId;
	}
		

	public String getEcmActionReturn() {
		return ecmActionReturn;
	}


	public void setEcmActionReturn(String ecmActionReturn) {
		this.ecmActionReturn = ecmActionReturn;
	}

	

	public String getNewDocId() {
		return newDocId;
	}


	public void setNewDocId(String newDocId) {
		this.newDocId = newDocId;
	}


	public RefreshPageCommand() {
	}

	public RefreshPageCommand(String pageId) {
		this.pageId = pageId;
		}

	public ControllerResponse execute() throws ControllerException {

		// Récupération page
		PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
		PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);
		
		
		
		// En cas de perte de session, les pages statiques CMS ne sont pas correctement rafraichies
		// (liées à la perte des paramètres de la page)
		// On reprovoque un init-state
		
		if( ( page instanceof DynamicPersistentPage) && (page.getDeclaredProperty("osivia.cms.basePath") != null))    {
            NavigationalStateContext stateContext = (NavigationalStateContext) getControllerContext().getAttributeResolver(
                    ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
            // Current page state
            PageNavigationalState pageState = stateContext.getPageNavigationalState(page.getId().toString());

            String[] sPath = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.path"));
            }

            if (sPath == null) {
                ViewPageCommand pageCmd = new ViewPageCommand(page.getId());
                PortalURL url = new PortalURLImpl(pageCmd, getControllerContext(), null, null);
                String redirectUrl = url.toString() + "?init-state=true";
                return new RedirectionResponse(redirectUrl);
            }
		}

		PageProperties.getProperties().setRefreshingPage(true);
		//getControllerContext().setAttribute(REQUEST_SCOPE, "osivia.refreshPage", "1");
	
		
		// Controller context
        ControllerContext controllerContext = this.getControllerContext();
		PortalControllerContext pcc = new PortalControllerContext(controllerContext);
        
        String ecmActionReturn = this.getEcmActionReturn();
        if(StringUtils.isNotBlank(ecmActionReturn)){
        	
        	String newDocLiveUrl = null;
        	if(this.getNewDocId() != null) {
        		newDocLiveUrl = getUrlFactory().getCMSUrl(pcc, null, getNewDocId(), null, null, InternalConstants.FANCYBOX_LIVE_CALLBACK, null, null, null, null);
        	}
        	
            PortalControllerContext portalCtx = new PortalControllerContext(controllerContext);
            String notification = itlzService.getString(ecmActionReturn, this.getControllerContext().getServerInvocation().getRequest().getLocale(), newDocLiveUrl);
            notifService.addSimpleNotification(portalCtx, notification, NotificationsType.SUCCESS);
            
//            CmsCommand redirect = new CmsCommand(null, null, null, null, null, null, null, null, null, null, null);
//            ControllerResponse execute = context.execute(redirect);
//
//            return execute;
        }
//        else {
        	return new UpdatePageResponse(page.getId());	
//        }
		
		

	}

}
