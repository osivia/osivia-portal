package org.osivia.portal.api.internationalization;

import java.util.Locale;

/**
 * Internationalization service interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IInternationalizationService {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=InternationalizationService";

    /** Internationalization customizer identifier. */
    String CUSTOMIZER_ID = "osivia.customizer.internationalization.id";
    /** Internationalization customizer resource key attribute. */
    String CUSTOMIZER_ATTRIBUTE_KEY = "osivia.customizer.internationalization.key";
    /** Internationalization customizer locale attribute. */
    String CUSTOMIZER_ATTRIBUTE_LOCALE = "osivia.customizer.internationalization.locale";
    /** Internationalization customizer custom result attribute. */
    String CUSTOMIZER_ATTRIBUTE_RESULT = "osivia.customizer.internationalization.result";


    /**
     * Get bundle factory.
     *
     * @param classLoader class loader, may be null to access default portal resource
     * @return bundle factory
     */
    IBundleFactory getBundleFactory(ClassLoader classLoader);


    /**
     * Access to portal localized resource property, which can be customized.
     *
     * @param key resource property key
     * @param locale locale
     * @param args resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, Object... args);


    /**
     * Access to class loader localized resource property, which can be customized.
     * 
     * @param key resource property key
     * @param locale locale
     * @param classLoader class loader, may be null to access default portal resource
     * @param args resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, ClassLoader classLoader, Object... args);

}
