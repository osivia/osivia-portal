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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.osivia.portal.api.customization.CustomizationContext;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.core.customization.ICustomizationService;

/**
 * Internationalization service implementation test class.
 *
 * @author Cédric Krommenhoek
 * @see InternationalizationService
 */
public class InternationalizationServiceTest {

    /** Internationalization service. */
    private InternationalizationService internationalizationService;

    /** Bundle factory. */
    private IBundleFactory bundleFactoryMock;
    /** Customization service. */
    private ICustomizationService customizationServiceMock;


    @Before
    public void setUp() throws Exception {
        this.internationalizationService = new InternationalizationService();

        this.bundleFactoryMock = EasyMock.createMock("BundleFactory", IBundleFactory.class);
        EasyMock.replay(this.bundleFactoryMock);

        this.customizationServiceMock = EasyMock.createNiceMock("CustomizationService", ICustomizationService.class);
        EasyMock.replay(this.customizationServiceMock);

        this.internationalizationService.setCustomizationService(this.customizationServiceMock);
    }


    @Test
    public final void testGetBundleFactory() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        IBundleFactory bundleFactory = this.internationalizationService.getBundleFactory(classLoader);
        assertNotNull(bundleFactory);
    }


    @Test
    public final void testGetString() {
        // Test 1 : nominal case with country
        String test1 = this.internationalizationService.getString("EXAMPLE", Locale.GERMANY);
        assertEquals("Beispiel", test1);


        // Test 2 : nominal case with language
        String test2 = this.internationalizationService.getString("EXAMPLE", Locale.GERMAN);
        assertEquals("Beispiel", test2);


        // Test 3 : customizer
        EasyMock.resetToStrict(this.customizationServiceMock);
        final Capture<CustomizationContext> capturedContext = new Capture<CustomizationContext>();
        this.customizationServiceMock.customize(EasyMock.eq(IInternationalizationService.CUSTOMIZER_ID), EasyMock.capture(capturedContext));
        EasyMock.expectLastCall().andAnswer(new CustomizerAnswer(capturedContext));
        EasyMock.replay(this.customizationServiceMock);

        String test3 = this.internationalizationService.getString("EXAMPLE", Locale.GERMAN);
        assertEquals("Valeur customisée", test3);

        EasyMock.resetToNice(this.customizationServiceMock);
        EasyMock.replay(this.customizationServiceMock);


        // Test 4 : missing ressource
        String test4 = this.internationalizationService.getString("test", Locale.GERMAN);
        assertEquals("[Missing resource: test]", test4);


        // Test 5 : missing resource bundle
        String test5 = this.internationalizationService.getString("EXAMPLE", Locale.JAPANESE);
        assertEquals("[Missing resource: EXAMPLE]", test5);
    }


    @Test
    public final void testGetStringClassLoader() {
        // Test 1 : current class loader
        ClassLoader classLoader1 = this.getClass().getClassLoader();
        String test1 = this.internationalizationService.getString("EXAMPLE", Locale.GERMAN, classLoader1);
        assertEquals("Beispiel", test1);

        // Test 2 : null class loader
        ClassLoader classLoader2 = null;
        String test2 = this.internationalizationService.getString("EXAMPLE", Locale.GERMAN, classLoader2);
        assertEquals("Beispiel", test2);
    }


    @Test
    public final void testGetStringArgs() {
        String key = "DEFAULT_VALUE";

        // Text
        String testText;
        // Standard text
        testText = this.internationalizationService.getString(key, Locale.GERMAN, "test");
        assertEquals("Standard: test", testText);
        // Integer
        testText = this.internationalizationService.getString(key, Locale.GERMAN, "12");
        assertEquals("Standard: 12", testText);

        // Date
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.YEAR, 2014);
        Date date = calendar.getTime();
        String testDate;
        // German
        testDate = this.internationalizationService.getString(key, Locale.GERMAN, date);
        assertEquals("Standard: 20.06.2014", testDate);
        // French
        testDate = this.internationalizationService.getString(key, Locale.FRENCH, date);
        assertEquals("Valeur par défaut : 20 juin 2014", testDate);

        // Decimal number
        double decimalNumber = 1234.567;
        String testDecimalNumber;
        // German
        testDecimalNumber = this.internationalizationService.getString(key, Locale.GERMAN, decimalNumber);
        assertEquals("Standard: 1.234,567", testDecimalNumber);
        // French
        testDecimalNumber = this.internationalizationService.getString(key, Locale.FRENCH, decimalNumber);
        assertEquals("Valeur par défaut : 1 234,567", testDecimalNumber);
    }


    /**
     * Inner class for customization context answer.
     *
     * @author Cédric Krommenhoek
     */
    private class CustomizerAnswer implements IAnswer<CustomizationContext> {

        /** Captured customization context. */
        private final Capture<CustomizationContext> captured;

        /**
         * Constructor.
         *
         * @param captured captured customization context
         */
        public CustomizerAnswer(Capture<CustomizationContext> captured) {
            this.captured = captured;
        }

        /**
         * {@inheritDoc}
         */
        public CustomizationContext answer() throws Throwable {
            Map<String, Object> attributes = this.captured.getValue().getAttributes();
            attributes.put(IInternationalizationService.CUSTOMIZER_ATTRIBUTE_RESULT, "Valeur customisée");
            return null;
        }

    }

}
