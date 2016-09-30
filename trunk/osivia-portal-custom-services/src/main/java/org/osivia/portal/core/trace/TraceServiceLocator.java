/**
 * 
 */
package org.osivia.portal.core.trace;

import org.osivia.portal.api.trace.ITraceService;
import org.osivia.portal.api.trace.ITraceServiceLocator;

/**
 * @author Loïc Billon
 *
 */
public class TraceServiceLocator implements ITraceServiceLocator {

	private ITraceService service;
	
	/* (non-Javadoc)
	 * @see org.osivia.portal.api.trace.ITraceServiceLocator#register(org.osivia.portal.api.trace.ITraceService)
	 */
	public void register(ITraceService service) {
		this.service = service;
		
	}

	/* (non-Javadoc)
	 * @see org.osivia.portal.api.trace.ITraceServiceLocator#getService()
	 */
	public ITraceService getService() {
		// TODO Auto-generated method stub
		return service;
	}

	/* (non-Javadoc)
	 * @see org.osivia.portal.api.trace.ITraceServiceLocator#unregister(org.osivia.portal.api.trace.ITraceService)
	 */
	public void unregister(ITraceService service) {
		if(this.service == service) {
			service.stopService();
			this.service = null;
		}
		
	}

}
