package org.osivia.portal.administration.ejb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;

import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.core.deploiement.IParametresPortailDeploymentManager;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * Import bean.
 * 
 * @author Cédric Krommenhoek
 * @see AbstractAdministrationBean
 */
@Name("importBean")
@Scope(ScopeType.EVENT)
public class ImportBean extends AbstractAdministrationBean {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal deployment manager. */
    private IParametresPortailDeploymentManager deployer;
    /** Uploads available. */
    private int uploadsAvailable = 5;
    /** Auto upload. */
    private final boolean autoUpload = false;
    /** Use flash. */
    private boolean useFlash = false;


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
                this.deployer.chargerParametres(tempFile, MBeanServerLocator.locateJBoss());
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
