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
package org.osivia.portal.core.cms;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.spi.ICMSIntegration;


/**
 * This listener listens to the main portal session events to notify the CMS
 * system of the end of the user session
 */
public class SessionListener implements HttpSessionListener {
	
	public static long activeSessions = 0;
	
	public static String activeSessionSync = new String("activeSessionSync");
	
	
	
	public void sessionCreated(HttpSessionEvent arg0) {
		
		synchronized (activeSessionSync) {
			activeSessions++;			
		}

	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		
		synchronized (activeSessionSync) {
			activeSessions--;
		}
		

		ICMSIntegration nuxeoService;
		try {
			nuxeoService = Locator.findMBean(ICMSIntegration.class, "osivia:service=NuxeoService");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		nuxeoService.sessionDestroyed(arg0);

	}

}
