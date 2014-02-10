package org.osivia.portal.core.cms;

import java.util.Map;

import org.jboss.portal.core.model.portal.Window;


public class CMSPlayHandlerUtils {
    public static void saveHandlerProperties( CMSServiceCtx handlerCtx, Map<String, String> windowProperties)  {
        windowProperties.put("osivia.handler.scope",handlerCtx.getScope());
        windowProperties.put("osivia.handler.pageID", handlerCtx.getPageId());
        windowProperties.put("osivia.handler.displayLiveVersion", handlerCtx.getDisplayLiveVersion());
        windowProperties.put("osivia.handler.hideMetadatas", handlerCtx.getHideMetaDatas());
        windowProperties.put("osivia.handler.displayContext", handlerCtx.getDisplayContext());
        windowProperties.put("osivia.handler.contextualizationBasePath", handlerCtx.getContextualizationBasePath());
    }
    
    
    public static void restoreHandlerProperties( Window window, CMSServiceCtx handlerCtx)  {
        handlerCtx.setScope(window.getDeclaredProperty("osivia.handler.scope"));
        handlerCtx.setPageId(window.getDeclaredProperty("osivia.handler.pageID"));
        handlerCtx.setDisplayLiveVersion(window.getDeclaredProperty("osivia.handler.displayLiveVersion"));
        handlerCtx.setHideMetaDatas(window.getDeclaredProperty("osivia.handler.hideMetadatas"));
        handlerCtx.setDisplayContext(window.getDeclaredProperty("osivia.handler.displayContext"));
        handlerCtx.setContextualizationBasePath(window.getDeclaredProperty("osivia.handler.contextualizationBasePath"));

    }
        

}
