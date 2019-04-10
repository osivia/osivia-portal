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
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.Bundle;
import org.osivia.portal.api.internationalization.IBundleFactory;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.portalobjects.PortalObjectUtils;

/**
 * Update task command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class UpdateTaskCommand extends ControllerCommand {

    /** Command action name. */
    public static final String ACTION = "updateTask";

    /** UUID parameter name. */
    public static final String UUID_PARAMETER = "uuid";
    /** Action identifier parameter name. */
    public static final String ACTION_ID_PARAMETER = "actionId";
    /** Task variables parameter name. */
    public static final String VARIABLES_PARAMETER = "variables";

    /** Redirection path variable name. */
    public static final String REDIRECTION_PATH_VARIABLE = "command.redirection.path";
    /** Redirection fragment type variable name. */
    public static final String REDIRECTION_FRAGMENT_TYPE_VARIABLE = "command.redirection.fragment-type";
    /** Redirection page display name variable name. */
    public static final String REDIRECTION_PAGE_DISPLAY_NAME_VARIABLE = "command.redirection.page-display-name";


    /** UUID. */
    private final UUID uuid;
    /** Action identifier. */
    private final String actionId;
    /** Task variables. */
    private final Map<String, String> variables;

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
    public UpdateTaskCommand(UUID uuid, String actionId, Map<String, String> variables) {
        super();
        this.uuid = uuid;
        this.actionId = actionId;
        this.variables = variables;

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
            Map<String, String> updatedVariables = cmsService.updateTask(cmsContext, this.uuid, this.actionId, this.variables);

            // Redirection
            String redirectionUrl;
            if (MapUtils.isEmpty(updatedVariables)) {
                redirectionUrl = null;
            } else {
                try {
                    String redirectionPath = updatedVariables.get(REDIRECTION_PATH_VARIABLE);
                    String redirectionFragmentType = updatedVariables.get(REDIRECTION_FRAGMENT_TYPE_VARIABLE);
                    String redirectionPageDisplayName = updatedVariables.get(REDIRECTION_PAGE_DISPLAY_NAME_VARIABLE);

                    if (StringUtils.isNotEmpty(redirectionPath)) {
                        redirectionUrl = this.portalUrlFactory.getPermaLink(portalControllerContext, null, null, redirectionPath,
                                IPortalUrlFactory.PERM_LINK_TYPE_CMS);
                    } else if (StringUtils.isNotEmpty(redirectionFragmentType)) {
                        // Portlet instance
                        String instance = "toutatice-portail-cms-nuxeo-viewFragmentPortletInstance";
                        // Page name
                        String name = "informations";
                        // Page display name
                        String displayName = StringUtils.defaultIfBlank(redirectionPageDisplayName, bundle.getString("INFORMATIONS"));
                        // Window properties
                        Map<String, String> properties = new HashMap<String, String>();
                        properties.put(InternalConstants.PROP_WINDOW_TITLE, displayName);
                        properties.put("osivia.hideTitle", "1");
                        properties.put("osivia.fragmentTypeId", redirectionFragmentType);

                        redirectionUrl = this.portalUrlFactory.getStartPortletInNewPage(portalControllerContext, name, displayName, instance, properties, null);
                    } else {
                        redirectionUrl = null;
                    }
                } catch (PortalException e) {
                    this.log.error(e.getMessage(), e.getCause());
                    redirectionUrl = null;
                }
            }

            if (StringUtils.isEmpty(redirectionUrl)) {
                PortalObjectId pageId = PortalObjectUtils.getPageId(this.context);
                if (pageId == null) {
                    Portal portal = PortalObjectUtils.getPortal(this.context);
                    pageId = portal.getDefaultPage().getId();
                }
                response = new UpdatePageResponse(pageId);
            } else {
                response = new RedirectionResponse(redirectionUrl);
            }

            if (updatedVariables == null) {
                String message = bundle.getString("INFO_TASK_NOT_UPDATED");
                this.notificationsService.addSimpleNotification(portalControllerContext, message, NotificationsType.INFO);
            }
        } catch (CMSException e) {
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

    /**
     * Getter for actionId.
     *
     * @return the actionId
     */
    public String getActionId() {
        return this.actionId;
    }

    /**
     * Getter for variables.
     *
     * @return the variables
     */
    public Map<String, String> getVariables() {
        return this.variables;
    }

}
