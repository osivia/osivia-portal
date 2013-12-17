package org.osivia.portal.core.theming;

/**
 * Region decorator.
 *
 * @author Cédric Krommenhoek
 */
public class RegionDecorator {

    /** Header content. */
    private final String headerContent;
    /** Footer content. */
    private final String footerContent;


    /**
     * Constructor.
     * 
     * @param headerContent header content
     * @param footerContent footer content
     */
    public RegionDecorator(String headerContent, String footerContent) {
        super();
        this.headerContent = headerContent;
        this.footerContent = footerContent;
    }


    /**
     * Getter for headerContent.
     * 
     * @return the headerContent
     */
    public String getHeaderContent() {
        return this.headerContent;
    }

    /**
     * Getter for footerContent.
     * 
     * @return the footerContent
     */
    public String getFooterContent() {
        return this.footerContent;
    }

}
