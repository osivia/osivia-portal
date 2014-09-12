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
package org.osivia.portal.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * URLUtils tests.
 * 
 * @author Cédric Krommenhoek
 * @see URLUtils
 */
public class URLUtilsTest {

    /** Virtual host. */
    private static final String VIRTUAL_HOST = "https://www.osivia.org";
    /** Scheme. */
    private static final String SCHEME = "http";
    /** Server name. */
    private static final String SERVER_NAME = "www.osivia.com";
    /** Server port. */
    private static final int SERVER_PORT = -1;
    /** Ressource path. */
    private static final String RESSOURCE_PATH = "/portal/auth/cms/ressource";


    /** HTTP servlet request mock. */
    private HttpServletRequest requestMock;


    /**
     * Set up.
     */
    @Before
    public void setUp() {
        this.requestMock = EasyMock.createMock("Request", HttpServletRequest.class);
        EasyMock.replay(this.requestMock);
    }

    @Test
    public void testCreateUrl() {
        String url;

        // Nominal case with virtual host header
        this.configureRequest(true);
        url = URLUtils.createUrl(this.requestMock, RESSOURCE_PATH, null);
        assertNotNull(url);
        assertEquals(VIRTUAL_HOST + RESSOURCE_PATH, url);

        // Null path with virtual host header
        url = URLUtils.createUrl(this.requestMock, null, null);
        assertNotNull(url);
        assertEquals(VIRTUAL_HOST, url);
        assertEquals(url, URLUtils.createUrl(this.requestMock));

        // Parameters
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("err", "1");
        parameters.put("httpCode", "403");
        parameters.put("message", "Accès interdit");
        url = URLUtils.createUrl(this.requestMock, RESSOURCE_PATH, parameters);
        assertNotNull(url);
        assertTrue(url.startsWith(VIRTUAL_HOST + RESSOURCE_PATH + "?"));
        assertEquals(2, StringUtils.countMatches(url, "&"));
        assertTrue(url.contains("err=1"));
        assertTrue(url.contains("httpCode=403"));
        assertTrue(url.contains("message=Acc%C3%A8s+interdit"));

        // Nominal case without virtual host header
        this.configureRequest(false);
        url = URLUtils.createUrl(this.requestMock, RESSOURCE_PATH, null);
        assertNotNull(url);
        assertEquals(SCHEME + "://" + SERVER_NAME + RESSOURCE_PATH, url);

        // Null path without virtual host header
        url = URLUtils.createUrl(this.requestMock, null, null);
        assertNotNull(url);
        assertEquals(SCHEME + "://" + SERVER_NAME, url);
        assertEquals(url, URLUtils.createUrl(this.requestMock));

        // Null tests
        url = URLUtils.createUrl(null, null, null);
        assertNull(url);
        url = URLUtils.createUrl(null, "/", null);
        assertNull(url);
    }


    @Test
    public void testAddParameter() {
        String url = "https://www.osivia.org/portal/auth";
        String newUrl;

        // Nominal case
        newUrl = URLUtils.addParameter(url, "refresh", "true");
        assertEquals(url + "?refresh=true", newUrl);

        newUrl = URLUtils.addParameter(newUrl, "action", "move");
        assertEquals(url + "?refresh=true&action=move", newUrl);

        // Null tests
        newUrl = URLUtils.addParameter(null, "test", "1");
        assertNull(newUrl);
        newUrl = URLUtils.addParameter(url, null, "1");
        assertNull(newUrl);
        newUrl = URLUtils.addParameter(url, "test", null);
        assertEquals(url + "?test=", newUrl);
    }


    private void configureRequest(boolean virtualHostHeader) {
        EasyMock.reset(this.requestMock);
        if (virtualHostHeader) {
            EasyMock.expect(this.requestMock.getHeader(URLUtils.VIRTUAL_HOST_REQUEST_HEADER)).andReturn(VIRTUAL_HOST).anyTimes();
        } else {
            EasyMock.expect(this.requestMock.getHeader(URLUtils.VIRTUAL_HOST_REQUEST_HEADER)).andReturn(null).anyTimes();
            EasyMock.expect(this.requestMock.getScheme()).andReturn(SCHEME).anyTimes();
            EasyMock.expect(this.requestMock.getServerName()).andReturn(SERVER_NAME).anyTimes();
            EasyMock.expect(this.requestMock.getServerPort()).andReturn(SERVER_PORT).anyTimes();
        }
        EasyMock.replay(this.requestMock);
    }

}
