package org.osivia.portal.core.customization;


/**
 * A notification interface ICMSCustomization about plugin deployments
 */
public interface ICMSCustomizationObserver {
    
    /**
     * This method is called during the deployment/undeployment Phase
     */
    void notifyDeployment(); 

}
