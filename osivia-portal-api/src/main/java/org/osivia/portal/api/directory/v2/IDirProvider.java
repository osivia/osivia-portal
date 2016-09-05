/*
 * (C) Copyright 2016 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.api.directory.v2;


/**
 * The directory provided handles all calls to the directory service behind dynamic proxies
 * The realization of the call is defered to a delegate which is deployed in a separated war/service
 * @author Loïc Billon
 * @since 4.4
 */
public interface IDirProvider {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=DirectoryProvider";
    
    /**
     * Get an implementation of a service by its class
	 * @param clazz the type of service (IDirService)
	 * @return an instance of a service
     */
    <D extends IDirService> D getDirService(Class<D> clazz);


	/**
	 * Register the delegate (when deploy the directory connector)
	 * @param delegate the portlet delegate
	 */
	void registerDelegate(IDirDelegate delegate);


	/**
	 * Unregister the delegate (when undeploy the directory connector)
	 * @param delegate the portlet delegate
	 */
	void unregisterDelegate(IDirDelegate delegate);


	/**
	 * Clear all directory caches
	 */
	void clearCaches();


    /**
     * Get current transaction manager delegate (for composite transactions management)
     * 
     * @return
     */
    Object getDirectoryTxManagerDelegate();

}
