/**
 * 
 */
package org.osivia.portal.core.portalobjects.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.easymock.EasyMock;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.server.ServerRequest;
import org.junit.Before;
import org.junit.Test;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;


/**
 * PortalObjectUtils tests.
 * 
 * @author Cédric Krommenhoek
 * @see PortalObjectUtils
 */
public class PortalObjectUtilsTest {

    private static final String FRENCH_PAGE_NAME = "Nom de la page en français";
    private static final String ENGLISH_PAGE_NAME = "Page name in english";
    private static final String GERMAN_PAGE_NAME = "Seite Name auf Deutsch";
    private static final String JAPANESE_PAGE_NAME = "日本語でページ名";
    private static final String PAGE_NAME = "Page name";
    private static final String SUB_PAGE_NAME = "Sub page name";

    /** Portal object mock. */
    PortalObject portalObjectMock;
    /** Portal object mock. */
    Portal portalMock;
    /** Page object mock. */
    Page pageMock;
    /** Sub page object mock. */
    Page subPageMock;


    /**
     * Set up.
     */
    @Before
    public void setUp() {
        this.portalObjectMock = EasyMock.createMock("PortalObject", PortalObject.class);
        EasyMock.expect(this.portalObjectMock.getParent()).andReturn(null).anyTimes();

        this.portalMock = EasyMock.createMock("Portal", Portal.class);
        EasyMock.expect(this.portalMock.getParent()).andReturn(null).anyTimes();

        this.pageMock = EasyMock.createMock("Page", Page.class);
        EasyMock.expect(this.pageMock.getParent()).andStubReturn(this.portalMock);
        Map<Locale, String> pageDisplayNames = new HashMap<Locale, String>();
        pageDisplayNames.put(Locale.FRENCH, FRENCH_PAGE_NAME);
        pageDisplayNames.put(Locale.ENGLISH, ENGLISH_PAGE_NAME);
        pageDisplayNames.put(Locale.GERMAN, GERMAN_PAGE_NAME);
        pageDisplayNames.put(Locale.JAPANESE, JAPANESE_PAGE_NAME);
        LocalizedString pageLocalizedString = new LocalizedString(pageDisplayNames, Locale.ENGLISH);
        EasyMock.expect(this.pageMock.getDisplayName()).andReturn(pageLocalizedString).anyTimes();
        EasyMock.expect(this.pageMock.getName()).andReturn(PAGE_NAME).anyTimes();

        this.subPageMock = EasyMock.createMock("SubPage", Page.class);
        EasyMock.expect(this.subPageMock.getParent()).andStubReturn(this.pageMock);
        Map<Locale, String> subPageDisplayNames = new HashMap<Locale, String>();
        subPageDisplayNames.put(Locale.GERMANY, GERMAN_PAGE_NAME);
        LocalizedString subPagelocalizedString = new LocalizedString(subPageDisplayNames, Locale.ENGLISH);
        EasyMock.expect(this.subPageMock.getDisplayName()).andReturn(subPagelocalizedString).anyTimes();
        EasyMock.expect(this.subPageMock.getName()).andReturn(SUB_PAGE_NAME).anyTimes();
    }


    /**
     * Test method for {@link PortalObjectUtils#isAncestor(PortalObject, PortalObject)}.
     */
    @Test
    public final void testIsAncestor() {
        EasyMock.replay(this.portalObjectMock);
        EasyMock.replay(this.portalMock);
        EasyMock.replay(this.pageMock);
        EasyMock.replay(this.subPageMock);


        // Test 1 : both arguments are null
        boolean result1 = PortalObjectUtils.isAncestor(null, null);
        assertFalse(result1);

        // Test 2 : first argument is null
        boolean result2 = PortalObjectUtils.isAncestor(null, this.portalObjectMock);
        assertFalse(result2);

        // Test 3 : second argument is null
        boolean result3 = PortalObjectUtils.isAncestor(this.portalObjectMock, null);
        assertFalse(result3);

        // Test 4 : same object
        boolean result4 = PortalObjectUtils.isAncestor(this.portalObjectMock, this.portalObjectMock);
        assertFalse(result4);

        // Test 5 : direct parent
        boolean result5 = PortalObjectUtils.isAncestor(this.portalMock, this.pageMock);
        assertTrue(result5);

        // Test 6 : undirect parent
        boolean result6 = PortalObjectUtils.isAncestor(this.portalMock, this.subPageMock);
        assertTrue(result6);

        // Test 7 : arguments inversion
        boolean result7 = PortalObjectUtils.isAncestor(this.subPageMock, this.portalMock);
        assertFalse(result7);

        // Test 8 : non descendant object
        boolean result8 = PortalObjectUtils.isAncestor(this.portalObjectMock, this.subPageMock);
        assertFalse(result8);


        EasyMock.verify(this.portalObjectMock);
        EasyMock.verify(this.portalMock);
        EasyMock.verify(this.pageMock);
        EasyMock.verify(this.subPageMock);
    }


    /**
     * Test method for {@link PortalObjectUtils#getDisplayName(PortalObject, ServerRequest)}.
     */
    @Test
    public final void testGetDisplayName() {
        Locale[] localesArray;
        Enumeration<Locale> localesEnum;
        List<Locale> localesList;

        EasyMock.replay(this.pageMock);
        EasyMock.replay(this.subPageMock);


        // Test 1 : both arguments are null
        localesArray = null;
        String result1 = PortalObjectUtils.getDisplayName(null, localesArray);
        assertNull(result1);

        // Test 2 : first argument is null
        localesArray = new Locale[]{Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN};
        String result2 = PortalObjectUtils.getDisplayName(null, localesArray);
        assertNull(result2);

        // Test 3 : second argument is null (array)
        localesArray = null;
        String result3 = PortalObjectUtils.getDisplayName(this.pageMock, localesArray);
        assertEquals(PAGE_NAME, result3);

        // Test 4 : second argument is null (enumeration)
        localesEnum = null;
        String result4 = PortalObjectUtils.getDisplayName(this.pageMock, localesEnum);
        assertEquals(PAGE_NAME, result4);

        // Test 5
        localesArray = new Locale[]{Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN};
        String result5 = PortalObjectUtils.getDisplayName(this.pageMock, localesArray);
        assertEquals(FRENCH_PAGE_NAME, result5);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result5bis = PortalObjectUtils.getDisplayName(this.pageMock, localesEnum);
        assertEquals(FRENCH_PAGE_NAME, result5bis);

        // Test 6
        localesArray = new Locale[]{Locale.GERMAN, Locale.FRENCH};
        String result6 = PortalObjectUtils.getDisplayName(this.pageMock, localesArray);
        assertEquals(GERMAN_PAGE_NAME, result6);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result6bis = PortalObjectUtils.getDisplayName(this.pageMock, localesEnum);
        assertEquals(GERMAN_PAGE_NAME, result6bis);

        // Test 7
        localesArray = new Locale[]{};
        String result7 = PortalObjectUtils.getDisplayName(this.pageMock, localesArray);
        assertEquals(PAGE_NAME, result7);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result7bis = PortalObjectUtils.getDisplayName(this.pageMock, localesEnum);
        assertEquals(PAGE_NAME, result7bis);

        // Test 8
        localesArray = new Locale[]{Locale.FRANCE, Locale.GERMANY};
        String result8 = PortalObjectUtils.getDisplayName(this.pageMock, localesArray);
        assertEquals(FRENCH_PAGE_NAME, result8);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result8bis = PortalObjectUtils.getDisplayName(this.pageMock, localesEnum);
        assertEquals(FRENCH_PAGE_NAME, result8bis);

        // Test 9
        localesArray = new Locale[]{Locale.FRENCH, Locale.ENGLISH, Locale.GERMAN};
        String result9 = PortalObjectUtils.getDisplayName(this.subPageMock, localesArray);
        assertEquals(GERMAN_PAGE_NAME, result9);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result9bis = PortalObjectUtils.getDisplayName(this.subPageMock, localesEnum);
        assertEquals(GERMAN_PAGE_NAME, result9bis);

        // Test 10
        localesArray = new Locale[]{Locale.GERMAN, Locale.FRENCH};
        String result10 = PortalObjectUtils.getDisplayName(this.subPageMock, localesArray);
        assertEquals(GERMAN_PAGE_NAME, result10);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result10bis = PortalObjectUtils.getDisplayName(this.subPageMock, localesEnum);
        assertEquals(GERMAN_PAGE_NAME, result10bis);

        // Test 11
        localesArray = new Locale[]{};
        String result11 = PortalObjectUtils.getDisplayName(this.subPageMock, localesArray);
        assertEquals(SUB_PAGE_NAME, result11);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result11bis = PortalObjectUtils.getDisplayName(this.subPageMock, localesEnum);
        assertEquals(SUB_PAGE_NAME, result11bis);

        // Test 12
        localesArray = new Locale[]{Locale.FRANCE, Locale.GERMANY};
        String result12 = PortalObjectUtils.getDisplayName(this.subPageMock, localesArray);
        assertEquals(GERMAN_PAGE_NAME, result12);
        localesList = Arrays.asList(localesArray);
        localesEnum = Collections.enumeration(localesList);
        String result12bis = PortalObjectUtils.getDisplayName(this.subPageMock, localesEnum);
        assertEquals(GERMAN_PAGE_NAME, result12bis);


        EasyMock.verify(this.pageMock);
        EasyMock.verify(this.subPageMock);
    }

}
