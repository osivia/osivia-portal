package org.osivia.portal.core.assistantpage;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.notifications.NotificationsType;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSPublicationInfos;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.internationalization.InternationalizationUtils;
import org.osivia.portal.core.notifications.NotificationsUtils;
import org.osivia.portal.core.security.CmsPermissionHelper;

/**
 * Command used to switch between cms modes
 * 
 */
public class ChangeCMSEditionModeCommand extends ControllerCommand {

    /**
     * Error if page is not published and user wants the online version.
     * */
    private static final String ERROR_MESSAGE_CHANGE_CMS_EDITION_MODE_COMMAND = "ERROR_MESSAGE_CHANGE_CMS_EDITION_MODE_COMMAND";

    /**
     * INFO.
     */
    private static final CommandInfo INFO = new ActionCommandInfo(false);

    /**
     * Logger.
     */
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);

    ICMSService cmsService;

    private static ICMSServiceLocator cmsServiceLocator;


    /**
     * page path used by the ECM.
     * */
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


    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }



	public CommandInfo getInfo() {
        return INFO;
	}

	private String pageId;

    /** Display the given version, ex : preview, online, ... */
    private String version;

    /** Display the widgets in the cms windows */
    private String editionMode;

    public String getVersion() {
        return this.version;
	}

    public String getEditionMode() {
        return this.editionMode;
    }


	public String getPageId() {
		return this.pageId;
	}

    public ChangeCMSEditionModeCommand(String pageId, String pagePath, String version, String editionMode) {


		this.pageId = pageId;
        this.pagePath = pagePath;
        this.version = version;
        this.editionMode = editionMode;
	}


    public ControllerResponse execute() throws ControllerException {

        // Récupération page
        PortalObjectId poid = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
        PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

        PortalControllerContext ctx = new PortalControllerContext(getControllerContext());
        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext(getControllerContext());

        try {
            // Try to get the publication info
            CMSPublicationInfos publicationInfos = getCMSService().getPublicationInfos(cmsCtx, pagePath);
            boolean published = publicationInfos.isPublished();

            // If page is not published, user can not access the online version
            if (!published && version.equals(CmsPermissionHelper.CMS_VERSION_ONLINE)) {

                Locale locale = getControllerContext().getServerInvocation().getRequest().getLocale();
                String message = InternationalizationUtils.getInternationalizationService().getString(ERROR_MESSAGE_CHANGE_CMS_EDITION_MODE_COMMAND, locale);

                // Show a notification
                NotificationsUtils.getNotificationsService().addSimpleNotification(ctx, message, NotificationsType.ERROR);

                return new UpdatePageResponse(page.getId());
            }

        } catch (CMSException e) {
            throw new ControllerException(e);
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        CmsPermissionHelper.changeCmsMode(getControllerContext(), pagePath, version, editionMode);

		return new UpdatePageResponse(page.getId());

	}

}
