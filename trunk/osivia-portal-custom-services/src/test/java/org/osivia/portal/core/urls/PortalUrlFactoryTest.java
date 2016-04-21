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
package org.osivia.portal.core.urls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.utils.URLUtils;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * PortalUrlFactory test class.
 *
 * @author Cédric Krommenhoek
 * @see PortalUrlFactory
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PageMarkerUtils.class)
public class PortalUrlFactoryTest {

    /** HTTP scheme. */
    private static final String SCHEME_HTTP = "http";
    /** HTTPS scheme. */
    private static final String SCHEME_HTTPS = "https";
    /** Server name. */
    private static final String SERVER_NAME = "www.osivia.com";
    /** Server port. */
    private static final int SERVER_PORT = 8080;
    /** Portal context. */
    private static final String PORTAL_CONTEXT = "/portal";
    /** Portal auth. */
    private static final String PORTAL_AUTH = "/auth";
    /** Current page marker. */
    private static final String CURRENT_PAGE_MARKER = "42";
    /** Request data. */
    private static final String REQUEST_DATA = "/cms/default-domain/diffusion/PortalSite-1/PortalPage-11.proxy?displayContext=menu&scope=__nocache&addToBreadcrumb=0";


    /** Portal URL factory under test. */
    private PortalUrlFactory portalUrlFactory;

    /** Portal controller context mock. */
    private PortalControllerContext portalControllerContextMock;
    /** Request mock. */
    private HttpServletRequest requestMock;


    @Before
    public void setUp() throws Exception {
        this.portalUrlFactory = new PortalUrlFactory();

        this.portalControllerContextMock = EasyMock.createMock("PortalControllerContext", PortalControllerContext.class);
        ControllerContext controllerContextMock = EasyMock.createMock("ControllerContext", ControllerContext.class);
        ServerInvocation serverInvocationMock = EasyMock.createMock("ServerInvocation", ServerInvocation.class);
        ServerInvocationContext serverContextMock = EasyMock.createMock("ServerContext", ServerInvocationContext.class);
        this.requestMock = EasyMock.createMock("Request", HttpServletRequest.class);

        EasyMock.expect(this.portalControllerContextMock.getControllerCtx()).andStubReturn(controllerContextMock);

        EasyMock.expect(controllerContextMock.getServerInvocation()).andStubReturn(serverInvocationMock);

        EasyMock.expect(serverInvocationMock.getServerContext()).andStubReturn(serverContextMock);

        EasyMock.expect(serverContextMock.getPortalContextPath()).andReturn(PORTAL_CONTEXT).anyTimes();
        EasyMock.expect(serverContextMock.getClientRequest()).andStubReturn(this.requestMock);

        EasyMock.replay(this.portalControllerContextMock);
        EasyMock.replay(controllerContextMock);
        EasyMock.replay(serverInvocationMock);
        EasyMock.replay(serverContextMock);
        EasyMock.replay(this.requestMock);


        // Page marker
        PowerMock.mockStatic(PageMarkerUtils.class);
        EasyMock.expect(PageMarkerUtils.getCurrentPageMarker(EasyMock.anyObject(ControllerContext.class))).andReturn(CURRENT_PAGE_MARKER).anyTimes();

        PowerMock.replayAll();
    }

    @Test
    public final void testAdaptPortalUrlToNavigation() {
        boolean[] predicates = {true, false};

        for (boolean absolute : predicates) {
            for (boolean auth : predicates) {
                for (boolean pageMarker : predicates) {
                    for (boolean port : predicates) {
                        for (boolean https : predicates) {
                            StringBuffer url = new StringBuffer();
                            StringBuffer expected = new StringBuffer();

                            if (absolute) {
                                String scheme;
                                int serverPort;

                                if (https) {
                                    url.append("https://");
                                    scheme = SCHEME_HTTPS;
                                } else {
                                    url.append("http://");
                                    scheme = SCHEME_HTTP;
                                }
                                url.append(SERVER_NAME);

                                if (port && !https) {
                                    url.append(":").append(SERVER_PORT);
                                    serverPort = SERVER_PORT;
                                } else {
                                    serverPort = -1;
                                }

                                this.resetRequestMock(scheme, SERVER_NAME, serverPort);
                            }

                            url.append(PORTAL_CONTEXT);

                            if (auth) {
                                url.append(PORTAL_AUTH);
                            }

                            expected.append(url.toString());

                            if (pageMarker) {
                                url.append("/pagemarker/12");
                            }
                            expected.append("/pagemarker/").append(CURRENT_PAGE_MARKER);

                            url.append(REQUEST_DATA);
                            expected.append(REQUEST_DATA);


                            try {
                                String actual = this.portalUrlFactory.adaptPortalUrlToNavigation(this.portalControllerContextMock, url.toString());

                                assertEquals(expected.toString(), actual);

                                assertTrue(StringUtils.contains(actual, "/pagemarker/" + CURRENT_PAGE_MARKER));

                                assertEquals(port && !https && absolute, StringUtils.contains(actual, String.valueOf(SERVER_PORT)));
                                assertEquals(auth, StringUtils.contains(actual, PORTAL_AUTH));
                                assertEquals(!absolute, StringUtils.startsWith(actual, "/"));
                            } catch (Exception e) {
                                e.printStackTrace();
                                fail(e.getMessage());
                            }
                        }
                    }
                }
            }
        }


        // External URL
        String url = "http://www.example.com/portal/test";
        try {
            String actual = this.portalUrlFactory.adaptPortalUrlToNavigation(this.portalControllerContextMock, url);
            assertEquals(url, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // External HTTPS URL
        String httpsUrl = "https://www.example.com/portal/test";
        try {
            String actual = this.portalUrlFactory.adaptPortalUrlToNavigation(this.portalControllerContextMock, httpsUrl);
            assertEquals(httpsUrl, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    @Test
    public final void testGetHttpErrorUrl() {
        String url;

        // Nominal case
        this.resetRequestMock(SCHEME_HTTP, SERVER_NAME, SERVER_PORT);
        url = this.portalUrlFactory.getHttpErrorUrl(this.portalControllerContextMock, HttpServletResponse.SC_FORBIDDEN);
        assertEquals(SCHEME_HTTP + "://" + SERVER_NAME + ":" + SERVER_PORT + "?httpCode=403", url);

        this.resetRequestMock(SCHEME_HTTPS, SERVER_NAME, 0);
        url = this.portalUrlFactory.getHttpErrorUrl(this.portalControllerContextMock, HttpServletResponse.SC_FORBIDDEN);
        assertEquals(SCHEME_HTTPS + "://" + SERVER_NAME + "?httpCode=403", url);

    }


    private void resetRequestMock(String scheme, String serverName, int serverPort) {
        EasyMock.reset(this.requestMock);
        EasyMock.expect(this.requestMock.getScheme()).andReturn(scheme).anyTimes();
        if (SCHEME_HTTP.equals(scheme)) {
            EasyMock.expect(this.requestMock.getHeader(URLUtils.VIRTUAL_HOST_REQUEST_HEADER)).andReturn(null).anyTimes();
        } else {
            EasyMock.expect(this.requestMock.getHeader(URLUtils.VIRTUAL_HOST_REQUEST_HEADER)).andReturn(scheme + "://" + serverName).anyTimes();
        }
        EasyMock.expect(this.requestMock.getServerName()).andReturn(serverName).anyTimes();
        EasyMock.expect(this.requestMock.getServerPort()).andReturn(serverPort).anyTimes();
        EasyMock.replay(this.requestMock);
    }

}
