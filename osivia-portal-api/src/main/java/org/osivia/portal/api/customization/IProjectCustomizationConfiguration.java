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


    /**
     * Compare current item CMS path with provided CMS path.
     *
     * @param cmsPath CMS path
     * @return true if CMS paths are equal
     */
    boolean equalsCMSPath(String cmsPath);


    /**
     * Compare current item web identifier with provided identifier.
     *
     * @param webId web identifier
     * @return true if web identifiers are equal
     */
    boolean equalsWebId(String webId);


    /**
     * Compare current item domain and web identifiers with provided identifiers.
     *
     * @param domainId domain identifier
     * @param webId web identifier
     * @return true if domain and web identifiers are equal
     */
    boolean equalsWebId(String domainId, String webId);


    /**
     * Set portal redirection URL.
     * 
     * @param redirectionURL redirection URL
     */
    void setRedirectionURL(String redirectionURL);

}
