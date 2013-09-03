package org.osivia.portal.api.internationalization;

import java.util.Locale;

/**
 * Internationalized bundle java bean.
 *
 * @author Cédric Krommenhoek
 */
public class Bundle {

    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;
    /** Class loader. */
    private final ClassLoader classLoader;
    /** Locale. */
    private final Locale locale;


    /**
     * Constructor.
     *
     * @param internationalizationService internationalization service
     * @param classLoader class loader
     * @param locale locale
     */
    public Bundle(IInternationalizationService internationalizationService, ClassLoader classLoader, Locale locale) {
        super();
        this.internationalizationService = internationalizationService;
        this.classLoader = classLoader;
        this.locale = locale;
    }


    /**
     * Access to a localized bundle property, which can be customized.
     *
     * @param key bundle property key
     * @return bundle property value
     */
    public final String getString(String key) {
        return this.internationalizationService.getString(key, this.locale, this.classLoader);
    }


    /**
     * Access to a localized bundle property, which can be customized.
     *
     * @param key bundle property key
     * @param args property arguments
     * @return bundle property value
     */
    public final String getString(String key, Object... args) {
        return this.internationalizationService.getString(key, this.locale, this.classLoader, args);
    }

}
