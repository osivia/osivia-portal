package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.page.PageProperties;

/**
 * Save region layout command.
 *
 * @author Cédric Krommenhoek
 * @see ControllerCommand
 */
public class SaveRegionLayoutCommand extends ControllerCommand {

    /** Action value. */
    public static final String ACTION = SaveRegionLayoutCommand.class.getSimpleName();


    /** Current page identifier. */
    private final String pageId;
    /** Page path. */
    private final String pagePath;
    /** Region name. */
    private final String regionName;
    /** Region layout name. */
    private final String regionLayoutName;

    /** CMS service locator. */
    private final ICMSServiceLocator cmsServiceLocator;

    /** Command info. */
    private final CommandInfo commandInfo;


    public SaveRegionLayoutCommand(String pageId, String pagePath, String regionName, String regionLayoutName) {
        super();
        this.pageId = pageId;
        this.pagePath = pagePath;
        this.regionName = regionName;
        this.regionLayoutName = regionLayoutName;

        // CMS service locator
        this.cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");

        // Command info
        this.commandInfo = new ActionCommandInfo(false);
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
        // Controller context
        ControllerContext controllerContext = this.getControllerContext();
        // Portal object container
        PortalObjectContainer portalObjectContainer = controllerContext.getController().getPortalObjectContainer();

        // Controller response
        ControllerResponse response;

        // CMS context
        CMSServiceCtx cmsContext = new CMSServiceCtx();
        cmsContext.setControllerContext(controllerContext);

        // CMS service
        ICMSService cmsService = this.cmsServiceLocator.getCMSService();

        try {
            PortalObjectId pagePortalObjectId = PortalObjectId.parse(this.pageId, PortalObjectPath.SAFEST_FORMAT);
            Page page = (Page) portalObjectContainer.getObject(pagePortalObjectId);

            if (CMSEditionPageCustomizerInterceptor.checkWritePermission(controllerContext, page)) {
                // Save region layout
                cmsService.saveCMSRegionSelectedLayout(cmsContext, this.pagePath, this.regionName, this.regionLayoutName);

                // Force editable windows reload
                PageProperties.getProperties().setRefreshingPage(true);

                response = new UpdatePageResponse(pagePortalObjectId);
            } else {
                response = new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);
            }
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return response;
    }


    /**
     * Getter for pageId.
     *
     * @return the pageId
     */
    public String getPageId() {
        return this.pageId;
    }

    /**
     * Getter for pagePath.
     * 
     * @return the pagePath
     */
    public String getPagePath() {
        return this.pagePath;
    }

    /**
     * Getter for regionName.
     * 
     * @return the regionName
     */
    public String getRegionName() {
        return this.regionName;
    }

    /**
     * Getter for regionLayoutName.
     * 
     * @return the regionLayoutName
     */
    public String getRegionLayoutName() {
        return this.regionLayoutName;
    }

}
