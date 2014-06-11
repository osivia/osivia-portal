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


    public <T extends DirectoryBean> T getForeignBean(String name, Class<T> type) {
        if (getPortletContext() != null) {
            IDirectoryService directoryService = getDirectoryServiceLocator().getDirectoryService();
            T directoryBean = directoryService.getDirectoryBean(name, type);

            logger.info("---- get : " + name + " : " + directoryBean);

            return directoryBean;
        } else

            logger.info("---- get : " + name + " : no portletCtx");

            return null;
    }

}
