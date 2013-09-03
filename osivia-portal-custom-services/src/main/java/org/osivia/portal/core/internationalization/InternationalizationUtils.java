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
     * Default constructor.
     * InternationalizationUtils instances should NOT be constructed in standard programming.
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     */
    public InternationalizationUtils() {
        super();
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
