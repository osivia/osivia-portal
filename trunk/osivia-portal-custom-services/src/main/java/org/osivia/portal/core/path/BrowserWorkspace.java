package org.osivia.portal.core.path;

/**
 * Browser workspace.
 * 
 * @author Cédric Krommenhoek
 * @see BrowserWorkspaceObject
 */
public class BrowserWorkspace extends BrowserWorkspaceObject {

    /** Path. */
    private String path;
    /** Glyph icon name. */
    private String glyph;


    /**
     * Constructor.
     */
    public BrowserWorkspace() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getGlyph() {
        return glyph;
    }


    /**
     * Getter for path.
     * 
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Setter for path.
     * 
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Setter for glyph.
     * 
     * @param glyph the glyph to set
     */
    public void setGlyph(String glyph) {
        this.glyph = glyph;
    }

}
