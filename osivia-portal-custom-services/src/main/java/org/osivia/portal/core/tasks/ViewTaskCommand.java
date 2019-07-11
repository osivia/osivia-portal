package org.osivia.portal.core.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.ErrorResponse;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Update task command.
 *
 * @author Jean-Sébastien Steux
 * @see ControllerCommand
 */
public class ViewTaskCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "viewTask";

    /** UUID parameter name. */
    public static final String UUID_PARAMETER = "uuid";




    /** UUID. */
    private final UUID uuid;


    /** Log. */
    private final Log log;
    /** Command info. */
    private final CommandInfo commandInfo;

    /** Portal URL factory. */
    private final IPortalUrlFactory portalUrlFactory;
    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;
    /** Internationalization bundle factory. */
    private final IBundleFactory bundleFactory;
    /** Notifications service. */
    private final INotificationsService notificationsService;


    /**
     * Constructor.
     *
     * @param uuid UUID
     * @param actionId action identifier
     * @param variables task variables
     * @param redirectionUrl redirection URL
     */
    public ViewTaskCommand(UUID uuid) {
        super();
        this.uuid = uuid;


        // Log
        this.log = LogFactory.getLog(this.getClass());
        // Command info
        this.commandInfo = new ActionCommandInfo(false);

        // Portal URL factory
        this.portalUrlFactory = Locator.findMBean(IPortalUrlFactory.class, IPortalUrlFactory.MBEAN_NAME);
        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, ICMSServiceLocator.MBEAN_NAME);
        // Internationalization bundle factory
        IInternationalizationService internationalizationService = Locator.findMBean(IInternationalizationService.class,
                IInternationalizationService.MBEAN_NAME);
        this.bundleFactory = internationalizationService.getBundleFactory(this.getClass().getClassLoader());
        // Notifications service
        this.notificationsService = Locator.findMBean(INotificationsService.class, INotificationsService.MBEAN_NAME);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public CommandInfo getInfo() {
        return this.commandInfo;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerResponse execute() throws ControllerException {
        // Portal controller context
        PortalControllerContext portalControllerContext = new PortalControllerContext(this.context);
        // HTTP servlet request
        HttpServletRequest request = portalControllerContext.getHttpServletRequest();
        // Internationalization bundle
        Bundle bundle = this.bundleFactory.getBundle(request.getLocale());

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();
        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(this.context);

        // Response
        ControllerResponse response;

        try {
            
            CMSItem task = cmsService.getTask(cmsContext,  uuid);
            
            if( task != null)   {
                String redirectionUrl = this.portalUrlFactory.getPermaLink(portalControllerContext, null, null, task.getPath(),
                        IPortalUrlFactory.PERM_LINK_TYPE_CMS);    
                response = new RedirectionResponse(redirectionUrl);
            }
            else 
                throw new CMSException(CMSException.ERROR_NOTFOUND);

   
        } catch(PortalException pe) {
            throw new ControllerException( pe);
        }
        
        
        catch (CMSException e) {
            response = new ErrorResponse(e, true);
        }

        return response;
    }


    /**
     * Getter for uuid.
     *
     * @return the uuid
     */
    public UUID getUuid() {
        return this.uuid;
    }



}
