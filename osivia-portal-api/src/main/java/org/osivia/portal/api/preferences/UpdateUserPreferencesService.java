package org.osivia.portal.api.preferences;

import org.osivia.portal.api.directory.v2.IDirService;

import javax.servlet.http.HttpSession;

/**
 * Update user preferences service interface.
 *
 * @author Cédric Krommenhoek
 * @see IDirService
 */
public interface UpdateUserPreferencesService extends IDirService {

    /**
     * Update user preferences.
     *
     * @param httpSession HTTP session
     */
    void update(HttpSession httpSession);

}
