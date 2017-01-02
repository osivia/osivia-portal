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
package org.osivia.portal.core.tracker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.SignOutCommand;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.command.PortalCommand;
import org.jboss.portal.core.model.portal.command.PortalObjectCommand;
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.core.error.IPortalLogger;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.tracker.ITracker;



@SuppressWarnings("unchecked")
public class CommandTrackerInterceptor extends ControllerInterceptor{
	
	private ITracker tracker;
	
	
	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	private Log log = LogFactory.getLog(CommandTrackerInterceptor.class);


	public ControllerResponse invoke(ControllerCommand cmd) throws Exception {
			
		getTracker().pushState(cmd);
		
		if( cmd instanceof SignOutCommand)
            IPortalLogger.logger.info(new LoggerMessage("user logout", true));

		
		
		ControllerResponse resp = (ControllerResponse) cmd.invokeNext();

		getTracker().popState();
		
		

	
		return resp;
	}
	
	
	
}
