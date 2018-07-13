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

import org.springframework.context.ApplicationContext;

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
     * Get bundle factory.
     *
     * @param classLoader        class loader, may be null to access default portal resource
     * @param applicationContext application context
     * @return bundle factory
     */
    IBundleFactory getBundleFactory(ClassLoader classLoader, ApplicationContext applicationContext);


    /**
     * Access to portal localized resource property, which can be customized.
     *
     * @param key    resource property key
     * @param locale locale
     * @param args   resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, Object... args);


    /**
     * Access to class loader localized resource property, which can be customized.
     *
     * @param key         resource property key
     * @param locale      locale
     * @param classLoader class loader, may be null to access default portal resource
     * @param args        resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, ClassLoader classLoader, Object... args);


    /**
     * Access to customized class loader localized resource property, which can be customized.
     *
     * @param key                   resource property key
     * @param locale                locale
     * @param classLoader           class loader, may be null to access default portal resource
     * @param customizedClassLoader customized class loader, may be null
     * @param args                  resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, ClassLoader classLoader, ClassLoader customizedClassLoader, Object... args);


    /**
     * Access to customized class loader localized resource property or application context message source, which can be customized.
     *
     * @param key                resource property key
     * @param locale             locale
     * @param classLoader        class loader, may be null to access default portal resource
     * @param applicationContext application context
     * @param args               resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, ClassLoader classLoader, ApplicationContext applicationContext, Object... args);


    /**
     * Access to customized class loader localized resource property or application context message source, which can be customized.
     *
     * @param key                   resource property key
     * @param locale                locale
     * @param classLoader           class loader, may be null to access default portal resource
     * @param customizedClassLoader customized class loader, may be null
     * @param applicationContext    application context
     * @param args                  resource property arguments
     * @return localized resource property value
     */
    String getString(String key, Locale locale, ClassLoader classLoader, ClassLoader customizedClassLoader, ApplicationContext applicationContext, Object...
            args);

}
