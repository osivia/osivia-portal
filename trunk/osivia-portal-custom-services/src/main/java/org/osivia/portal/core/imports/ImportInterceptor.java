/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.osivia.portal.core.imports;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.server.ServerInterceptor;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerRequest;

import java.util.Locale;

/**
 * Susupension des requetes pendant les imports (à positionner avant la gestion
 * des transactions)
 */

public class ImportInterceptor extends ServerInterceptor {
	public static boolean isImportRunning = false;
	
	public static boolean isPageImportTerminated = false;
	public static boolean isPortalImportTerminated = false;
	
	public static int nbPendingRequest = 0;

	protected void invoke(ServerInvocation invocation) throws Exception, InvocationException {

		if (isImportRunning == true) {
			while (isImportRunning == true)
				Thread.sleep(1000L);
		}

		try {
			nbPendingRequest++;

			invocation.invokeNext();
		} finally {
			
			if( nbPendingRequest > 0)
				nbPendingRequest--;
			
		}

		if (isImportRunning && (isPageImportTerminated || isPortalImportTerminated) ) {

			// Wait after the commit for asynchronous updates
			// (to avoid JCA exception from container)
			
			if( isPortalImportTerminated)
				Thread.sleep(10000L);
			else
				Thread.sleep(1000L);				
			
			// Eventual loops will be ignored for the next import
			nbPendingRequest = 0;

			// Import is terminated ant transaction is committed
			// release other threads
			isImportRunning = false;

		}

	}
}
