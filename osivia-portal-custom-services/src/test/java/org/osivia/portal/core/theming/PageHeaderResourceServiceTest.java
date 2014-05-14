package org.osivia.portal.core.theming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.easymock.EasyMock;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osivia.portal.core.constants.InternalConstants;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PortalWebApp.class)
public class PageHeaderResourceServiceTest {

    /** Service under test. */
    private PageHeaderResourceService service;

    /** Page header resource cache. */
    private PageHeaderResourceCache cache;

    @Before
    public void setUp() throws Exception {
        this.service = new PageHeaderResourceService();
        this.cache = PageHeaderResourceCache.getInstance();
    }

    @Test
    public void testDeploy() throws IOException {
        // Manifest test resource URL
        URL url = this.getClass().getClassLoader().getResource("manifest.mf");
        // Context path
        String contextPath = "/test-context-path";

        // Servlet context
        ServletContext servletContextMock = EasyMock.createMock("ServletContext", ServletContext.class);
        EasyMock.expect(servletContextMock.getResource("/META-INF/MANIFEST.MF")).andReturn(url).anyTimes();
        EasyMock.replay(servletContextMock);

        // Portal web app
        PortalWebApp portalWebAppMock = PowerMock.createStrictMock(PortalWebApp.class);
        EasyMock.expect(portalWebAppMock.getServletContext()).andStubReturn(servletContextMock);
        EasyMock.expect(portalWebAppMock.getContextPath()).andReturn(contextPath).anyTimes();
        PowerMock.replay(portalWebAppMock);

        // Service call
        this.service.deploy(portalWebAppMock);

        // Test asserts
        assertEquals("3.2.0-SNAPSHOT", this.cache.getVersion(contextPath));
    }


    @Test
    public void testUndeploy() {
        // Context path
        String contextPath = "/test-context-other-path";
        // Element
        String element = "<link href='/test/resource.css' />";

        // Update cache
        this.cache.addVersion(contextPath, "3.2.0-RC1");
        this.cache.addAdaptedElement(element, "<link href='/test/resource-1.2.css' />");

        // Portal web app
        PortalWebApp portalWebAppMock = PowerMock.createStrictMock(PortalWebApp.class);
        EasyMock.expect(portalWebAppMock.getContextPath()).andReturn(contextPath).anyTimes();
        PowerMock.replay(portalWebAppMock);

        // Service call
        this.service.undeploy(portalWebAppMock);

        // Test asserts
        assertNull(this.cache.getVersion(contextPath));
        assertNull(this.cache.getAdaptedElement(element));
    }


    @Test
    public void testAdaptResourceCSSElement() {
        System.setProperty(InternalConstants.SYSTEM_PROPERTY_ADAPT_RESOURCE, "true");

        // Context path
        String contextPath = "/test-adapt-context-path";

        Element elementCSS = new DOMElement("link");
        elementCSS.addAttribute("rel", "stylesheet");
        elementCSS.addAttribute("id", "css_id");
        elementCSS.addAttribute("href", contextPath + "/style/test.css");
        elementCSS.addAttribute("type", "text/css");

        String originalElement = elementCSS.asXML();

        // Service call
        String adaptedElement = this.service.adaptResourceElement(originalElement);

        // Test asserts
        assertEquals(originalElement, adaptedElement);
        assertNotNull(this.cache.getAdaptedElement(originalElement));

        // Update cache
        this.cache.addVersion(contextPath, "3.2.0-RC1");
        this.cache.clearAdaptedElements();

        // Service call
        adaptedElement = this.service.adaptResourceElement(originalElement);

        // Test asserts
        assertFalse(originalElement.equals(adaptedElement));
        assertTrue(adaptedElement.contains(contextPath + "/style/test-3.2.0-RC1.css"));
        assertEquals(adaptedElement, this.cache.getAdaptedElement(originalElement));
    }

    @Test
    public void testAdaptResourceElementWithLineBreak() {
        System.setProperty(InternalConstants.SYSTEM_PROPERTY_ADAPT_RESOURCE, "true");

        String original = "<link type=\"text/css\" rel=\"stylesheet\" href=\"/toutatice-portail-cms-nuxeo/css/common.css\" media=\"screen\"/>\n";

        // Update cache
        this.cache.addVersion("/toutatice-portail-cms-nuxeo", "3.2.0-RC2");

        // Service call
        String adapted = this.service.adaptResourceElement(original);

        // Test asserts
        assertEquals("<link type=\"text/css\" rel=\"stylesheet\" href=\"/toutatice-portail-cms-nuxeo/css/common-3.2.0-RC2.css\" media=\"screen\"/>", adapted);
    }

}
