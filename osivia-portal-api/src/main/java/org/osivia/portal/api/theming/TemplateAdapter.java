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
     * @param spacePath space path
     * @param path current path
     * @param spaceTemplate space template
     * @param targetTemplate target template
     * @return template
     */
    String adapt(String spacePath, String path, String spaceTemplate, String targetTemplate);

}
