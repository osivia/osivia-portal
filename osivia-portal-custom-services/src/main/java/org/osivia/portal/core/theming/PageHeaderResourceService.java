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
 */
package org.osivia.portal.core.theming;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.osivia.portal.core.constants.InternalConstants;


/**
 * Page header resource service implementation.
 *
 * @author Jean-Sébastien Steux
 * @author Cédric Krommenhoek
 * @see IPageHeaderResourceService
 */
public class PageHeaderResourceService implements IPageHeaderResourceService {

    /** Manifest resource path. */
    private static final String MANIFEST_RESOURCE_PATH = "/META-INF/MANIFEST.MF";
    /** Manifest attribute name for maven-generated version number. */
    private static final String MAVEN_VERSION_MANIFEST_ATTRIBUTE = "Implementation-Version";

    /** Page header resource cache. */
    private final PageHeaderResourceCache cache;

    /** Resource pattern. */
    private final Pattern resourcePattern;


    /**
     * Constructor.
     */
    public PageHeaderResourceService() {
        this.cache = PageHeaderResourceCache.getInstance();
        this.resourcePattern = Pattern.compile("^(.+(href|src)=[^/]+)((/[^/]+)/.*)(\\.css|\\.js)(.+)$", Pattern.CASE_INSENSITIVE);
    }


    /**
     * {@inheritDoc}
     */
    public void deploy(PortalWebApp portalWebApp) {
        // Servlet context
        ServletContext servletContext = portalWebApp.getServletContext();
        if (servletContext != null) {
            try {
                URL url = servletContext.getResource(MANIFEST_RESOURCE_PATH);
                Manifest manifest = new Manifest(url.openStream());
                Attributes attributes = manifest.getMainAttributes();
                String version = attributes.getValue(MAVEN_VERSION_MANIFEST_ATTRIBUTE);
                this.cache.addVersion(portalWebApp.getContextPath(), version);
            } catch (Exception e) {
                // Do nothing
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void undeploy(PortalWebApp portalWebApp) {
        this.cache.removeVersion(portalWebApp.getContextPath());
        this.cache.clearAdaptedElements();
    }


    /**
     * {@inheritDoc}
     */
    public String adaptResourceElement(String originalElement) {
        String adaptedElement = this.cache.getAdaptedElement(originalElement);
        if (adaptedElement == null) {
            // Default value
            adaptedElement = originalElement;

            if (BooleanUtils.toBoolean(System.getProperty(InternalConstants.SYSTEM_PROPERTY_ADAPT_RESOURCE))) {
                Matcher matcher = this.resourcePattern.matcher(originalElement.trim());
                if (matcher.matches()) {
                    // Context path
                    String contextPath = matcher.group(4);
                    // Version
                    String version = this.cache.getVersion(contextPath);

                    StringBuilder builder = new StringBuilder();
                    builder.append(matcher.group(1));
                    builder.append(matcher.group(3));
                    if (version != null) {
                        builder.append("-adapt-").append(version);
                    }
                    builder.append(matcher.group(5));
                    builder.append(matcher.group(6));
                    adaptedElement = builder.toString();
                }
            }

            // Add adapted URL in cache
            this.cache.addAdaptedElement(originalElement, adaptedElement);
        }
        return adaptedElement;
    }

}
