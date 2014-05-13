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

package org.osivia.portal.core.theming;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.portal.server.deployment.PortalWebApp;


/**
 * The Class PageHeaderResourceService.
 */
public class PageHeaderResourceService implements IPageHeaderResourceService {


    /** The href. */
    Pattern href = Pattern.compile("(.*)(href|src)=\"((?:[^\"])*)\"((.*))(\n)*");


    /* (non-Javadoc)
     * @see org.osivia.portal.core.theming.IPageHeaderResourceService#deploy(org.jboss.portal.server.deployment.PortalWebApp)
     */
    public void deploy(PortalWebApp pwa) {


        Manifest manifest;
        try {
            if (pwa.getServletContext() != null) {

                InputStream is = pwa.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");

                if (is != null) {

                    manifest = new Manifest(is);

                    Attributes attrs = manifest.getMainAttributes();
                    if (attrs != null) {
                        String builtBy = attrs.getValue("Built-By");

                        if (builtBy != null)
                            // Add to cache
                            PageHeaderResourceCache.contextVersions.put(pwa.getContextPath(), builtBy);
                    }

                }
            }

        } catch (Exception e) {
            // NO MANIFEST
        }


    }

    /* (non-Javadoc)
     * @see org.osivia.portal.core.theming.IPageHeaderResourceService#undeploy(org.jboss.portal.server.deployment.PortalWebApp)
     */
    public void undeploy(PortalWebApp pwa) {
        PageHeaderResourceCache.contextVersions.remove(pwa.getContextPath());

    }


    /* (non-Javadoc)
     * @see org.osivia.portal.core.theming.IPageHeaderResourceService#adaptResourceElement(java.lang.String)
     */
    public String adaptResourceElement(String originalResourceURL) {

        Matcher mHref = href.matcher(originalResourceURL);
        if (mHref.matches()) {


                String url = mHref.group(3);

                String[] contexts = url.split("/");

                if (contexts.length > 2) {
                    
                    String context = "/" + contexts[1];

                    String builtBy = PageHeaderResourceCache.contextVersions.get(context);

                    if (builtBy != null) {
                        // build new url
                        url = url + "?builtBy=" + builtBy;
                        
                        // Concat all groups

                        String hrefResult = mHref.group(1) + mHref.group(2) + "=\"" + url + "\"" + mHref.group(4);

                        return hrefResult;
                    }
                }
            
        }

        return originalResourceURL;
    }

}
