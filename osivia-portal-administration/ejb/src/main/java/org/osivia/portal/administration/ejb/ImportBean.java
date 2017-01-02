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
package org.osivia.portal.administration.ejb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.deploiement.IParametresPortailDeploymentManager;

import org.osivia.portal.core.imports.ImportCheckerDatas;
import org.osivia.portal.core.imports.ImportCheckerNode;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;



/**
 * Import bean.
 *
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("importBean")
@Scope(ScopeType.PAGE)
public class ImportBean extends AbstractAdministrationBean {
    
    private static Log logger = LogFactory.getLog(ImportBean.class);

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Initial uploads available. */
    private static final int INITIAL_UPLOADS_AVAILABLE = 5;

    /** Portal deployment manager. */
    private IParametresPortailDeploymentManager deployer;
    /** Uploads available. */
    private int uploadsAvailable = INITIAL_UPLOADS_AVAILABLE;
    /** Auto upload. */
    private final boolean autoUpload = false;
    /** Use flash. */
    private boolean useFlash = false;
   /** Portal object container. */
    private PortalObjectContainer portalObjectContainer;    
    
    protected ICacheService cacheService;
    

    private String portalObjectPath = null;

    private long lastCheckTS = 0L;
    

    public String getPortalObjectPath() {
        return portalObjectPath;
    }

    public boolean isChecking() {
        return cacheService.isChecking();
    }

    
    public void setPortalObjectPath(String portalObjectPath) {
        this.portalObjectPath = portalObjectPath;
    }



    /**
     * Default constructor.
     */
    public ImportBean() {
        super();
    }

    

    /**
     * {@inheritDoc}
     */
    @Create
    @Override
    public void init() {
        super.init();
        PortletContext context = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        this.cacheService = (ICacheService) Locator.findMBean(ICacheService.class, "osivia:service=Cache");
        this.portalObjectContainer = (PortalObjectContainer) context.getAttribute(AdministrationConstants.PORTAL_OBJECT_CONTAINER_NAME);
        
    }
    

    
    
    
    /**
     * Refresh the cluster state
     *
     * @return the cluster msg
     */
    
    public String getClusterMsg() {
        
        if (lastCheckTS > 0) {

            ImportCheckerDatas importData = cacheService.getImportCheckerDatas();
            
            if (importData != null) {

                String msg = "";

                boolean error = false;

                if (importData.getCheckerTimestamp() >= lastCheckTS) {
 
                    for (ImportCheckerNode checkerNode : importData.getNodes()) {

                        String color = "green";
                        
                        // Does it correspond to the reference value ?
                        if (!StringUtils.equals(checkerNode.getMd5Digest(), importData.getReferenceDigest())) {
                            color = "red";
                            error = true;
                        }

                        msg += "<font color=\"" + color + "\">" + checkerNode.getNodeName() + " </font><br>";
                    }


                    if (cacheService.isChecking()) {
                        msg += "waiting for cluster nodes ...";
                    } else {
                        if (!error)
                            msg += "<font color=\"green\"> All nodes are synchronized </font>";
                        else
                            msg += "<font color=\"red\"> Some nodes are NOT synchronized </font>";

                    }
                } else {
                    msg =  "waiting for cluster nodes ...";
                }


                return msg;
            } else
                return "";
        }

        return "";
    }

    
    
    
    public void startChecking () {
        
        lastCheckTS = 0;   
        
        if( StringUtils.isNotEmpty(portalObjectPath))   {
            try {
                PortalObject po = this.portalObjectContainer.getObject(PortalObjectId.parse(portalObjectPath, PortalObjectPath.CANONICAL_FORMAT));
                
                if( (po instanceof Page) || (po instanceof Portal)) {
                    cacheService.startCheckPortalObject(portalObjectPath);
                    lastCheckTS = System.currentTimeMillis();
                }   else
                    this.setMessages("Le chemin ne correspond pas à une page");

            } catch( Exception e)   {
                this.setMessages("Opération impossible : " + e.getMessage());
            }
        }
        
    }
    
    /**
     * Import listener.
     *
     * @param event upload event
     * @throws IOException
     */
    public void listener(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();

        File tempFile = null;
        try {
            if (!item.isTempFile()) {
                tempFile = File.createTempFile("upload", ".xml");
                FileOutputStream fos = new FileOutputStream(tempFile);
                fos.write(item.getData());
                fos.close();

                PortletContext ctx = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
                this.deployer = (IParametresPortailDeploymentManager) ctx.getAttribute(AdministrationConstants.CONFIG_DEPLOYER_NAME);
                portalObjectPath = this.deployer.chargerParametres(tempFile, MBeanServerLocator.locateJBoss());
                
               
                // Start automatic checking
                if( portalObjectPath != null)   {
                    
                    PortalObject po = this.portalObjectContainer.getObject(PortalObjectId.parse(portalObjectPath, PortalObjectPath.CANONICAL_FORMAT));
                    String check = po.getProperty("osivia.import.enableAutomaticChecking");
                    if( BooleanUtils.toBoolean(check))  {
                         startChecking();                         
                    }

                }
            }
        } catch (Exception e) {
            this.setMessages("Opération impossible : " + e.getMessage());
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }


    /**
     * Getter for uploadsAvailable.
     *
     * @return the uploadsAvailable
     */
    public int getUploadsAvailable() {
        return this.uploadsAvailable;
    }

    /**
     * Setter for uploadsAvailable.
     *
     * @param uploadsAvailable the uploadsAvailable to set
     */
    public void setUploadsAvailable(int uploadsAvailable) {
        this.uploadsAvailable = uploadsAvailable;
    }

    /**
     * Getter for autoUpload.
     *
     * @return the autoUpload
     */
    public boolean isAutoUpload() {
        return this.autoUpload;
    }

    /**
     * Getter for useFlash.
     *
     * @return the useFlash
     */
    public boolean isUseFlash() {
        return this.useFlash;
    }

    /**
     * Setter for useFlash.
     *
     * @param useFlash the useFlash to set
     */
    public void setUseFlash(boolean useFlash) {
        this.useFlash = useFlash;
    }

}
