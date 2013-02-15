package org.osivia.portal.administration.ejb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.portlet.PortletContext;
import javax.servlet.ServletException;
import javax.transaction.UserTransaction;

import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.deploiement.IParametresPortailDeploymentManager;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.richfaces.model.UploadItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


@Name("importBean")
@Scope(ScopeType.EVENT)
public class ImportBean   {

	@In(create=true, value="errorBean")
	private ErrorBean errorBean;
	
	
	IParametresPortailDeploymentManager deployer;
	
	public void listener(UploadEvent event) throws IOException {
		

		
		
		UploadItem item = event.getUploadItem();
		
		File tempFile = null;

		try {

			System.out.println("upload: ");
			
			/* Get File Data */
			
			
			if (!item.isTempFile()) {
				/* Create file */
				
				tempFile = File.createTempFile("upload", ".xml");
				FileOutputStream fos = new FileOutputStream(tempFile);
				fos.write(item.getData());
				fos.close();
				
				PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext()
						.getContext();
				deployer = (IParametresPortailDeploymentManager) ctx.getAttribute("PiaConfigDeployer");
				deployer.chargerParametres(tempFile, MBeanServerLocator.locateJBoss());
				

				
			}

		} catch (Exception e) {
			errorBean.setMsg("Opération impossible : "+ e.getMessage());
		} finally	{
			tempFile.delete();
		}
	}
	
	
}
