package org.osivia.portal.core.sharing.link;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.command.mapper.URLFactoryDelegate;
import org.jboss.portal.server.AbstractServerURL;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.portal.server.ServerURL;

/**
 * Link sharing URL factory.
 * 
 * @author Cédric Krommenhoek
 * @see URLFactoryDelegate
 */
public class LinkSharingUrlFactory extends URLFactoryDelegate {

    /** Path. */
    private String path;


    /**
     * Constructor.
     */
    public LinkSharingUrlFactory() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ServerURL doMapping(ControllerContext controllerContext, ServerInvocation invocation, ControllerCommand controllerCommand) {
        ServerURL serverUrl;
        
        if ((controllerCommand != null) && (controllerCommand instanceof LinkSharingCommand)) {
            serverUrl = new AbstractServerURL();
            serverUrl.setPortalRequestPath(this.path);

            // Link sharing command
            LinkSharingCommand command = (LinkSharingCommand) controllerCommand;

            // Link identifier
            String id = command.getId();
            if (StringUtils.isNotEmpty(id)) {
                String encoded;
                try {
                    encoded = URLEncoder.encode(id, CharEncoding.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    encoded = null;
                }

                serverUrl.setParameterValue("id", encoded);
            }
        } else {
            serverUrl = null;
        }

        return serverUrl;
    }


    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter for path.
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
