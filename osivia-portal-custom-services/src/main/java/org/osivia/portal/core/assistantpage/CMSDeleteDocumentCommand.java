package org.osivia.portal.core.assistantpage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.internationalization.IInternationalizationService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.INotificationsService;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;

/**
 * CMS command used to remove a document
 */
public class CMSDeleteDocumentCommand extends ControllerCommand {

    private static final String SUCCESS_MESSAGE_DELETE = "SUCCESS_MESSAGE_DELETE";

    ICMSService cmsService;

    private static INotificationsService notifService = NotificationsUtils.getNotificationsService();
    private static IInternationalizationService itlzService = InternationalizationUtils.getInternationalizationService();

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    private static final CommandInfo info = new ActionCommandInfo(false);
    protected static final Log logger = LogFactory.getLog(CMSDeleteDocumentCommand.class);

    /** page ID used for reload after delete */
    private String pageId;

    /** page path used by the ECM */
    private String pagePath;

    /**
     * @return the pagePath
     */
    public String getPagePath() {
        return pagePath;
    }


    /**
     * @param pagePath the pagePath to set
     */
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }


    /**
     * @return the pageId
     */
    public String getPageId() {
        return pageId;
    }

    public CMSDeleteDocumentCommand(String pageId, String pagePath) {
        this.pageId = pageId;
        this.pagePath = pagePath;
    }

    public CommandInfo getInfo() {
        return info;
    }


    public ControllerResponse execute() throws ControllerException {


        try {
            // Contrôle droits

            PortalObjectId poid = PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT);
            PortalObject page = getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            CMSServiceCtx cmsCtx = new CMSServiceCtx();
            cmsCtx.setControllerContext(getControllerContext());


            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(context, (Page) page))
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);


            PortalControllerContext pcc = new PortalControllerContext(getControllerContext());


            getCMSService().deleteDocument(cmsCtx, pagePath);

            String success = itlzService.getString(SUCCESS_MESSAGE_DELETE, getControllerContext().getServerInvocation().getRequest().getLocale());
            notifService.addSimpleNotification(pcc, success, NotificationsType.SUCCESS);


            // force reload page(editables windows)
            getControllerContext().getServerInvocation().setAttribute(ControllerCommand.REQUEST_SCOPE, "osivia.editableWindows." + "." + pagePath, null);

            // Force reload of the navigation tree
            String navigationScope = page.getProperty("osivia.cms.navigationScope");
            String basePath = page.getProperty("osivia.cms.basePath");

            CMSServiceCtx cmxCtx = new CMSServiceCtx();
            cmxCtx.setControllerContext(getControllerContext());
            cmxCtx.setScope(navigationScope);
            cmxCtx.setForceReload(true);

            getCMSService().getPortalNavigationItem(cmxCtx, basePath, basePath);


            return new UpdatePageResponse(poid);


        } catch (Exception e) {
            if (!(e instanceof ControllerException))
                throw new ControllerException(e);
            else
                throw (ControllerException) e;
        }
    }

}
