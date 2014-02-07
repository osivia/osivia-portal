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
