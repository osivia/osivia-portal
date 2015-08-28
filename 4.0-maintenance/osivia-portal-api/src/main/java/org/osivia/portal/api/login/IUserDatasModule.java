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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osivia.portal.api.directory.entity.DirectoryPerson;


/**
 * The Interface IUserDatasModule.
 * 
 * customize user datas map
 * 
 * @author Jean-Sébastien Steux
 */
public interface IUserDatasModule {

	
    /** Post login processor customizer identifier. */
    String CUSTOMIZER_ID = "osivia.customizer.postlogin.id";
	
    /**
     * Compute user datas.
     * 
     * @param request the request
     * @param datas the datas
     * @deprecated see computeLoggedUser
     */
    @Deprecated
	public void computeUserDatas(HttpServletRequest request, Map<String, Object> datas);

    /**
     * Compute user datas into a DirectoryPerson object
     * 
     * @param request the request
     * @return the connected user
     */
    public DirectoryPerson computeLoggedUser(HttpServletRequest request);
}
