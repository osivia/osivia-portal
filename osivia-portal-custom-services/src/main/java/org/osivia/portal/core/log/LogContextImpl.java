package org.osivia.portal.core.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.log.LogContext;

/**
 * Log context implementation.
 * 
 * @author Cédric Krommenhoek
 * @see LogContext
 */
public class LogContextImpl implements LogContext {

    /** Log context token request attribute. */
    private static final String TOKEN_ATTRIBUTE = "osivia.log.token";
    
    
    /**
     * Constructor.
     */
    public LogContextImpl() {
        super();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String createContext(PortalControllerContext portalControllerContext, String domain, String code) {
        // Token
        String token = this.generateNewToken(portalControllerContext);
        MDC.put("token", StringUtils.trimToEmpty(token));

        // Current user
        String user = this.getCurrentUser(portalControllerContext);
        MDC.put("user", StringUtils.trimToEmpty(user));
        
        // Error domain & code
        MDC.put("domain", StringUtils.trimToEmpty(domain));
        MDC.put("code", StringUtils.trimToEmpty(code));
        
        return token;
    }

    
    /**
     * Generate new token.
     * 
     * @param portalControllerContext portal controller context
     * @return token
     */
    private String generateNewToken(PortalControllerContext portalControllerContext) {
        
        String serverName = "";
        if (portalControllerContext.getHttpServletRequest() != null)    {
            Cookie[] cookies = portalControllerContext.getHttpServletRequest().getCookies();
            for(int i=0; i< cookies.length; i++) {
                Cookie cookie = cookies[i];
                if( "ROUTEID".equals(cookie.getName())) {
                    serverName = cookie.getValue() + ":" ;
                }
            }
        }
        
        String token = "#" + serverName + String.valueOf(System.currentTimeMillis());
        
        if (portalControllerContext.getRequest() != null) {
            portalControllerContext.getRequest().setAttribute(TOKEN_ATTRIBUTE, token);
        } else if (portalControllerContext.getHttpServletRequest() != null) {
            portalControllerContext.getHttpServletRequest().setAttribute(TOKEN_ATTRIBUTE, token);
        }
        
        return token;
    }
    
    
    /**
     * Get current user identifier.
     * 
     * @param portalControllerContext portal controller context
     * @return user identifier
     */
    private String getCurrentUser(PortalControllerContext portalControllerContext) {
        String user;

        if (portalControllerContext.getHttpServletRequest() != null) {
            user = StringUtils.defaultIfEmpty(portalControllerContext.getHttpServletRequest().getRemoteUser(), "anonymous");
        } else {
            user = null;
        }

        return user;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getToken(PortalControllerContext portalControllerContext) {
        String token;
        
        if (portalControllerContext.getRequest() != null) {
            token = (String) portalControllerContext.getRequest().getAttribute(TOKEN_ATTRIBUTE);
        } else if (portalControllerContext.getHttpServletRequest() != null) {
            token = (String) portalControllerContext.getHttpServletRequest().getAttribute(TOKEN_ATTRIBUTE);
        } else {
            token = null;
        }
        
        return token;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteContext(PortalControllerContext portalControllerContext) {
        Map<?, ?> context = MDC.getContext();
        
        if (MapUtils.isNotEmpty(context)) {
            List<String> keys = new ArrayList<>(context.size());
            for (Object object : context.keySet()) {
                String key = (String) object;
                keys.add(key);
            }

            for (String key : keys) {
                MDC.remove(key);
            }
        }
    }
    
}
