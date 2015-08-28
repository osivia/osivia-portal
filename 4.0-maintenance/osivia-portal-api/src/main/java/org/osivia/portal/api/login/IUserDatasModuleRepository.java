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
package org.osivia.portal.api.login;

import javax.portlet.PortletRequest;

/**
 * The Interface IUserDatasModuleRepository.
 */
public interface IUserDatasModuleRepository {
	
	/** MBean JBoss */
	String MBEAN_NAME = "osivia:service=Interceptor,type=Server,name=ServerLogin";

	/**
	 * Register.
	 *
	 * @param moduleMetadatas the module metadatas
	 */
	public void register (UserDatasModuleMetadatas moduleMetadatas);
	
	/**
	 * Unregister.
	 *
	 * @param moduleMetadatas the module metadatas
	 */
	public void unregister (UserDatasModuleMetadatas moduleMetadatas);
	
	
    /**
     * Force reloading of current user datas.
     *
     * @param moduleMetadatas the module metadatas
     */
	
	public void reload(PortletRequest request);
	
}
