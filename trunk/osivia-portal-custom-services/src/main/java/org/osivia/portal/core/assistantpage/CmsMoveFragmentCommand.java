package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.error.UserNotificationsException;

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
    private final String pagePath;

    /** the identifier of the region from the fragment is moved */
    private final String fromRegion;

    /** position in the fromRegion (from 0 (top) to N-1 ( number of current fgts in the region) */
    private final Integer fromPos;

    /** the identifier of the region where the fragment is dropped */
    private final String toRegion;

    /** the new position of the fgt in the toRegion */
    private final Integer toPos;

    /** the id of the window moved */
    private final String refUri;


    public CmsMoveFragmentCommand(String pagePath, String fromRegion, Integer fromPosInt, String toRegion, Integer toPosInt, String refUri) {
        this.pagePath = pagePath;

        this.fromRegion = fromRegion.replaceAll("^region_", "");
        this.toRegion = toRegion.replaceAll("^region_", "");
        this.fromPos = fromPosInt;
        this.toPos = toPosInt;
        this.refUri = refUri;
    }


    @Override
    public ControllerResponse execute() throws ControllerException {

        CMSServiceCtx cmsCtx = new CMSServiceCtx();
        cmsCtx.setControllerContext(this.getControllerContext());

        try {
            // Test for notifications


            /*
            String test =  (String) getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE,"test");

            if( test == null)   {
                UserNotification okNotif = new UserNotification(false, "OK");
                getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, InternalConstants.ATTR_USER_NOTIFICATION , okNotif);

                getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE,"test", "ok");
            }   else  if( "ok".equals(test))  {
                UserNotification okNotif = new UserNotification(true, "KO");
                getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, InternalConstants.ATTR_USER_NOTIFICATION , okNotif);

                getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE,"test","ko");
            }   else
                getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE,"test",null);
                */

            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(this.context, this.pagePath)) {
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);
            }

            try {
                getCMSService().moveFragment(cmsCtx, this.pagePath, this.fromRegion, this.fromPos, this.toRegion, this.toPos, this.refUri);
            } catch( CMSException e)    {
                throw new UserNotificationsException("Problème dans le déplacement");
            }


        } catch (CMSException e) {
            throw new ControllerException(e);
        } catch (Exception e) {
            if( ! (e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw (ControllerException) e;
            }
        }

        PortalObjectId portalObjectId = (PortalObjectId) this.getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
        return new UpdatePageResponse(portalObjectId);

    }

    @Override
    public CommandInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

}
