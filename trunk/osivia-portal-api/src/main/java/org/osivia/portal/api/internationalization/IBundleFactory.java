package org.osivia.portal.api.internationalization;

import java.util.Locale;

/**
 * Bundle factory interface.
 * 
 * @author Cédric Krommenhoek
 * 
 */
public interface IBundleFactory {

    /**
     * Get internationalized bundle.
     *
     * @param locale bundle locale
     * @return internationalized bundle
     */
    Bundle getBundle(Locale locale);

}
