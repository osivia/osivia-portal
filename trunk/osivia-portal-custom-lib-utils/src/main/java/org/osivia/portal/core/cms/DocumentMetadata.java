package org.osivia.portal.core.cms;

import java.util.HashMap;
import java.util.Map;

/**
 * Document metadata.
 *
 * @author Cédric Krommenhoek
 */
public class DocumentMetadata {

    /** Title. */
    private String title;
    /** SEO properties. */
    private final Map<String, String> seo;


    /**
     * Constructor.
     */
    public DocumentMetadata() {
        super();
        this.seo = new HashMap<String, String>();
    }


    /**
     * Getter for title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for seo.
     *
     * @return the seo
     */
    public Map<String, String> getSeo() {
        return this.seo;
    }

}
