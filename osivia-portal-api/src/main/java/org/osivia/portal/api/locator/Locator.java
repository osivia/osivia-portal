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
package org.osivia.portal.api.locator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.mx.util.MBeanProxy;
import org.jboss.mx.util.MBeanProxyCreationException;
import org.jboss.mx.util.MBeanServerLocator;

public class Locator {
	
	/** 
	 * Return a MBean specified by its class and lookup name using JBoss JMX utilities.
	 * @throws NullPointerException 
	 * @throws MBeanProxyCreationException 
	 * @throws MalformedObjectNameException 
	 * @see org.jboss.mx.util.MBeanProxy
	 * @see org.jboss.mx.util.MBeanServerLocator
	 */
	public static <T> T findMBean(Class<T> type, String lookupName)   {
		
		try	{

		MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();
		Object mbean = MBeanProxy.get(type,	new ObjectName(lookupName),	mbeanServer);
		return type.cast(mbean);
		} catch( Exception e){
			throw new RuntimeException(e);
		}

	}


}
