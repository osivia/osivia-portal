package org.osivia.portal.api.customization;

/**
 * Project customization configuration interface.
 *
 * @author Cédric Krommenhoek
 */
public interface IProjectCustomizationConfiguration {

    /** Project customizer identifier. */
    String CUSTOMIZER_ID = "osivia.project.customizer.id";
    /** Project customizer configuration attribute name. */
    String CUSTOMIZER_ATTRIBUTE_CONFIGURATION = "osivia.project.customizer.configuration";
    /** Project customizer controller context attribute name. */
    String CUSTOMIZER_ATTRIBUTE_CONTROLLER_CONTEXT = "osivia.project.customizer.controllerContext";


    /**
     * Create CMS redirection.
     *
     * @param cmsPath CMS path
     * @param redirectionURL redirection URL
     */
    void createCMSRedirection(String cmsPath, String redirectionURL);


    /**
     * Create web redirection.
     * 
     * @param webId web ID
     * @param redirectionURL redirection URL
     */
    void createWebRedirection(String webId, String redirectionURL);


    /**
     * Create web redirection.
     * 
     * @param domainId domain ID
     * @param webId web ID
     * @param redirectionURL redirection URL
     */
    void createWebRedirection(String domainId, String webId, String redirectionURL);

}
