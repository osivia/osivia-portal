package org.osivia.portal.api.theming;

/**
 * Template adapter.
 *
 * @author Cédric Krommenhoek
 */
public interface TemplateAdapter {

    /**
     * Adapt template.
     * 
     * @param spaceTemplate space template
     * @param targetTemplate target template
     * @return template
     */
    String adapt(String spaceTemplate, String targetTemplate);

}
