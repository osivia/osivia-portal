package org.jboss.portal.core.model.portal.navstate;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.invocation.AttributeResolver;


public class NavigationAttributeResolver implements AttributeResolver {

    final static String mapName = "osivia.navigationMap";
    final HttpServletRequest request;
    private static final Logger logger = Logger.getLogger(NavigationAttributeResolver.class);


    public NavigationAttributeResolver(HttpServletRequest request) {
        super();
        this.request = request;

    }

    private synchronized Map<Object, Object> getMap() {
        Map<Object, Object> map = (Map<Object, Object>) request.getAttribute(mapName);
        if (map == null) {
            map = new ConcurrentHashMap<>();
            request.setAttribute(mapName, map);
        }
        return map;
    }

    @Override
    public Set getKeys() {
        return getMap().keySet();
    }


    private void dump(String action, Object attrKey, Object attrValue) {
        if (attrValue != null && attrValue instanceof PageNavigationalState) {
            PageNavigationalState pageState = (PageNavigationalState) attrValue;
            String[] sPath = null;
            if (pageState != null) {
                sPath = pageState.getParameter(new QName(XMLConstants.DEFAULT_NS_PREFIX, "osivia.cms.contentPath"));
            }

            String pathPublication = null;
            if ((sPath != null) && (sPath.length > 0)) {
                pathPublication = sPath[0];
            }

             logger.info(action + " " + "osivia.cms.contentPath" + "-> "+ pathPublication);
        }
        if (attrValue != null && attrValue instanceof WindowNavigationalState) {
            WindowNavigationalState windowState = (WindowNavigationalState) attrValue;
            if (attrKey.toString().endsWith("CMSPlayerWindow")) {

                logger.info(action + " " + " REQUEST " + attrKey +  "-> " + windowState.getWindowState());

       
                if (windowState.getWindowState().equals(WindowState.NORMAL)) {
                    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
                    int l = 1;
                    for (StackTraceElement traceElement : trace) {
                        logger.info(traceElement);
                        l++;
                        if (l > 5)
                            break;
                    }
                }

            }
        }

    }

    @Override
    public Object getAttribute(Object attrKey) throws IllegalArgumentException {
        Object value = getMap().get(attrKey);
        //dump("get", attrKey, value);
        return value;
    }

    @Override
    public void setAttribute(Object attrKey, Object attrValue) throws IllegalArgumentException {
        if( attrValue == null)
            getMap().remove(attrKey);
        else
            getMap().put(attrKey, attrValue);
        //dump("set", attrKey, attrValue);
    }

}
