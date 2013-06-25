package org.osivia.portal.api.internationalization;

import java.util.Locale;

/**
 * Internationalization service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IInternationalizationService {

    /** MBean name. */
    static final String MBEAN_NAME = "osivia:service=InternationalizationService";

    /** Internationalization customizer identifier. */
    static final String CUSTOMIZER_ID = "osivia.customizer.internationalization.id";
    /** Internationalization customizer resource key attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_KEY = "osivia.customizer.internationalization.key";
    /** Internationalization customizer locale attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_LOCALE = "osivia.customizer.internationalization.locale";
    /** Internationalization customizer custom result attribute. */
    static final String CUSTOMIZER_ATTRIBUTE_RESULT = "osivia.customizer.internationalization.result";


    /**
     * Access to localized resource property, which can be customized.
     *
     * @param key resource property key
     * @param locale locale
     * @return localized resource property value
     */
    String getString(String key, Locale locale);

}
