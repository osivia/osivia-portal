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
