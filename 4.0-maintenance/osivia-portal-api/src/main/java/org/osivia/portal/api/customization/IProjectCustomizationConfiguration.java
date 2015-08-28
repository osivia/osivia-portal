package org.osivia.portal.api.customization;

import javax.servlet.http.HttpServletRequest;

import org.jboss.portal.core.model.portal.Page;

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
     * Get current CMS path.
     *
     * @return CMS path
     */
    String getCMSPath();


    /**
     * Get current domain and web identifiers.
     *
     * @return domain and web identifiers, eg. [domainId, webId]
     */
    String[] getDomainAndWebId();


    /**
     * Get current page.
     *
     * @return page
     */
    Page getPage();


    /**
     * Check if customization occurs before invocation.
     *
     * @return true if customization occurs before invocation
     */
    boolean isBeforeInvocation();


    /**
     * Get HTTP servlet request.
     *
     * @return HTTP servlet request
     */
    HttpServletRequest getHttpServletRequest();


    /**
     * Get theme name.
     * 
     * @return theme name
     */
    String getThemeName();


    /**
     * Get administrator indicator.
     * 
     * @return administrator indicator
     */
    boolean isAdministrator();


    /**
     * Set portal redirection URL.
     *
     * @param redirectionURL redirection URL
     */
    void setRedirectionURL(String redirectionURL);

}
