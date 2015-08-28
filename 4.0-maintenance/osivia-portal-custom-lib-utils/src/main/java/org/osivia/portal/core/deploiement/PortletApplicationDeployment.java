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
package org.osivia.portal.core.deploiement;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.deployment.DeploymentException;
import org.jboss.mx.util.MBeanProxyExt;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.core.deployment.jboss.PortletAppDeployment;
import org.jboss.portal.core.deployment.jboss.PortletAppDeploymentFactory;
import org.jboss.portal.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.contribution.IContributionService;
import org.osivia.portal.api.directory.IDirectoryServiceLocator;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.profils.IProfilManager;
import org.osivia.portal.core.web.IWebIdService;

/**
 * Portlet application deployment.
 *
 * @see PortletAppDeployment
 */
public class PortletApplicationDeployment extends PortletAppDeployment {

    /**
     * Constructor.
     *
     * @param url URL
     * @param pwa portal web app
     * @param listener managed object registry event listener
     * @param mbeanServer MBean server
     * @param factory portlet app deployment factory
     */
    public PortletApplicationDeployment(URL url, PortalWebApp pwa, ManagedObjectRegistryEventListener listener, MBeanServer mbeanServer,
            PortletAppDeploymentFactory factory) {
        super(url, pwa, listener, mbeanServer, factory);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws DeploymentException {
        // Inject services
        this.injectStandardService(Constants.CACHE_SERVICE_NAME, ICacheService.class.getName(), "osivia:service=CacheServices");
        this.injectStandardService(Constants.STATUS_SERVICE_NAME, IStatusService.class.getName(), "osivia:service=StatusServices");
        this.injectStandardService(Constants.URL_SERVICE_NAME, IPortalUrlFactory.class.getName(), "osivia:service=UrlFactory");
        this.injectStandardService(Constants.WEBID_SERVICE_NAME, IWebIdService.class.getName(), IWebIdService.MBEAN_NAME);
        this.injectStandardService(Constants.PROFILE_SERVICE_NAME, IProfilManager.class.getName(), "osivia:service=ProfilManager");
        this.injectStandardService(Constants.FORMATTER_SERVICE_NAME, IFormatter.class.getName(),
                "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        this.injectStandardService(Constants.NOTIFICATIONS_SERVICE_NAME, INotificationsService.class.getName(), INotificationsService.MBEAN_NAME);
        this.injectStandardService(Constants.INTERNATIONALIZATION_SERVICE_NAME, IInternationalizationService.class.getName(),
                IInternationalizationService.MBEAN_NAME);
        this.injectStandardService(Constants.CONTRIBUTION_SERVICE_NAME, IContributionService.class.getName(), IContributionService.MBEAN_NAME);
        this.injectStandardService(Constants.DIRECTORY_SERVICE_LOCATOR_NAME, IDirectoryServiceLocator.class.getName(), IDirectoryServiceLocator.MBEAN_NAME);


        // FIXME à déplacer dans CMS
        this.injectStandardService("NuxeoService", "fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService", "osivia:service=NuxeoService");


        InputStream source = null;
        try {
            // osivia-portal.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/osivia-portal.tld"));
            this.pwa.importFile("/WEB-INF/theme", "osivia-portal.tld", source, false);

            // internationalization.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/internationalization.tld"));
            this.pwa.importFile("/WEB-INF/theme", "internationalization.tld", source, false);

            // spring
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/spring.tld"));
            this.pwa.importFile("/WEB-INF/theme", "spring.tld", source, false);
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/spring-form.tld"));
            this.pwa.importFile("/WEB-INF/theme", "spring-form.tld", source, false);
            
            // displaytag.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/displaytag.tld"));
            this.pwa.importFile("/WEB-INF/theme", "displaytag.tld", source, false);
            
            // displaytag-el.tld
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/theme/displaytag-el.tld"));
            this.pwa.importFile("/WEB-INF/theme", "displaytag-el.tld", source, false);
        } catch (IOException e) {
            throw new DeploymentException("Cannot import taglib", e);
        } finally {
            IOTools.safeClose(source);
        }

        //
        super.start();
    }


    /**
     * Utility method used to inject standard service.
     *
     * @param serviceName service name
     * @param serviceClass service class
     * @param serviceRef service reference
     */
    protected void injectStandardService(String serviceName, String serviceClass, String serviceRef) {
        try {
            Class<?> proxyClass = this.pwa.getClassLoader().loadClass(serviceClass);
            ObjectName objectName = ObjectName.getInstance(serviceRef);
            Object proxy = MBeanProxyExt.create(proxyClass, objectName, this.mbeanServer);
            this.pwa.getServletContext().setAttribute(serviceName, proxy);
        } catch (Exception e) {
            this.log.error("Was not able to create service proxy", e);
        }
    }

}
