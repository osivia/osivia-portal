/*
 * (C) Copyright 2016 OSIVIA (http://www.osivia.com)
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
package org.osivia.portal.api.directory.v2;

import org.osivia.portal.api.locator.Locator;

/**
 * This is a factory for returning an instance of service
 * @author Loïc Billon
 * @since 4.4
 *
 */
public class DirServiceFactory {

	/**
	 * Factory method of a service
	 * @param clazz the type of service (IDirService)
	 * @return an instance of a service
	 */
	public static <D extends IDirService>  D getService(Class<D> clazz) {
		
		IDirProvider provider = Locator.findMBean(IDirProvider.class, IDirProvider.MBEAN_NAME);
		
		return provider.getDirService(clazz);
	}


    /**
     * Get current transaction manager delegate (for composite transactions management)
     * 
     * @return txManagerDelegate
     */
    public static Object getDirectoryTxManagerDelegate() {

        IDirProvider provider = Locator.findMBean(IDirProvider.class, IDirProvider.MBEAN_NAME);

        return provider.getDirectoryTxManagerDelegate();
    }

}
