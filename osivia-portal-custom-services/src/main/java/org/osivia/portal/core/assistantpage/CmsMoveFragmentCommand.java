package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;

/**
 * CMS command used when a fragment is dragged and dropped in other place in the page
 */
public class CmsMoveFragmentCommand extends ControllerCommand {


    ICMSService cmsService;

    private static ICMSServiceLocator cmsServiceLocator;

    public static ICMSService getCMSService() throws Exception {

        if (cmsServiceLocator == null) {
            cmsServiceLocator = Locator.findMBean(ICMSServiceLocator.class, "osivia:service=CmsServiceLocator");
        }

        return cmsServiceLocator.getCMSService();

    }

    /** the path of the page */
    private String pagePath;

    /** an unique identifier of the fragment to move in the current page */
    private String refURI;

    /** an unique identifier of an object which recieve the fragment (region or window) */
    private String toURI;

    /** 'true' if fgt is dropped below the destination, 'false' if above */
    private boolean belowFragment;

    /** 'true' if the fgt is moved in an empty region, 'false' if it is dragged between existing fragments */
    private boolean dropOnEmptyRegion = false;

    public CmsMoveFragmentCommand(String pagePath, String refURI, String toURI, boolean belowFragment) {
        this.pagePath = pagePath;

        // Change window ID to ECM ID (remove prefix
        this.refURI = refURI.replaceAll("^window_", "");

        if (toURI.contains("region_")) {
            this.toURI = toURI.replaceAll("^region_", "");
            this.dropOnEmptyRegion = true;
        } else {
            this.toURI = toURI.replaceAll("^window_", "");
        }

        this.belowFragment = belowFragment;
    }


    @Override
    public ControllerResponse execute() throws ControllerException {

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext(getControllerContext());

        try {
            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(context, pagePath))
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);


            getCMSService().moveFragment(cmsCtx, pagePath, refURI, toURI, belowFragment, dropOnEmptyRegion);
        } catch (CMSException e) {
            throw new ControllerException(e);
        } catch (Exception e) {
            throw new ControllerException(e);
        }

        return null;
    }

    @Override
    public CommandInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

}
