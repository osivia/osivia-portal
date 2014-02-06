package org.osivia.portal.core.profiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.tracker.ITracker;


/**
 * Classe utilitaire permettant de diagnostiquer les temps d'execution des différents composants
 * 
 * @author jeanseb
 * 
 */

public class ProfilerService implements IProfilerService {

    private static Log logger = LogFactory.getLog("PORTAL_PROFILER");

    private ITracker tracker;

    public ITracker getTracker() {
        return tracker;
    }

    public void setTracker(ITracker tracker) {
        this.tracker = tracker;
    }


    public void logEvent(String category, String name, long time, boolean error) {

        if (logger.isInfoEnabled()) {
            
            String cmsPath = null;
            
            
            HttpServletRequest servletRequest = getTracker().getHttpRequest();
            
            if( servletRequest != null)
                cmsPath = (String) getTracker().getHttpRequest().getAttribute("osivia.profiler.cmsPath");
            
            if( cmsPath == null)
                cmsPath = "";

            synchronized (this) {
                String msg = "";

                SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss:SSS");
                msg += timeFormater.format(new Date(System.currentTimeMillis()));


                msg += ";" + Thread.currentThread().getId();
                
                msg += ";" + cmsPath;

                msg += ";" + category.replaceAll(";", " ");
                msg += ";" + name.replaceAll(";", " ");
                msg += ";" + (!error ? time : "-");
                logger.info(msg);
            }
        }

    }


}
