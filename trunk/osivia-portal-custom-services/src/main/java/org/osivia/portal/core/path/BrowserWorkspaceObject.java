package org.osivia.portal.core.path;

import java.util.ArrayList;
import java.util.List;

/**
 * Browser workspace object abstract super-class.
 * 
 * @author Cédric Krommenhoek
 * @see Comparable
 */
public abstract class BrowserWorkspaceObject implements Comparable<BrowserWorkspaceObject> {

    /** Title. */
    private String title;

    /** Children. */
    private final List<BrowserWorkspace> children;


    /**
     * Constructor.
     */
    public BrowserWorkspaceObject() {
        super();
        this.children = new ArrayList<BrowserWorkspace>();
    }


    /**
     * {@inheritDoc}
     */
    public int compareTo(BrowserWorkspaceObject other) {
        if (other == null) {
            return 1;
        }

        if (this.title == null) {
            return -1;
        } else if (other.title == null) {
            return 1;
        } else {
            return this.title.compareTo(other.title);
        }
    }


    /**
     * Get glyph icon name.
     * 
     * @return glyph
     */
    public abstract String getGlyph();


    /**
     * Getter for title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
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
     * Getter for children.
     * 
     * @return the children
     */
    public List<BrowserWorkspace> getChildren() {
        return children;
    }

}
