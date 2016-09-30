/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.api.internationalization;

import java.util.Locale;

/**
 * Internationalized bundle java bean.
 *
 * @author Cédric Krommenhoek
 */
public class Bundle {

    /** Bundle request attribute name. */
    public static final String ATTRIBUTE_NAME = "osivia.portal.internationalization.bundle";

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


    /**
     * Access to a localized bundle property, which can be customized.
     *
     * @param key bundle property key
     * @param customizedClassLoader customized class loader
     * @param args property arguments
     * @return bundle property value
     */
    public final String getString(String key, ClassLoader customizedClassLoader, Object... args) {
        return this.internationalizationService.getString(key, this.locale, this.classLoader, customizedClassLoader, args);
    }


    /**
     * Getter for classLoader.
     *
     * @return the classLoader
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Getter for locale.
     *
     * @return the locale
     */
    public Locale getLocale() {
        return this.locale;
    }

}
