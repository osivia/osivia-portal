/*
 * (C) Copyright 2015 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.core.ecm;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.ecm.EcmCommand;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PageProperties;

/**
 * Delegate of the Ecm Command pattern. Execute the JBoss command, fire notifications and manage redirections
 * @author lbillon
 *
 */
public class EcmCommandDelegate extends ControllerCommand  {

	private static final CommandInfo info = new ActionCommandInfo(false);

	ICMSService cmsService;
	
	protected static ICMSService getCMSService() {

		if (cmsServiceLocator == null) {
			cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class,
					"osivia:service=CmsServiceLocator");
		}

		return cmsServiceLocator.getCMSService();

	}


	private static ICMSServiceLocator cmsServiceLocator;


	/** cms path used by the ECM */
	private String cmsPath;
	
	private EcmCommand command;
	
	/**
	 * @return the command
	 */
	public EcmCommand getCommand() {
		return command;
	}
	/**
	 * @return the cmsPath
	 */
	public String getCmsPath() {
		return cmsPath;
	}

	/**
	 * @param cmsPath
	 *            the cmsPath to set
	 */
	public void setCmsPath(String cmsPath) {
		this.cmsPath = cmsPath;
	}
	

	public EcmCommandDelegate(EcmCommand initialCommand, String cmsPath) {
		this.command = initialCommand;
		this.cmsPath = cmsPath;
	}

	public CommandInfo getInfo() {
		return info;
	}

	/**
	 * {@inheritDoc}
	 */
	public ControllerResponse execute() throws ControllerException {

		try {

			CMSServiceCtx cmsCtx = new CMSServiceCtx();
			cmsCtx.setControllerContext(getControllerContext());
			
			getCMSService().executeEcmCommand(cmsCtx, command, cmsPath);
			
			command.notifyAfterCommand(getControllerContext());
			
			return redirectAfterCommand();

		} catch (CMSException e) {
            throw new ControllerException(e);
        }

	}

	public ControllerResponse redirectAfterCommand()
			throws InvocationException, ControllerException {


		ControllerResponse execute = null;
		
		if(command.getStrategy().equals(EcmCommand.ReloadAfterCommandStrategy.refreshNavigation)) {
		
			// reload navigation tree
			PageProperties.getProperties().setRefreshingPage(true);
			
			// Redirection path
			String redirectCmsPath = EcmCommand.ReloadAfterCommandStrategy.refreshNavigation.getRedirectionPathPath();
	
			CmsCommand redirect = new CmsCommand(null, redirectCmsPath, null, null, null,
					null, null, null, null, null, null);
			execute = context.execute(redirect);
	
		
		} 
		else {
			CmsCommand redirect = new CmsCommand(null, cmsPath, null, null, null,
					null, null, null, null, null, null);
			execute = context.execute(redirect);
		}
		

		return execute;
	}
	
	
}
