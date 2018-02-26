package org.osivia.portal.api.log;

import org.osivia.portal.api.locator.Locator;

/**
 * Log context factory.
 * 
 * @author Cédric Krommenhoek
 */
public abstract class LogContextFactory {

    /**
     * Get log context.
     * 
     * @return log context
     */
    public static LogContext getLogContext() {
        return Locator.findMBean(LogContext.class, LogContext.MBEAN_NAME);
    }

}
