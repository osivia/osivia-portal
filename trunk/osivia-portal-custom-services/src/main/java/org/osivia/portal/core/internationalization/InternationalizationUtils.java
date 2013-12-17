package org.osivia.portal.core.internationalization;

import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;

/**
 * Utility class with null-safe methods for internationalization.
 *
 * @author Cédric Krommenhoek
 */
public class InternationalizationUtils {

    /**
     * Private constructor : prevent instantiation.
     */
    private InternationalizationUtils() {
        throw new AssertionError();
    }


    /**
     * Get internationalization service.
     *
     * @return notifications service
     */
    public static final IInternationalizationService getInternationalizationService() {
        return Locator.findMBean(IInternationalizationService.class, IInternationalizationService.MBEAN_NAME);
    }

}
