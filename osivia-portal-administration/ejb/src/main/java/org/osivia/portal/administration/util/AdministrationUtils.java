package org.osivia.portal.administration.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;


/**
 * Utility class with null-safe methods for administration.
 *
 * @author Cédric Krommenhoek
 */
public class AdministrationUtils {

    /**
     * Default constructor.
     * AdministrationUtils instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public AdministrationUtils() {
        super();
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
