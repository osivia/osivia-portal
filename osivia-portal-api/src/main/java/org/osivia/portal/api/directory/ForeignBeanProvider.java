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
package org.osivia.portal.api.directory;

import javax.portlet.PortletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.Constants;

/**
 * Used to provided beans from service-directory portlet to client portlets
 * 
 * @author lbillon
 * 
 */
public abstract class ForeignBeanProvider {


    protected static final Log logger = LogFactory.getLog(ForeignBeanProvider.class.getName());

    private PortletContext portletContext;



    public void setPortletContext(PortletContext portletContext) {
        this.portletContext = portletContext;

    }


    public PortletContext getPortletContext() {
        return portletContext;
    }


    public IDirectoryServiceLocator getDirectoryServiceLocator() {
        if (getPortletContext() != null) {
            IDirectoryServiceLocator directoryServiceLocator = (IDirectoryServiceLocator) this.getPortletContext().getAttribute(
                    Constants.DIRECTORY_SERVICE_LOCATOR_NAME);

            return directoryServiceLocator;
        } else
            return null;
    }

	/**
	 * get a bean in the service-directory spring context
	 * 
	 * @param name
	 *            name of the bean
	 * @param type
	 *            classType of the bean
	 * @return
	 * @deprecated use org.osivia.portal.api.directory.v2.DirServiceFactory.getService(Class<D>)
	 */
	@Deprecated
	public synchronized <T extends DirectoryBean> T getForeignBean(String name, Class<T> type) {
		T directoryBean = null;

		if (getPortletContext() != null) {
			IDirectoryService directoryService = getDirectoryServiceLocator().getDirectoryService();
			int tempo = 0;

			// TODO, tempo inutile refs #726
			// while (directoryService == null && tempo <= 60) {
			// try {
			// logger.warn("gusseing " + name +
			// ", waiting for identity service");
			// this.wait(5000);
			// tempo = tempo + 5;
			//
			// } catch (InterruptedException e) {
			//
			// }
			//
			// directoryService =
			// getDirectoryServiceLocator().getDirectoryService();
			//
			// }

			if(directoryService != null) {
				directoryBean = directoryService.getDirectoryBean(name, type);
			}
			else {
				logger.warn("no identity service registered!");
			}
		}

		return directoryBean;
	}

}
