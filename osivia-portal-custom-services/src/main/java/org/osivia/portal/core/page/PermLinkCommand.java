package org.osivia.portal.core.page;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.jboss.portal.common.invocation.InvocationException;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.controller.command.info.ViewCommandInfo;
import org.jboss.portal.core.controller.command.response.RedirectionResponse;
import org.jboss.portal.core.controller.command.response.SecurityErrorResponse;
import org.jboss.portal.core.controller.command.response.UnavailableResourceResponse;
import org.jboss.portal.core.impl.api.node.PageURL;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.urls.IPortalUrlFactory;
import org.osivia.portal.core.cms.CmsCommand;
import org.osivia.portal.core.dynamic.StartDynamicPageCommand;
import org.osivia.portal.core.dynamic.StopDynamicPageCommand;
import org.osivia.portal.core.portalobjects.DynamicTemplatePage;
import org.osivia.portal.core.urls.PortalUrlFactory;

/**
 * Affichage des permaliens
 */
public class PermLinkCommand extends ControllerCommand
{

    /** . */
    private static final CommandInfo info = new ViewCommandInfo();

    /** . */
    private static final Map<String,String[]> EMPTY_PARAMETERS = Collections.emptyMap();

    private String permLinkRef;
    private String templateInstanciationParentId;
    private String cmsPath;
    private String permLinkType;
    private String portalPersistentName;

    public String getPortalPersistentName() {
        return this.portalPersistentName;
    }

    public String getTemplateInstanciationParentId() {
        return this.templateInstanciationParentId;
    }

    public String getPermLinkRef() {
        return this.permLinkRef;
    }

    /** . */
    private Map<String, String> parameters;

    private boolean keepPNState = true;

    public PermLinkCommand(String permLinkRef, Map<String, String> parameters, String templateInstanciationParentId, String cmsPath, String permLinkType, String portalPersistentName)
    {
        this.permLinkRef = permLinkRef;

        //
        /*
      if (parameters == null)
      {
         throw new IllegalArgumentException("No null parameters accepted");
      }
         */

        //
        this.parameters = parameters;
        this.templateInstanciationParentId = templateInstanciationParentId;
        this.cmsPath=cmsPath;
        this.permLinkType=permLinkType;
        this.portalPersistentName = portalPersistentName;

        // Compatibility purpose
        if( this.permLinkType == null) {
            this.permLinkType = IPortalUrlFactory.PERM_LINK_TYPE_PAGE;
        }
    }

    public String getCmsPath() {
        return this.cmsPath;
    }

    public String getPermLinkType() {
        return this.permLinkType;
    }

    @Override
    public CommandInfo getInfo()
    {
        return info;
    }

    public Map<String, String> getParameters()
    {
        return this.parameters;
    }

    private Page getPage( PortalObject parent)	{


        Collection<PortalObject> childs = parent.getChildren(PortalObject.PAGE_MASK | PortalObject.WINDOW_MASK);

        for (PortalObject child : childs)	{
            String windowPermLinkName = "osivia.permaLinkRef";

            if(  IPortalUrlFactory.PERM_LINK_TYPE_RSS.equals(this.permLinkType) || IPortalUrlFactory.PERM_LINK_TYPE_RSS_PICTURE.equals(this.permLinkType)) {
                windowPermLinkName =  "osivia.rssLinkRef";
            }

            if( this.permLinkRef.equals(child.getDeclaredProperty(windowPermLinkName)))	{
                if( child instanceof Page) {
                    return (Page) child;
                }
                if( child instanceof Window) {
                    return (Page) child.getParent();
                }
            }

            if( child instanceof Page)	{
                Page page = this.getPage( child);
                if( page != null) {
                    return page;
                }
            }

        }

        return null;

    }


    private Page getPage() throws UnsupportedEncodingException, IllegalArgumentException, InvocationException, ControllerException	{


        // Search ref
        Page page = null;

        // On recherche la page contenant le permalink
        Collection<PortalObject> portals = this.getControllerContext().getController().getPortalObjectContainer().getContext().getChildren();
        for (PortalObject portal : portals)	{
            page = this.getPage(   portal);
            if( page != null) {
                break;
            }

        }

        if( page == null)	{
            // Page inexistante, on redirige vers la page par defaut du portail
            page = this.getControllerContext().getController().getPortalObjectContainer().getContext().getDefaultPortal().getDefaultPage();
        }

        if( this.templateInstanciationParentId != null){
            // Instanciation dynamique

            // Récupération page
            PortalObjectId poid = PortalObjectId.parse(this.templateInstanciationParentId, PortalObjectPath.SAFEST_FORMAT);
            PortalObject parent = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

            String templateId = URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
            String parentId = URLEncoder.encode(parent.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

            StartDynamicPageCommand cmd = new StartDynamicPageCommand(parentId,  "perm" + System.currentTimeMillis(), null,templateId,
                    new HashMap<String, String>(), this.getParameters());
            PortalObjectId pageId = ((UpdatePageResponse) this.context.execute(cmd)).getPageId();
            page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);

        }

        return page;


    }




    @Override
    public ControllerResponse execute() throws ControllerException	{
        try	{

            /*

		   if( PortalUrlFactory.PERM_LINK_TYPE_CMS.equals(permLinkType) ){


			   CmsCommand cmsCommand = new CmsCommand(null, cmsPath, parameters, IPortalUrlFactory.CONTEXTUALIZATION_PORTAL, "permlink", null, null, null, null, null, portalPersistentName);


			   ControllerResponse resp = context.execute(cmsCommand);

			   return resp;
		   }
             */


            if( PortalUrlFactory.PERM_LINK_TYPE_RSS.equals(this.permLinkType) ){

                Page page = null;
                String dynamicPagePath = null;

                if( this.cmsPath == null)	{
                    page = this.getPage();
                    if( page != null) {
                        dynamicPagePath = page.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
                    }
                }

                CmsCommand cmsCommand = new CmsCommand(dynamicPagePath, this.cmsPath, this.parameters, IPortalUrlFactory.CONTEXTUALIZATION_PORTAL, "RSS", null, null, null, this.permLinkRef, null, this.portalPersistentName);

                ControllerResponse resp = this.context.execute(cmsCommand);

                if( page instanceof DynamicTemplatePage)	{
                    String pageId = page.getId().toString(PortalObjectPath.SAFEST_FORMAT);

                    StopDynamicPageCommand stopCmd = new StopDynamicPageCommand(pageId);
                    this.context.execute(stopCmd);


                }

                if( resp instanceof SecurityErrorResponse)	{
                    // SecurityResponse redirect to login on anonymous mode
                    // A 404 is better

                    String label = " RSS ";
                    if( this.cmsPath != null) {
                        label += this.cmsPath;
                    }
                    resp = new UnavailableResourceResponse(label, false);
                }


                return resp;
            }


            if( PortalUrlFactory.PERM_LINK_TYPE_PAGE.equals(this.permLinkType) ){

                // Search ref
                Page page = null;

                // On recherche la page contenant le permalink
                Collection<PortalObject> portals = this.getControllerContext().getController().getPortalObjectContainer().getContext().getChildren();
                for (PortalObject portal : portals)	{
                    page = this.getPage(   portal);
                    if( page != null) {
                        break;
                    }

                }

                if( page == null)	{
                    // Page inexistante, on redirige vers la page par defaut du portail
                    PageURL url = new PageURL(this.getControllerContext().getController().getPortalObjectContainer().getContext().getDefaultPortal().getDefaultPage().getId(), this.getControllerContext());
                    url.setRelative(false);
                    return new RedirectionResponse(url.toString());
                }

                if( this.templateInstanciationParentId != null){
                    // Instanciation dynamique

                    // Récupération page
                    PortalObjectId poid = PortalObjectId.parse(this.templateInstanciationParentId, PortalObjectPath.SAFEST_FORMAT);
                    PortalObject parent = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);

                    String templateId = URLEncoder.encode(page.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");
                    String parentId = URLEncoder.encode(parent.getId().toString(PortalObjectPath.SAFEST_FORMAT), "UTF-8");

                    // 1.0.14 : 1 seul onglet par permalien
                    StartDynamicPageCommand cmd = new StartDynamicPageCommand(parentId,  "perm_" + this.permLinkRef, null,templateId,
                            new HashMap<String, String>(), this.getParameters());
                    return this.context.execute(cmd);
                }


                // Rechargement pour récuperer les fenêtres dynamiques
                // ????
                page = (Page) this.getControllerContext().getController().getPortalObjectContainer().getObject(page.getId());


                if (this.parameters.size() > 0)
                {
                    NavigationalStateContext nsContext = (NavigationalStateContext)this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);

                    //
                    String pageId = page.getId().toString();

                    //
                    PageNavigationalState previousPNS = nsContext.getPageNavigationalState(pageId);

                    //
                    Map<QName, String[]> state = new HashMap<QName, String[]>();

                    if (this.keepPNState)
                    {
                        // Clone the previous state if needed
                        if (previousPNS != null)
                        {
                            state.putAll(previousPNS.getParameters());
                        }
                    }

                    //
                    for (Map.Entry<String, String> entry : this.parameters.entrySet())
                    {

                        state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[] {entry.getValue()});
                    }

                    //
                    nsContext.setPageNavigationalState(pageId, new PageNavigationalState(state));
                }

                // Réinitialisation des états des fenêtres
                Collection windows = page.getChildren(PortalObject.WINDOW_MASK);
                PageCustomizerInterceptor.initPageState(page, this.getControllerContext());

                //
                return new UpdatePageResponse(page.getId());
            }
        } catch( Exception e){
            if( e instanceof ControllerException) {
                throw (ControllerException) e;
            } else {
                throw new ControllerException(e);
            }

        }

        return null;
    }
}
