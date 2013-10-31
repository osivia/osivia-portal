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
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.status.IStatusService;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.formatters.IFormatter;
import org.osivia.portal.core.profils.IProfilManager;

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
        this.injectStandardService(Constants.PROFILE_SERVICE_NAME, IProfilManager.class.getName(), "osivia:service=ProfilManager");
        this.injectStandardService(Constants.FORMATTER_SERVICE_NAME, IFormatter.class.getName(),
                "osivia:service=Interceptor,type=Command,name=AssistantPageCustomizer");
        this.injectStandardService(Constants.NOTIFICATIONS_SERVICE_NAME, INotificationsService.class.getName(), INotificationsService.MBEAN_NAME);
        this.injectStandardService(Constants.INTERNATIONALIZATION_SERVICE_NAME, IInternationalizationService.class.getName(),
                IInternationalizationService.MBEAN_NAME);

        // FIXME à déplacer dans CMS
        this.injectStandardService("NuxeoService", "fr.toutatice.portail.cms.nuxeo.api.services.INuxeoService", "osivia:service=NuxeoService");


        // internationalization.tld
        InputStream source = null;
        try {
            source = IOTools.safeBufferedWrapper(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("conf/theme/internationalization.tld"));
            this.pwa.importFile("/WEB-INF/theme", "internationalization.tld", source, false);
        } catch (IOException e) {
            throw new DeploymentException("Cannot import internationalization.tld", e);
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
