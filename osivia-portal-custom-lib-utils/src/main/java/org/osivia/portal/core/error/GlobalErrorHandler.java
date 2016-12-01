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
 */
package org.osivia.portal.core.error;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.tracker.ITracker;


public class GlobalErrorHandler {

    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String TIME_FORMAT = "HH:mm:ss";
    
    protected static final Log logger = LogFactory.getLog("PORTAL_ERROR");

    private static GlobalErrorHandler s_instance = null;

    public static synchronized GlobalErrorHandler getInstance() {
        if (s_instance == null) {
            s_instance = new GlobalErrorHandler();
        }
        return s_instance;
    }

    private long currentErrorId = 0;
    private Log log;

    protected GlobalErrorHandler() {
        super();
        log = LogFactory.getLog("PORTAL_USER_ERROR");
    }

    // v.1.0.21 : desynchronisation
    public synchronized long logError(ErrorDescriptor error) {
        currentErrorId++;
        error.setErrorId(currentErrorId);

        int errorCode = error.getHttpErrCode();


        Map<String,Object> properties = error.getProperties();

        
        String url = null;
        

        HttpServletRequest request = null;
        
        ITracker tracker = Locator.findMBean(ITracker.class, "osivia:service=Tracker");
        if (tracker != null) {
            request = tracker.getHttpRequest();
            if (request != null) {
                // portal url
                url = (String) request.getAttribute("osivia.trace.url");
            }
        }

        // applicative url
        if (properties.get("osivia.url") != null)
            url = (String) properties.get("osivia.url");

        // userAgent
        String userAgent = "";
        if( request != null){
            userAgent = request.getHeader("User-Agent");
        }
        
        if( properties.get("osivia.header.userAgent") != null)
            userAgent = (String) properties.get("osivia.header.userAgent");
        
        
        

        log.error(asString(error, url, request, userAgent));

        String msg = "#" + currentErrorId + "# ";

        
        
        
        String displayContext = (String) properties.get("osivia.cms.target");
        if( displayContext != null){
            displayContext = "nx:"+displayContext;
        } else  {

            displayContext = (String) properties.get("osivia.portal.portalObject");
            if (displayContext != null)
                displayContext = "portal:"+displayContext;
            else    {
                 displayContext = (String) properties.get("osivia.portal.portlet");
                if (displayContext != null)
                    displayContext = "porlet:"+displayContext;
                else
                    displayContext = url;
            }
        }
        
        if( displayContext != null) {
            msg += " ["+displayContext+"]";
        }
         
        String applicativeMessage = null;
        
        
        if (error.getMessage() != null) {
            applicativeMessage = error.getMessage();
        }   else    {
            if (errorCode == HttpServletResponse.SC_INTERNAL_SERVER_ERROR)  {
                applicativeMessage = "Technical error";
            }
            if (errorCode == HttpServletResponse.SC_NOT_FOUND)  {
                applicativeMessage = "Not found";
            }
            if (errorCode == HttpServletResponse.SC_FORBIDDEN)  {
                applicativeMessage = "Forbidden";
            }
            if (errorCode == HttpServletResponse.SC_REQUEST_TIMEOUT)  {
                applicativeMessage = "Timeout expired";
            }
        }
        
        // Only 50x errors
        if( ((errorCode == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) 
                || (errorCode == HttpServletResponse.SC_REQUEST_TIMEOUT)
                ) && applicativeMessage != null)
            IPortalLogger.logger.error(error);
        
        
        if( applicativeMessage != null) {
            msg += " " + applicativeMessage;
        }
      
        
        
        logger.error(msg);


        return currentErrorId;
    }


    private static String asString(ErrorDescriptor error, String url, HttpServletRequest request, String userAgent ) {
        StringBuffer sb = new StringBuffer();
        appendAsText(sb, error, url, request, userAgent);
        return sb.toString();
    }

    private static void appendAsText(StringBuffer sb, ErrorDescriptor error, String url, HttpServletRequest request, String userAgent ) {
        if (error != null) {
            sb.append("\n\n----------------------------------------------------------------\n");

            sb.append("\nNumError: #" + error.getErrorId() + "#");

            Date errDate = error.getDate();
            if (errDate != null) {
                sb.append("\ndate: ");
                sb.append(format(errDate));
                sb.append("\n");
            }
            String userId = error.getUserId();
            if (isNotEmpty(userId)) {
                sb.append("user: ");
                sb.append(userId);
                sb.append("\n");
            }

            int errorCode = error.getHttpErrCode();
            if (errorCode != ErrorDescriptor.NO_HTTP_ERR_CODE) {
                sb.append("http code: ");
                sb.append(errorCode);
                sb.append("\n");
            }

            if (isNotEmpty(url)) {
                sb.append("url: ");
                sb.append(url);
                sb.append("\n");
            }
            
            if (isNotEmpty(userAgent)) {
                sb.append("userAgent: ");
                sb.append(userAgent);
                sb.append("\n");
            }


            
            Map<String, Object> errContext = error.getProperties();
            if (errContext != null) {
                for (Map.Entry<String, Object> entry : errContext.entrySet()) {
                    String key = entry.getKey();
                    if( !StringUtils.equals(key, "osivia.url") && !StringUtils.equals(key, "osivia.header.userAgent"))  {
                         sb.append(entry.getKey()).append(": ");
                        sb.append(entry.getValue());
                        sb.append("\n");
                    }
                }
                sb.append("\n");
            }
           

            if (error.getMessage() != null) {
                sb.append("msg: " + error.getMessage());
                sb.append("\n");     
            }
            
            
            if (error.getException() != null)
                sb.append(error.getStack());

            
            if( request != null)    {

                try {
                    
                HttpSession session = request.getSession();
                List<UserAction> historic = (List<UserAction>) session.getAttribute("osivia.trace.historic");
                if (historic != null && historic.size() > 0) {
                    sb.append("historic: ");
                    String display = "";
                    for(UserAction userAction: historic)    {
                        display += "\n  " + formatTime( new Date(userAction.getTimestamp()))+" : "+userAction.getCommand()+" ";
                    }
                    sb.append(display);
                    sb.append("\n\n");

                }}
                catch( Exception e) {
                    // Illegalstate si appel depuis valve
                }
            }


        }
    }

    private static String format(Date date) {
        return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
    }
    
    private static String formatTime(Date date) {
        return new SimpleDateFormat(TIME_FORMAT).format(date);
    }

    private static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

}
