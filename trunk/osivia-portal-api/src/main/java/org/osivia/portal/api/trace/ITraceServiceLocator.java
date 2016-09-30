/**
 * 
 */
package org.osivia.portal.api.trace;

/**
 * Locator of the TraceService
 * @author Loïc Billon
 *
 */
public interface ITraceServiceLocator {

	final static String MBEAN_NAME = "osivia:service=TraceServiceLocator";
	
	
	public ITraceService getService();

	/**
	 * @param service
	 */
	public void register(ITraceService service);
	
	/**
	 * @param service
	 */
	public void unregister(ITraceService service);
}
