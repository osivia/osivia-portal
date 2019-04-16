package org.osivia.portal.core.sharing.link;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.AbstractCommandFactory;
import org.jboss.portal.server.ServerInvocation;

/**
 * Link sharing command factory service.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractCommandFactory
 */
public class LinkSharingCommandFactoryService extends AbstractCommandFactory {

    /**
     * Constructor.
     */
    public LinkSharingCommandFactoryService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerCommand doMapping(ControllerContext controllerContext, ServerInvocation invocation, String host, String contextPath, String requestPath) {
        LinkSharingCommand command = new LinkSharingCommand();

        // Parameter map
        ParameterMap parameterMap = controllerContext.getServerInvocation().getServerContext().getQueryParameterMap();

        if (parameterMap != null) {
            // Link identifier
            String id = parameterMap.getValue("id");
            if (StringUtils.isNotEmpty(id)) {
                String decoded;
                try {
                    decoded = URLDecoder.decode(id, CharEncoding.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    decoded = null;
                }

                command.setId(decoded);
            }
        }

        return command;
    }

}
