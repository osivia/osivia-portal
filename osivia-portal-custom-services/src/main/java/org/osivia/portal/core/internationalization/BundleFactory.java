package org.osivia.portal.core.internationalization;

import java.util.Locale;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;

/**
 * Bundle factory implementation.
 *
 * @author Cédric Krommenhoek
 * @see IBundleFactory
 */
public class BundleFactory implements IBundleFactory {

    /** Internationalization service. */
    private final IInternationalizationService internationalizationService;
    /** Class loader. */
    private final ClassLoader classLoader;


    /**
     * Constructor.
     *
     * @param internationalizationService internationalization service
     * @param classLoader class loader
     */
    public BundleFactory(IInternationalizationService internationalizationService, ClassLoader classLoader) {
        super();
        this.internationalizationService = internationalizationService;
        this.classLoader = classLoader;
    }


    /**
     * {@inheritDoc}
     */
    public Bundle getBundle(Locale locale) {
        return new Bundle(this.internationalizationService, this.classLoader, locale);
    }

}
