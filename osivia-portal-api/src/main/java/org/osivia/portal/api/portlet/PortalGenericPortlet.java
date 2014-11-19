package org.osivia.portal.api.portlet;

import javax.portlet.GenericPortlet;

import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.IDirectoryService;
import org.osivia.portal.api.directory.IDirectoryServiceLocator;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;

public abstract class PortalGenericPortlet extends GenericPortlet {

	
	private final INotificationsService notificationsService;

	private final IBundleFactory bundleFactory;

	private final IPortalUrlFactory portalUrlFactory;

	private final IDirectoryServiceLocator directoryServiceLocator;

	public PortalGenericPortlet() {
		super();

		this.notificationsService = Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);

		IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
				IInternationalizationService.MBEAN_NAME);

		this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

		this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);

		this.directoryServiceLocator = Locator.findMBean(IDirectoryServiceLocator.class, IDirectoryServiceLocator.MBEAN_NAME);
	}

	public IDirectoryService getDirectoryService() {
		return this.directoryServiceLocator.getDirectoryService();
	}

	/**
	 * @return the notificationsService
	 */
	public INotificationsService getNotificationsService() {
		return notificationsService;
	}

	/**
	 * @return the bundleFactory
	 */
	public IBundleFactory getBundleFactory() {
		return bundleFactory;
	}

	/**
	 * @return the portalUrlFactory
	 */
	public IPortalUrlFactory getPortalUrlFactory() {
		return portalUrlFactory;
	}

	protected void addNotification(PortalControllerContext pcc, String label, NotificationsType notificationType) {
		Bundle bundle = getBundleFactory().getBundle(pcc.getRequest().getLocale());
		String string = bundle.getString(label);
		getNotificationsService().addSimpleNotification(pcc, string, notificationType);
	}
}
