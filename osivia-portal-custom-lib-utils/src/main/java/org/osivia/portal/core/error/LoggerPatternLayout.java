package org.osivia.portal.core.error;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.osivia.portal.api.locator.Locator;



/**
 * The Class LoggerPatternLayout. This a custom pattern adapted for the noe logging system
 */
public class LoggerPatternLayout extends PatternLayout {

    /**
     * Instantiates a new logger pattern layout.
     */
    public LoggerPatternLayout() {
        super("%d %m");
        
    }



    /* (non-Javadoc)
     * @see org.apache.log4j.PatternLayout#format(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public String format(LoggingEvent event) {
        
        // Nuxeo service
        IPortalLogger portalLogger = Locator.findMBean(IPortalLogger.class, "osivia:service=LoggerService");
        return portalLogger.log(this, event);

    }

    /* (non-Javadoc)
     * @see org.apache.log4j.PatternLayout#getConversionPattern()
     */
    @Override
    public String getConversionPattern() {
       
        return "%d %-5p [%c] %m%n";
    }
  
    /* (non-Javadoc)
     * @see org.apache.log4j.PatternLayout#ignoresThrowable()
     */
    @Override
    public
    boolean ignoresThrowable() {
      return false;
    }

}
