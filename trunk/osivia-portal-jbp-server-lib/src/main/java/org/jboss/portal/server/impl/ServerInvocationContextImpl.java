/******************************************************************************
 * JBoss, a division of Red Hat *
 * Copyright 2006, Red Hat Middleware, LLC, and individual *
 * contributors as indicated by the @authors tag. See the *
 * copyright.txt in the distribution for a full listing of *
 * individual contributors. *
 * *
 * This is free software; you can redistribute it and/or modify it *
 * under the terms of the GNU Lesser General Public License as *
 * published by the Free Software Foundation; either version 2.1 of *
 * the License, or (at your option) any later version. *
 * *
 * This software is distributed in the hope that it will be useful, *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU *
 * Lesser General Public License for more details. *
 * *
 * You should have received a copy of the GNU Lesser General Public *
 * License along with this software; if not, write to the Free *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. *
 ******************************************************************************/
package org.jboss.portal.server.impl;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.portal.common.invocation.AbstractInvocationContext;
import org.jboss.portal.common.net.media.MediaType;
import org.jboss.portal.common.text.CharBuffer;
import org.jboss.portal.common.text.FastURLEncoder;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.server.PortalConstants;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerInvocationContext;
import org.jboss.portal.server.ServerURL;
import org.jboss.portal.server.impl.invocation.SessionAttributeResolver;
import org.jboss.portal.server.request.URLContext;
import org.jboss.portal.server.request.URLFormat;
import org.jboss.portal.web.Body;
import org.jboss.portal.web.WebRequest;
import org.osivia.portal.core.utils.URLUtils;

/**
 * Server invocation context implementation.
 * 
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author Cédric Krommenhoek
 * @see AbstractInvocationContext
 * @see ServerInvocationContext
 */
public class ServerInvocationContextImpl extends AbstractInvocationContext implements ServerInvocationContext {

    /** The fast url encoder. */
    private static final FastURLEncoder urlEncoder = FastURLEncoder.getUTF8Instance();

    /** The client request. */
    private final HttpServletRequest req;

    /** The client request. */
    private final WebRequest webReq;

    /** The client response. */
    private final HttpServletResponse resp;

    /** The portal context path. */
    private String portalContextPath;

    /** The portal request path. */
    private String portalRequestPath;

    /** The portal host. */
    private String portalHost;

    /** The url context. */
    private URLContext urlContext;

    /** Request relative prefix. */
    private final String requestRelativePrefix;

    /** Request prefix. */
    private final String requestPrefix;


    /**
     * Constructor.
     * 
     * @param req HTTP servlet request
     * @param resp HTTP servlet response
     * @param webReq Web request
     * @param portalHost portal host
     * @param portalRequestPath portal request path
     * @param portalContextPath portal context path
     * @param urlContext URL context
     */
    public ServerInvocationContextImpl(HttpServletRequest req, HttpServletResponse resp, WebRequest webReq, String portalHost, String portalRequestPath,
            String portalContextPath, URLContext urlContext) {
        if (req == null) {
            throw new IllegalArgumentException();
        }
        if (resp == null) {
            throw new IllegalArgumentException();
        }

        //
        this.req = req;
        this.webReq = webReq;
        this.resp = resp;
        this.portalRequestPath = portalRequestPath;
        this.portalContextPath = portalContextPath;
        this.portalHost = portalHost;
        this.urlContext = urlContext;

        // Request relative prefix
        String url = URLUtils.createUrl(req, req.getContextPath(), null);
        this.requestRelativePrefix = url;

        // Request prefix
        this.requestPrefix = req.getContextPath();


        // HOTFIX 2.0.8 : resolver REQUEST_SCOPE en synchronise
        this.addResolver(ServerInvocation.REQUEST_SCOPE, new SyncRequestAttributeResolver(req));
        // addResolver(ServerInvocation.REQUEST_SCOPE, new RequestAttributeResolver(req));
        this.addResolver(ServerInvocation.SESSION_SCOPE, new SessionAttributeResolver(req, PortalConstants.PORTAL_SESSION_MAP_KEY, false));
        this.addResolver(ServerInvocation.PRINCIPAL_SCOPE, new SessionAttributeResolver(req, PortalConstants.PORTAL_PRINCIPAL_MAP_KEY, true));
    }


    /**
     * Getter for webReq.
     * 
     * @return webReq value
     */
    public WebRequest getWebRequest() {
        return this.webReq;
    }


    /**
     * {@inheritDoc}
     */
    public HttpServletRequest getClientRequest() {
        return this.req;
    }

    /**
     * {@inheritDoc}
     */
    public HttpServletResponse getClientResponse() {
        return this.resp;
    }

    /**
     * {@inheritDoc}
     */
    public String getMediaType() {
        MediaType mediaType = this.webReq.getMediaType();
        if (mediaType == null) {
            return null;
        }
        return mediaType.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public URLContext getURLContext() {
        return this.urlContext;
    }

    /**
     * {@inheritDoc}
     */
    public ParameterMap getQueryParameterMap() {
        return ParameterMap.wrap(this.webReq.getQueryParameterMap());
    }

    /**
     * {@inheritDoc}
     */
    public ParameterMap getBodyParameterMap() {
        Body body = this.webReq.getBody();

        //
        if (body instanceof Body.Form) {
            return ParameterMap.wrap(((Body.Form) body).getParameters());
        }

        //
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getPortalRequestPath() {
        return this.portalRequestPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getPortalContextPath() {
        return this.portalContextPath;
    }

    /**
     * {@inheritDoc}
     */
    public String getPortalHost() {
        return this.portalHost;
    }

    /**
     * {@inheritDoc}
     */
    public String renderURL(ServerURL url, URLContext context, URLFormat format) {
        // Modif PIA
        /*
         * int index = context.getMask() << 2 | format.getMask();
         * if (buffers[index] == null)
         * {
         * buffers[index] = new Buffer(resp, context, format);
         * }
         * Buffer buffer = buffers[index];
         */
        // TODO : a optimiser (non multi-threads)

        Buffer buffer = new Buffer(this.resp, context, format);
        return buffer.toString(url);
    }


    /**
     * Buffer inner class.
     * 
     * @see CharBuffer
     */
    public class Buffer extends CharBuffer {

        /** HTTP servlet response. */
        private final HttpServletResponse resp;

        /** URL format. */
        private final URLFormat format;

        /** Prefix length. */
        private final int prefixLength;


        /**
         * Constructor.
         * 
         * @param resp HTTP servlet response
         * @param context URL context
         * @param format URL format
         */
        public Buffer(HttpServletResponse resp, URLContext context, URLFormat format) {
            this.resp = resp;
            this.format = format;

            //
            if (!format.isRelative()) {
                this.append(ServerInvocationContextImpl.this.requestRelativePrefix);
            } else {
                this.append(ServerInvocationContextImpl.this.requestPrefix);
            }

            // Append the servlet path
            switch (context.getMask()) {
                case URLContext.AUTH_MASK + URLContext.SEC_MASK:
                    this.append("/authsec");
                break;
                case URLContext.AUTH_MASK:
                    this.append("/auth");
                    break;
                case URLContext.SEC_MASK:
                    this.append("/sec");
                    break;
            }

            // Save the prefix length
            this.prefixLength = this.length;
        }


        /**
         * To string render method
         * 
         * @param url server URL
         * @return string render value
         */
        public String toString(ServerURL url) {
            // Reset the prefix length
            this.length = this.prefixLength;

            // julien : check UTF-8 is ok and should not be dependant on the response charset
            this.append(url.getPortalRequestPath());

            //
            boolean first = true;
            for (Entry<?, ?> element : url.getParameterMap().entrySet()) {
                String name = (String) element.getKey();
                String[] values = (String[]) element.getValue();
                for (String value : values) {
                    this.append(first ? '?' : '&');
                    this.append(name, urlEncoder);
                    this.append('=');
                    this.append(value, urlEncoder);
                    first = false;
                }
            }

            // Stringify
            String s = this.asString();

            // Let the servlet rewrite the URL if necessary
            if (this.format.isServletEncoded()) {
                synchronized (this.resp) {
                    s = this.resp.encodeURL(s);
                }
            }

            //
            return s;
        }
    }
}
