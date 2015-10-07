/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.jboss.portal.theme.deployment.jboss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.management.MBeanServer;

import org.jboss.deployment.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.jboss.portal.server.deployment.jboss.Deployment;
import org.jboss.portal.theme.LayoutException;
import org.jboss.portal.theme.LayoutService;
import org.jboss.portal.theme.RuntimeContext;
import org.jboss.portal.theme.metadata.PortalLayoutMetaData;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

/**
 * Deploy the layouts contained in a WebApplication.
 * <p>
 * A Web Application can define layouts to be offered to the portal. Layouts are JSPs or Servlets, that take over the job of presenting the rendered content of
 * a portal request to the end device. Layouts need to be announced to the portal via the /WEB-INF/portal-layouts.xml descriptor (see also portal-layouts.dtd)
 * </p>
 *
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @version $Revision: 8784 $
 */
public class LayoutDeployment extends Deployment {

    /** Log. */
    private static Logger log = Logger.getLogger(LayoutDeployment.class);
    /** Layout deployment factory. */
    private final LayoutDeploymentFactory factory;


    /**
     * Constructor.
     *
     * @param url URL
     * @param pwa portal web app
     * @param mbeanServer mbean server
     * @param layoutDeploymentFactory layout deployment factory
     */
    public LayoutDeployment(URL url, PortalWebApp pwa, MBeanServer mbeanServer, LayoutDeploymentFactory layoutDeploymentFactory) {
        super(url, pwa, mbeanServer);
        this.factory = layoutDeploymentFactory;
    }


    /**
     * Create the resources from this portal web application.
     *
     * @throws DeploymentException if anything goes wrong
     */
    @Override
    @SuppressWarnings("unchecked")
    public void create() throws DeploymentException {
        log.debug("Create new layout(s), found in :  " + this.pwa.getId());

        //
        InputStream in = null;
        try {
            in = IOTools.safeBufferedWrapper(this.url.openStream());
            LayoutService layoutService = this.factory.getLayoutService();
            RuntimeContext ctx = new RuntimeContext(this.pwa.getId(), this.pwa.getServletContext(), this.pwa.getContextPath(), this.pwa.getClassLoader());
            Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
            List<PortalLayoutMetaData> portalLayouts = (List<PortalLayoutMetaData>) unmarshaller.unmarshal(in, new PortalLayoutMetaDataFactory(), null);
            for (PortalLayoutMetaData layoutMD : portalLayouts) {
                layoutService.addLayout(ctx, layoutMD);
            }
            log.debug("Done creating new layout(s), found in :  " + this.pwa.getId());
        } catch (Exception e) {
            throw new DeploymentException(e);
        } finally {
            IOTools.safeClose(in);
        }

        // Make sure that the portal-layout.tld and custom taglibs are available in the local context
        File targetContextRoot = new File(this.pwa.getServletContext().getRealPath("/WEB-INF"));
        if (targetContextRoot.exists() && targetContextRoot.isDirectory()) {
            InputStream source = null;

            // portal-layout.tld
            try {
                source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/portal-layout.tld"));
                this.pwa.importFile("/WEB-INF/theme", "portal-layout.tld", source, false);
            } catch (IOException e) {
                throw new DeploymentException("Cannot import portal-layout.tld", e);
            } finally {
                IOTools.safeClose(source);
            }
        } else {
            log.warn("Cannot access the WEB-INF folder for the deployed application: " + this.pwa.getId());
        }
    }


    /**
     * Destroy (and remove) all the resources from this portal web application that are registered with the portal.
     *
     * @throws DeploymentException if anything goes wrong
     */
    @Override
    public void destroy() throws DeploymentException {
        if (log.isDebugEnabled()) {
            log.debug("destroying layout(s), found in :  " + this.pwa.getId());
        }
        try {
            LayoutService server = this.factory.getLayoutService();
            server.removeLayouts(this.pwa.getId());
        } catch (LayoutException e) {
            throw new DeploymentException(e);
        }
        if (log.isDebugEnabled()) {
            log.debug("done destroying layout(s), found in :  " + this.pwa.getId());
        }
    }

}
