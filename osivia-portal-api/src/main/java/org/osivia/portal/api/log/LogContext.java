package org.osivia.portal.api.log;

import org.osivia.portal.api.context.PortalControllerContext;

/**
 * Log context interface.
 * 
 * @author Cédric Krommenhoek
 */
public interface LogContext {

    /** MBean name. */
    String MBEAN_NAME = "osivia:service=LogContext";
    
    
    /**
     * Create a new log context.
     * 
     * @param portalControllerContext portal controller context
     * @param domain error domain
     * @param code error code
     * @param log context token
     */
    String createContext(PortalControllerContext portalControllerContext, String domain, String code);
    
    
    /**
     * Get current log context token.
     * 
     * @param portalControllerContext portal controller context
     * @return token
     */
    String getToken(PortalControllerContext portalControllerContext);
    
    
    /**
     * Delete current log context.
     * 
     * @param portalControllerContext portal controller context
     */
    void deleteContext(PortalControllerContext portalControllerContext);
    
}
