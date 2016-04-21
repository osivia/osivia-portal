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
import org.osivia.portal.api.menubar.IMenubarService;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.trace.ITraceService;
import org.osivia.portal.api.trace.ITraceServiceLocator;
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
	
	private final IMenubarService menubarService;
	
	private final ITraceServiceLocator traceServiceLocator;

	public PortalGenericPortlet() {
		super();

		this.notificationsService = Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);

		IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
				IInternationalizationService.MBEAN_NAME);

		this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());

		this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);

		this.directoryServiceLocator = Locator.findMBean(IDirectoryServiceLocator.class, IDirectoryServiceLocator.MBEAN_NAME);
		
		this.menubarService = Locator.findMBean(IMenubarService.class, IMenubarService.MBEAN_NAME);
		
		this.traceServiceLocator = Locator.findMBean(ITraceServiceLocator.class, ITraceServiceLocator.MBEAN_NAME);
	}

	public IDirectoryService getDirectoryService() {
		return this.directoryServiceLocator.getDirectoryService();
	}
	
	/**
	 * @return the traceService via the locator
	 */
	public ITraceService getTraceService() {
		return this.traceServiceLocator.getService();
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
	 * @return the menubarService
	 */
	public IMenubarService getMenubarService() {
		return menubarService;
	}

	/**
	 * Return a label in resources files
	 * @param pcc
	 * @param label the key in resources bundle
	 * @return the label translated
	 */
	protected String getMessage(PortalControllerContext pcc, String label) {
		Bundle bundle = getBundleFactory().getBundle(pcc.getRequest().getLocale());
		return bundle.getString(label);
	}	
	
	/**
	 * Return a label in resources files
	 * @param pcc
	 * @param label the key in resources bundle
	 * @param args some args
	 * @return the label translated
	 */
	protected String getMessage(PortalControllerContext pcc, String label, Object... args) {
		Bundle bundle = getBundleFactory().getBundle(pcc.getRequest().getLocale());
		return bundle.getString(label, args);
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
		String string = getMessage(pcc, label);
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
		String string = getMessage(pcc, label, args);
		getNotificationsService().addSimpleNotification(pcc, string, notificationType);
	}
}
