package org.osivia.portal.core.urls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;

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

    /** Server name. */
    private static final String SERVER_NAME = "www.osivia.com";
    /** Server port. */
    private static final int SERVER_PORT = 8080;
    /** Server default port. */
    private static final int SERVER_DEFAULT_PORT = 80;
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
                        StringBuffer url = new StringBuffer();
                        StringBuffer expected = new StringBuffer();

                        if (absolute) {
                            url.append("http://");
                            url.append(SERVER_NAME);

                            if (port) {
                                this.resetRequestMock(SERVER_NAME, SERVER_PORT);
                                url.append(":").append(SERVER_PORT);
                            } else {
                                this.resetRequestMock(SERVER_NAME, SERVER_DEFAULT_PORT);
                            }
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

                            assertEquals(port && absolute, StringUtils.contains(actual, String.valueOf(SERVER_PORT)));
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


        // External URL
        String url = "http://www.example.com/portal/test";
        try {
            String actual = this.portalUrlFactory.adaptPortalUrlToNavigation(this.portalControllerContextMock, url);
            assertEquals(url, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    private void resetRequestMock(String serverName, int serverPort) {
        EasyMock.reset(this.requestMock);
        EasyMock.expect(this.requestMock.getServerName()).andReturn(serverName).anyTimes();
        EasyMock.expect(this.requestMock.getServerPort()).andReturn(serverPort).anyTimes();
        EasyMock.replay(this.requestMock);
    }

}
