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
