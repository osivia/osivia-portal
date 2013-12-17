package org.osivia.portal.administration.ejb;

import java.io.Serializable;


/**
 * Page tree node data.
 *
 * @author Cédric Krommenhoek
 * @see Serializable
 */
public class PageTreeData implements Serializable {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Page identifier. */
    private final String id;
    /** Page name. */
	private final String name;


    /**
     * Constructor using fields.
     *
     * @param id page identifier
     * @param name page name
     */
    public PageTreeData(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.name;
    }

}
