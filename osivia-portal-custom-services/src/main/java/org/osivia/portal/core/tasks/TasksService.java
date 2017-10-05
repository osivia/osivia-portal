package org.osivia.portal.core.tasks;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.api.PortalURL;
import org.jboss.portal.common.invocation.Scope;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.cms.EcmDocument;
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
    public List<EcmDocument> getTasks(PortalControllerContext portalControllerContext) throws PortalException {
        // User principal
        Principal principal = portalControllerContext.getHttpServletRequest().getUserPrincipal();

        // Tasks
        List<EcmDocument> tasks;

        if (principal == null) {
            tasks = new ArrayList<>(0);
        } else {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);

            try {
                tasks = cmsService.getTasks(cmsContext, principal.getName());
            } catch (CMSException e) {
                throw new PortalException(e);
            }
        }

        return tasks;
    }


    /**
     * {@inheritDoc}
     */
    public EcmDocument getTask(PortalControllerContext portalControllerContext, String path) throws PortalException {
        // User principal
        Principal principal = portalControllerContext.getHttpServletRequest().getUserPrincipal();

        // Task
        EcmDocument task;

        if (principal == null) {
            task = null;
        } else {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);

            try {
                task = cmsService.getTask(cmsContext, principal.getName(), path, null);
            } catch (CMSException e) {
                throw new PortalException(e);
            }
        }

        return task;
    }


    /**
     * {@inheritDoc}
     */
    public EcmDocument getTask(PortalControllerContext portalControllerContext, UUID uuid) throws PortalException {
        // User principal
        Principal principal = portalControllerContext.getHttpServletRequest().getUserPrincipal();

        // Task
        EcmDocument task;

        if (principal == null) {
            task = null;
        } else {
            // CMS service
            ICMSService cmsService = this.cmsServiceLocator.getCMSService();
            // CMS context
            CMSServiceCtx cmsContext = new CMSServiceCtx();
            cmsContext.setPortalControllerContext(portalControllerContext);

            try {
                task = cmsService.getTask(cmsContext, principal.getName(), null, uuid);
            } catch (CMSException e) {
                throw new PortalException(e);
            }
        }

        return task;
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
                // Tasks
                List<EcmDocument> tasks = this.getTasks(portalControllerContext);
                // Count
                count = tasks.size();

                controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, COUNT_ATTRIBUTE, count);
                controllerContext.setAttribute(Scope.PRINCIPAL_SCOPE, TIMESTAMP_ATTRIBUTE, System.currentTimeMillis());
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

        // Customized host property
        String host = System.getProperty(HOST_PROPERTY);

        // Command
        ControllerCommand command = new UpdateTaskCommand(uuid, actionId, null, redirectionUrl);

        // Portal URL
        PortalURL portalUrl = new PortalURLImpl(command, controllerContext, true, null);

        // Command URL
        String url;

        if (StringUtils.isEmpty(host)) {
            url = portalUrl.toString();
        } else {
            // Relative portal URL
            portalUrl.setRelative(true);

            url = host + portalUrl.toString();
        }
        
        return url;
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
