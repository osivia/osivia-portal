package org.osivia.portal.core.internationalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
