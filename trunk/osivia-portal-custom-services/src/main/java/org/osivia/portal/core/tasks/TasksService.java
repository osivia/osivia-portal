package org.osivia.portal.core.tasks;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.tasks.ITasksService;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.context.ControllerContextAdapter;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.page.PortalURLImpl;

/**
 * Tasks service implementation.
 * 
 * @author Cédric Krommenhoek
 * @see ITasksService
 */
public class TasksService implements ITasksService {

    /** Tasks count attribute name. */
    private static final String COUNT_ATTRIBUTE = "osivia.tasks.count";
    /** Tasks count timestamp attribute name. */
    private static final String TIMESTAMP_ATTRIBUTE = "osivia.tasks.timestamp";


    /** CMS service locator. */
    private ICMSServiceLocator cmsServiceLocator;


    /**
     * Constructor.
     */
    public TasksService() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public int getTasksCount(PortalControllerContext portalControllerContext) throws PortalException {
        // User principal
        Principal principal = portalControllerContext.getHttpServletRequest().getUserPrincipal();

        // Tasks count
        int count;

        if (principal == null) {
            count = 0;
        } else {
            // Controller context
            ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

            // Saved count attribute
            Object countAttribute = controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, COUNT_ATTRIBUTE);

            // Refresh indicator
            boolean refresh;

            if (countAttribute == null) {
                refresh = true;
            } else {
                // Timestamps
                long currentTimestamp = System.currentTimeMillis();
                long savedTimestamp;
                Object savedTimestampAttribute = controllerContext.getAttribute(Scope.PRINCIPAL_SCOPE, TIMESTAMP_ATTRIBUTE);
                if ((savedTimestampAttribute != null) && (savedTimestampAttribute instanceof Long)) {
                    savedTimestamp = (Long) savedTimestampAttribute;
                } else {
                    savedTimestamp = 0;
                }

                // Page refresh indicator
                boolean pageRefresh = PageProperties.getProperties().isRefreshingPage();

                if (pageRefresh) {
                    refresh = ((currentTimestamp - savedTimestamp) > TimeUnit.SECONDS.toMillis(1));
                } else {
                    refresh = ((currentTimestamp - savedTimestamp) > TimeUnit.MINUTES.toMillis(3));
                }
            }

            if (refresh) {
                // CMS service
                ICMSService cmsService = this.cmsServiceLocator.getCMSService();
                // CMS context
                CMSServiceCtx cmsContext = new CMSServiceCtx();
                cmsContext.setPortalControllerContext(portalControllerContext);

                try {
                    count = cmsService.getTasksCount(cmsContext, principal.getName());

                    controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, COUNT_ATTRIBUTE, count);
                    controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, TIMESTAMP_ATTRIBUTE, System.currentTimeMillis());
                } catch (CMSException e) {
                    throw new PortalException(e);
                }
            } else {
                count = (Integer) countAttribute;
            }
        }

        return count;
    }


    /**
     * {@inheritDoc}
     */
    public void resetTasksCount(PortalControllerContext portalControllerContext) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        controllerContext.removeAttribute(Scope.PRINCIPAL_SCOPE, COUNT_ATTRIBUTE);
        controllerContext.removeAttribute(Scope.PRINCIPAL_SCOPE, TIMESTAMP_ATTRIBUTE);
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandUrl(PortalControllerContext portalControllerContext, UUID uuid, String actionId, String redirectionUrl) throws PortalException {
        // Controller context
        ControllerContext controllerContext = ControllerContextAdapter.getControllerContext(portalControllerContext);

        // Command
        ControllerCommand command = new UpdateTaskCommand(uuid, actionId, null, redirectionUrl);

        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, true, null);
        
        return portalUrl.toString();
    }


    /**
     * Setter for cmsServiceLocator.
     * 
     * @param cmsServiceLocator the cmsServiceLocator to set
     */
    public void setCmsServiceLocator(ICMSServiceLocator cmsServiceLocator) {
        this.cmsServiceLocator = cmsServiceLocator;
    }

}
