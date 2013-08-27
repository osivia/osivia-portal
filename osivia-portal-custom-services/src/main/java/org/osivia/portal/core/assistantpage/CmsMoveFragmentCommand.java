package org.osivia.portal.core.assistantpage;

import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSService;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.error.UserNotificationException;
import org.osivia.portal.core.page.UserNotification;

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

    /** the id of the window moved */
    private String refUri;


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
        cmsCtx.setControllerContext(getControllerContext());

        try {
            
            
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
           
            if (!CMSEditionPageCustomizerInterceptor.checkWritePermission(context, pagePath))
                return new SecurityErrorResponse(SecurityErrorResponse.NOT_AUTHORIZED, false);

            try {
                getCMSService().moveFragment(cmsCtx, pagePath, fromRegion, fromPos, toRegion, toPos, refUri);
            } catch( CMSException e)    {
                throw new UserNotificationException(new UserNotification(true, "Problème dans le copie/coller"));
            }
            
            
        } catch (CMSException e) {
            throw new ControllerException(e);
        } catch (Exception e) {
            if( ! (e instanceof ControllerException))
                throw new ControllerException(e);
            else throw (ControllerException) e;
        }
        
        PortalObjectId portalObjectId = (PortalObjectId) getControllerContext().getAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.currentPageId");
        return new UpdatePageResponse(portalObjectId);

    }

    @Override
    public CommandInfo getInfo() {
        // TODO Auto-generated method stub
        return null;
    }

}
