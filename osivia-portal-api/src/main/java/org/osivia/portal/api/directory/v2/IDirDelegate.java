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

import java.lang.reflect.InvocationTargetException;


/**
 * The directory connector is provided by a service or a portlet which should realize this interface.
 * @author Loïc Billon
 * @since 4.4
 * @see org.osivia.services.directory.DirDelegate in osivia-directory project
 */
public interface IDirDelegate {

	
	/**
	 * Call the directory context and return an implementation of a service
	 * @param requiredType the type of service
	 * @return implementation of the service
	 * @throws InvocationTargetException 
	 */
    public <D extends IDirService> D getDirectoryService(Class<D> requiredType) throws InvocationTargetException;
    
    /**
     * clear all entries in all caches registered in the service.
     */
    public void clearCaches();
    
    
    /**
     * Class loader used by the delegate
     * @return
     */
    public ClassLoader getClassLoader();
}
