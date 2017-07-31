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
package org.osivia.portal.core.internationalization;

import java.util.Locale;

import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.springframework.context.ApplicationContext;

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
    /** Application context. */
    private final ApplicationContext applicationContext;


    /**
     * Constructor.
     *
     * @param internationalizationService internationalization service
     * @param classLoader                 class loader
     */
    public BundleFactory(IInternationalizationService internationalizationService, ClassLoader classLoader) {
        this(internationalizationService, classLoader, null);
    }


    /**
     * Constructor.
     *
     * @param internationalizationService internationalization service
     * @param classLoader                 class loader
     * @param applicationContext          application context
     */
    public BundleFactory(IInternationalizationService internationalizationService, ClassLoader classLoader, ApplicationContext applicationContext) {
        super();
        this.internationalizationService = internationalizationService;
        this.classLoader = classLoader;
        this.applicationContext = applicationContext;
    }


    @Override
    public Bundle getBundle(Locale locale) {
        Bundle bundle;
        if (locale == null) {
            bundle = new Bundle(this.internationalizationService, this.classLoader, this.applicationContext, Locale.getDefault());
        } else {
            bundle = new Bundle(this.internationalizationService, this.classLoader, this.applicationContext, locale);
        }

        return bundle;
    }

}
