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
package org.osivia.portal.administration.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;


/**
 * Utility class with null-safe methods for administration.
 *
 * @author Cédric Krommenhoek
 */
public final class AdministrationUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private AdministrationUtils() {
        throw new AssertionError();
    }


    /**
     * Check administrator privileges.
     *
     * @param request request, may be null
     * @return true if administrator
     */
    public static final boolean checkAdminPrivileges(HttpServletRequest request) {
        if (request == null) {
            return false;
        } else {
            Boolean isAdmin = (Boolean) request.getSession().getAttribute(AdministrationConstants.ADMIN_PRIVILEGES_ATTRIBUTE_NAME);
            return BooleanUtils.isTrue(isAdmin);
        }
    }

}
