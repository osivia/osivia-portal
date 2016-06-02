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
 */
package org.osivia.portal.api.directory;

import org.osivia.portal.api.directory.entity.DirectoryPerson;

/**
 * Integration with a directory service
 * @author lbillon
 *
 */
public interface IDirectoryService {

	/**
	 * get a person
	 * @param username (uid)
	 * @return a person
	 * @deprecated use org.osivia.portal.api.directory.v2.service.PersonService.getPersonWithNuxeoProfile(String)
	 */
    @Deprecated
	DirectoryPerson getPerson(String username);

    /**
     * Get a bean (for advanced portlets usage)
     * @param name name of a bean
     * @param requiredType type of the bean 
     * @return bean
     * @deprecated use org.osivia.portal.api.directory.v2.IDirDelegate.getDirectoryService(Class<D>)
     */
    @Deprecated
	<T extends DirectoryBean> T getDirectoryBean(String name, Class<T> requiredType);
    
    /**
     * clear all entries in all caches registered in the service.
     */
    public void clearCaches();
    
}
