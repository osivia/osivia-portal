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
package org.osivia.portal.core.user;

import javax.servlet.http.HttpSession;

import org.osivia.portal.api.user.IUserPreferencesService;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

/**
 * Impl of IUserPreferencesService
 * @author Loïc Billon
 *
 */
public class UserPreferencesService implements IUserPreferencesService {


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;

    
	/* (non-Javadoc)
	 * @see org.osivia.portal.api.user.IUserPreferencesService#updateUserProfile(javax.servlet.http.HttpSession)
	 */
	@Override
	public void updateUserPreferences(HttpSession httpSession) {
		ICMSService cmsService = cmsServiceLocator.getCMSService();
		
		// CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
		
		cmsService.updateUserPreferences(cmsContext, httpSession);
		
	}


    /**
     * Setter for cmsServiceLocator.
     *
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
