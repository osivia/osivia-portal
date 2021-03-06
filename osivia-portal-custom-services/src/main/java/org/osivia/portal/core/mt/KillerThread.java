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
package org.osivia.portal.core.mt;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.http.HttpResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.core.error.ErrorDescriptor;
import org.osivia.portal.core.error.GlobalErrorHandler;
import org.osivia.portal.core.errors.PortalLoggerContext;

/**
 * 
 * Permet d'interrompre un thread pendant son exécution
 * 
 * @author jeanseb
 * 
 */
public class KillerThread implements Runnable {
	
	protected static final Log logger = LogFactory.getLog("PORTAL");	

	Future futureToSurvey;
	ServiceThread threadToSurvey;
	boolean threadEnded = false;
	String windowName;
	String userName;

	String portletName;
    String url;
    String userAgent;
    PortalLoggerContext loggerContext;

	
	public KillerThread(String windowName, Future futureToSurvey, ServiceThread threadToSurvey, String userName, String portletName, String url, String userAgent, PortalLoggerContext loggerContext) {
		super();
		this.windowName = windowName;
		this.futureToSurvey = futureToSurvey;
		this.threadToSurvey = threadToSurvey;
		this.userName = userName;
		this.url = url;
		this.portletName = portletName;
		this.userAgent = userAgent;
		this.loggerContext = loggerContext;

	}

	public void notifyEndThread() {
		//logger.info("Supervisor thread notified from " + threadToSurvey);
		
		threadEnded = true;
	}

	
	public String printStack( ServiceThread thread 		)	{
			String sStack = "";
			StackTraceElement[] stack = thread.getCurrentThread().getStackTrace();
			for (int i=0 ; i < stack.length   ; i++)	{
				sStack += "\n   " + stack[i];
			}		
			return sStack;
};
	
	
	public void run() {

		try {
			
		    String sStack = printStack(threadToSurvey) ;
	          
	            
	     
	         if( !sStack.contains("sun.misc.Unsafe"))  {  
	             
	             Map<String,Object> properties = new HashMap<String, Object>();
	             properties.put("stack", sStack);
	             
	             properties.put("osivia.url", url);
	             properties.put("osivia.header.userAgent", userAgent);
	             properties.put("osivia.portal.portlet", portletName);
                 properties.put("osivia.log.context", loggerContext);
	             
	             ErrorDescriptor errDescriptor = new ErrorDescriptor(HttpServletResponse.SC_REQUEST_TIMEOUT, null, null, this.userName, properties);
	             GlobalErrorHandler.getInstance().logError(errDescriptor);
	             
	         }
			
	
			
			// Stop the thread

			threadToSurvey.setKillerThread(this);

			// Try to cancel by future API
			futureToSurvey.cancel(true);

			// wait for the trhread to end (60 sec.)
			Thread.sleep(60000);

			if (!threadEnded) {
				
				// JSS 20120306 v1.0.8 : ajout de la stacktrace du thread qui boucle
				
			     sStack = printStack(threadToSurvey) ;
			    
		          if( !sStack.contains("sun.misc.Unsafe"))  {  
		              logger.error("!!! NON-ENDING THREAD WINDOW "+ windowName  + sStack);
		          }

				
				// v.1.0.22 : suppression du kill de thread (ne marche pas sans recyclage)
				// (brovoque des temps expirés en production)
				// kill the thread
				//threadToSurvey.getCurrentThread().stop();
				
				
				
				
				// v.1.0.21 : suppression du recyclage de pool (génère un nouveau groupe de threads)
				
				/*

				// Resynchronize the threads pool
				ThreadsPool.cancelInstance();
				
				*/
				
				// v.1.0.23 : on fait un shutdown (à valider)
				
				
				if( "true".equals(System.getProperty("portlets.killLongRunningThreads")))	{
					
					// non statifaisant .. certains threads passent à l'as ;..
				
					threadToSurvey.getCurrentThread().stop();
				
					logger.error("Shutdown the pool " );
				
					ThreadsPool.shutdown();
				}

			}

		} catch (Exception e) {
			
			logger.error( e);

		}

	}

}
