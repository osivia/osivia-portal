/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.dynamic;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerException;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.portal.core.controller.command.info.ActionCommandInfo;
import org.jboss.portal.core.controller.command.info.CommandInfo;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.command.response.UpdatePageResponse;
import org.jboss.portal.core.model.portal.navstate.PageNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.assistantpage.AssistantCommand;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.pagemarker.PageMarkerInfo;
import org.osivia.portal.core.pagemarker.PageMarkerUtils;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;


public class StartDynamicPageCommand extends DynamicCommand {

	private static final CommandInfo info = new ActionCommandInfo(false);
	protected static final Log logger = LogFactory.getLog(AssistantCommand.class);
	

	public CommandInfo getInfo() {
		return info;
	}
	String parentId ;
	String templateId ;
	String businessName;
	Map displayNames;
	Map<String, String> props;
	Map<String, String> params;

    Map<String, String> cmsParams;

	
    public Map<String, String> getCmsParams() {
        return cmsParams;
    }

    
    public void setCmsParams(Map<String, String> cmsParams) {
        this.cmsParams = cmsParams;
    }

    public StartDynamicPageCommand() {
	}

	public StartDynamicPageCommand(String parentId,  String businessName,  Map displayNames, String templateId,
			Map<String, String> props, Map<String, String> params) {
		this.parentId = parentId;
		this.businessName = businessName;
		this.templateId = templateId;

		this.displayNames = displayNames;
		this.props = props;
		this.params = params;
	}
	

	public ControllerResponse execute() throws ControllerException {

		try {
		    

			// Récupération page
			PortalObjectId poid = PortalObjectId.parse(this.parentId, PortalObjectPath.SAFEST_FORMAT);
			PortalObject parent = this.getControllerContext().getController().getPortalObjectContainer().getObject(poid);
	

			PortalObjectId potemplateid = PortalObjectId.parse(this.templateId, PortalObjectPath.SAFEST_FORMAT);
			String potemplatepath = potemplateid.toString( PortalObjectPath.CANONICAL_FORMAT);
			
             
			 String cmsPath = props.get("osivia.cms.basePath");

			

			 

			String pageName = RestorablePageUtils.createRestorableName(this.getControllerContext(), this.businessName, templateId, cmsPath, displayNames, props, params, cmsParams);
		    String pageUniqueName = businessName;
		
			

 
            PortalObjectId pageId = new PortalObjectId("", new PortalObjectPath(parent.getId().getPath().toString()
                    .concat("/").concat(pageName), PortalObjectPath.CANONICAL_FORMAT));
			

	         PortalObject currentPortal = parent;
             while (! (currentPortal instanceof Portal))    {
                 currentPortal = currentPortal.getParent();
             }

			// templates defined in others portals may be redefined localy
            String templatePortal =  potemplateid.getPath().getName(0);
            if( ! templatePortal.equals(currentPortal.getName()))  {

			    // Build local path
			    String localPath = "/"+currentPortal.getName() + potemplatepath.substring(templatePortal.length() + 1);
			    PortalObjectPath localTemplatePath = new PortalObjectPath(localPath, PortalObjectPath.CANONICAL_FORMAT);

			    PortalObjectId polocaltemplateId = new PortalObjectId("", localTemplatePath);

			    // If exists in current portal, get it
			    if( this.getControllerContext().getController().getPortalObjectContainer().getObject(polocaltemplateId) != null) {
                    potemplateid = polocaltemplateId;
                }
			}


			IDynamicObjectContainer dynamicCOntainer = Locator.findMBean(IDynamicObjectContainer.class,
			"osivia:service=DynamicPortalObjectContainer");


			Map<String, String> properties = new HashMap<String, String>();

			for (String dynaKey : this.props.keySet()) {
				properties.put(dynaKey, this.props.get(dynaKey));

			}


			// Mémorisation de la page avant l'appel
			PageMarkerInfo markerInfo = PageMarkerUtils.getLastPageState( this.getControllerContext());

			if( markerInfo != null)	{
			    // Pas de retour si ouverture depuis un popup
       		   if( markerInfo.getPopupMode() == null) {
                properties.put("osivia.dynamic.close_page_path",markerInfo.getPageId().toString());
            }
			}



			DynamicPageBean pageBean = new DynamicPageBean(parent, pageName, pageUniqueName, this.displayNames, potemplateid, properties);







			dynamicCOntainer.addDynamicPage(pageBean);

            PortalObject page = this.getControllerContext().getController().getPortalObjectContainer().getObject(pageId);



			// Maj des paramètres publics de la page
	         NavigationalStateContext nsContext = (NavigationalStateContext)this.context.getAttributeResolver(ControllerCommand.NAVIGATIONAL_STATE_SCOPE);
 	         Map<QName, String[]> state = new HashMap<QName, String[]>();
	         for (Map.Entry<String, String> entry : this.params.entrySet())
	         {
	            state.put(new QName(XMLConstants.DEFAULT_NS_PREFIX, entry.getKey()), new String[] {entry.getValue()});
	         }
	         nsContext.setPageNavigationalState(pageId.toString(), new PageNavigationalState(state));

	         /* Suppression des anciens renders params */

				for (PortalObject po : page.getChildren(PortalObject.WINDOW_MASK)) {
					this.getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE, po.getId().toString());
					
	                   // et des anciens caches
                    // Suppression du cache
                    getControllerContext().removeAttribute(ControllerCommand.PRINCIPAL_SCOPE,   "cached_markup." + po.getId().toString());

				}

				// Maj du breadcrumb
				this.getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, 	"breadcrumb", null);


		        // rafaichir la bandeau
	            getControllerContext().setAttribute(ControllerCommand.PRINCIPAL_SCOPE, "osivia.tabbedNavRefresh", "1"); 

				
			return new UpdatePageResponse(pageId);



		} catch (Exception e) {
			if( ! (e instanceof ControllerException)) {
                throw new ControllerException(e);
            } else {
                throw ((ControllerException) e);
            }
		}

	}

}
