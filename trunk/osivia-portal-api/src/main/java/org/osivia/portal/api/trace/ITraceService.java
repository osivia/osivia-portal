/**
 * 
 */
package org.osivia.portal.api.trace;

import org.osivia.portal.api.PortalException;


/**
 * Service used to collect data and store into a nosqldb 
 * @author Loïc Billon
 *
 */
public interface ITraceService {


	/**
	 * Log a trace in the stats repository
	 * @param t
	 * @throws PortalException
	 */
	void trace(Trace t) throws PortalException;
	
	/**
	 * Disconnect the service
	 */
	void stopService();

	/**
	 * Test if service is avaliable
	 * @return
	 */
	boolean enabled();



}
