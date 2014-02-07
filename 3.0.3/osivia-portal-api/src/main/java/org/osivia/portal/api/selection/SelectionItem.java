package org.osivia.portal.api.selection;

import java.util.Map;

/**
 * Selection item bean.
 * 
 * @author Cédric Krommenhoek
 */
public class SelectionItem {

    /** Identifier. */
    private String id;
    /** Display title. */
    private String displayTitle;
    /** Properties. */
    private Map<String, Object> properties;

    /**
     * Default contructor.
     */
    public SelectionItem() {
        super();
    }

    /**
     * Generated constructor.
     * 
     * @param id identifier
     * @param displayTitle display title
     * @param properties properties
     */
    public SelectionItem(String id, String displayTitle, Map<String, Object> properties) {
        super();
        this.id = id;
        this.displayTitle = displayTitle;
        this.properties = properties;
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
        SelectionItem other = (SelectionItem) obj;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "SelectionItem [id=" + this.id + ", displayTitle=" + this.displayTitle + "]";
    }

    /**
     * Getter.
     * 
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter.
     * 
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter.
     * 
     * @return the displayTitle
     */
    public String getDisplayTitle() {
        return this.displayTitle;
    }

    /**
     * Setter.
     * 
     * @param displayTitle the displayTitle to set
     */
    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    /**
     * Getter.
     * 
     * @return the properties
     */
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Setter.
     * 
     * @param properties the properties to set
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}
