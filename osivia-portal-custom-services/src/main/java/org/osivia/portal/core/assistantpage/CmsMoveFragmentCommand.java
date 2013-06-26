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

    /** the identifier of the region from the fragment is moved */
    private String fromRegion;

    /** position in the fromRegion (from 0 (top) to N-1 ( number of current fgts in the region) */
    private Integer fromPos;

    /** the identifier of the region where the fragment is dropped */
    private String toRegion;
    
    /** the new position of the fgt in the toRegion */
    private Integer toPos;


    public CmsMoveFragmentCommand(String pagePath, String fromRegion, Integer fromPosInt, String toRegion, Integer toPosInt) {
        this.pagePath = pagePath;

        this.fromRegion = fromRegion.replaceAll("^region_", "");
        this.toRegion = toRegion.replaceAll("^region_", "");
        this.fromPos = fromPosInt;
        this.toPos = toPosInt;
    }


    @Override
    public ControllerResponse execute() throws ControllerException {

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext(getControllerContext());

        try {
            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(context, pagePath))
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);


            getCMSService().moveFragment(cmsCtx, pagePath, fromRegion, fromPos, toRegion, toPos);
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
