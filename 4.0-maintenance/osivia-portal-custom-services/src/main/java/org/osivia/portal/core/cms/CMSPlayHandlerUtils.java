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
 *
 */
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
