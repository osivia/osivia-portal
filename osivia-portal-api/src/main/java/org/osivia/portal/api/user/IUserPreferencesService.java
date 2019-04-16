/*
 * (C) Copyright 2018 OSIVIA (http://www.osivia.com)
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

package org.osivia.portal.api.user;

import javax.servlet.http.HttpSession;

/**
 * User preferences Mbean service
 * @author Loïc Billon
 *
 */
public interface IUserPreferencesService {

	
    /** MBean name. */
    String MBEAN_NAME = "osivia:service=UserPreferencesService";

	/**
	 * This method is called at the end of a user session to store
	 * the preferences (stored in http session) to the ecm.
	 * 
	 * @param httpSession
	 */
	void updateUserPreferences(HttpSession httpSession);
	
	
}
