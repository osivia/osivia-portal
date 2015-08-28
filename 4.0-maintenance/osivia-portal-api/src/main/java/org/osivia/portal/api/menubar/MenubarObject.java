package org.osivia.portal.api.menubar;

/**
 * Abstract menubar object.
 *
 * @author Cédric Krommenhoek
 */
public abstract class MenubarObject {

    /** Menubar object title. */
    private String title;
    /** Menubar object glyphicons. */
    private String glyphicon;
    /** Menubar object order. */
    private int order;
    /** Disabled menubar object indicator. */
    private boolean disabled;

    /** Menubar object identifier. */
    private final String id;


    /**
     * Constructor.
     *
     * @param id menubar object identifier
     * @param title menubar object title
     * @param glyphicon menubar object glyphicon
     * @param order menubar object order
     * @param disabled disabled menubar object indicator
     */
    public MenubarObject(String id, String title, String glyphicon, int order, boolean disabled) {
        super();
        this.id = id;
        this.title = title;
        this.glyphicon = glyphicon;
        this.order = order;
        this.disabled = disabled;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        MenubarObject other = (MenubarObject) obj;
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
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
     * Getter for glyphicon.
     *
     * @return the glyphicon
     */
    public String getGlyphicon() {
        return this.glyphicon;
    }

    /**
     * Setter for glyphicon.
     *
     * @param glyphicon the glyphicon to set
     */
    public void setGlyphicon(String glyphicon) {
        this.glyphicon = glyphicon;
    }

    /**
     * Getter for order.
     *
     * @return the order
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Setter for order.
     *
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Getter for disabled.
     *
     * @return the disabled
     */
    public boolean isDisabled() {
        return this.disabled;
    }

    /**
     * Setter for disabled.
     *
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

}