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

/**
 * Abstract portlet class used to access basic services such as notification,
 * directories, etc.
 * 
 * @author lbillon
 * 
 */
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

	/**
	 * Display a notification with a key label
	 * 
	 * @param pcc
	 * @param label
	 *            the key in resources bundle
	 * @param notificationType
	 *            the notification type
	 */
	protected void addNotification(PortalControllerContext pcc, String label, NotificationsType notificationType) {
		Bundle bundle = getBundleFactory().getBundle(pcc.getRequest().getLocale());
		String string = bundle.getString(label);
		getNotificationsService().addSimpleNotification(pcc, string, notificationType);
	}

	/**
	 * Display a notification with a key label and variables
	 * 
	 * @param pcc
	 * @param label
	 *            the key in resources bundle
	 * @param notificationType
	 *            the notification type
	 * @param args
	 *            variables
	 */
	protected void addNotification(PortalControllerContext pcc, String label, NotificationsType notificationType, Object... args) {
		Bundle bundle = getBundleFactory().getBundle(pcc.getRequest().getLocale());
		String string = bundle.getString(label, args);
		getNotificationsService().addSimpleNotification(pcc, string, notificationType);
	}
}
