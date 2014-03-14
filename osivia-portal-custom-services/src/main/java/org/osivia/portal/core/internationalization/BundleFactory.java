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
