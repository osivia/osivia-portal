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
package org.osivia.portal.api.directory;


public interface IDirectoryServiceLocator {
    
    
    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=DirectoryServiceLocator";
    
    public void register(IDirectoryService service);
	
    /**
     * get a directory service
     * @return
     * @deprecated use org.osivia.portal.api.directory.v2.IDirProvider.getDirService(Class<D>)
     */
    @Deprecated
	public IDirectoryService getDirectoryService();

}
