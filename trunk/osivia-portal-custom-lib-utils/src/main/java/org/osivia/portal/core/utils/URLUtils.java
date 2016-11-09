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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

/**
 * Utility method with null-safe methods for URL objects.
 * 
 * @author Cédric Krommenhoek
 * @see HttpServletRequest
 * @see URL
 */
public final class URLUtils {

    /** Virtual host request header name. */
    public static final String VIRTUAL_HOST_REQUEST_HEADER = "osivia-virtual-host";


    /**
     * Private constructor : prevent instantiation.
     */
    private URLUtils() {
        throw new AssertionError();
    }


    /**
     * Create URL from HTTP servlet request.
     * 
     * @param request HTTP servlet request
     * @param uri URI, may be null or empty
     * @param parameters parameters map, may be null
     * @return URL
     */
    public static final String createUrl(HttpServletRequest request, String uri, Map<String, String> parameters) {
        if (request == null) {
            return null;
        }

        String path = uri;
        if (path == null) {
            path = StringUtils.EMPTY;
        }

        StringBuffer buffer = new StringBuffer();

        // Scheme, host, port and path
        String virtualHost = request.getHeader(VIRTUAL_HOST_REQUEST_HEADER);
        if (StringUtils.isBlank(virtualHost)) {
            String scheme = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();

            try {
                URL url;
                if( port != 80)
                    url = new URL(scheme, host, port, path);
                else
                    url = new URL(scheme, host,  path);                    
                buffer.append(url);
            } catch (MalformedURLException e1) {
                return null;
            }
        } else {
            buffer.append(virtualHost);
            buffer.append(path);
        }

        // Parameters
        if (parameters != null) {
            boolean firstParameter = true;
            for (Entry<String, String> entry : parameters.entrySet()) {
                try {
                    String name = URLEncoder.encode(entry.getKey(), CharEncoding.UTF_8);
                    String value = URLEncoder.encode(entry.getValue(), CharEncoding.UTF_8);

                    if (firstParameter) {
                        buffer.append("?");
                        firstParameter = false;
                    } else {
                        buffer.append("&");
                    }

                    buffer.append(name);
                    buffer.append("=");
                    buffer.append(value);
                } catch (UnsupportedEncodingException e) {
                    continue;
                }
            }
        }

        return buffer.toString();
    }


    /**
     * Create portal base URL from HTTP servlet request.
     * 
     * @param request HTTP servlet request
     * @return portal base URL
     */
    public static final String createUrl(HttpServletRequest request) {
        return createUrl(request, null, null);
    }


    /**
     * Add parameter to an existing URL.
     * 
     * @param url URL
     * @param name parameter name
     * @param value parameter value, may be null or empty
     * @return modified URL with new parameter
     */
    public static final String addParameter(String url, String name, String value) {
        if ((url == null) || (name == null)) {
            return null;
        }

        String safeName = name;
        String safeValue = value;
        if (safeValue == null) {
            safeValue = StringUtils.EMPTY;
        }

        try {
            safeName = URLEncoder.encode(safeName, CharEncoding.UTF_8);
            safeValue = URLEncoder.encode(safeValue, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        String separator = "?";
        if (url.indexOf("?") >= 0) {
            separator = "&";
        }

        StringBuffer buffer = new StringBuffer(url);
        buffer.append(separator);
        buffer.append(safeName);
        buffer.append("=");
        buffer.append(safeValue);
        return buffer.toString();
    }

}
