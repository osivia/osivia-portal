package org.osivia.portal.core.sequencing;

import org.osivia.portal.core.attributes.StorageAttributeValue;

/**
 * Portlet sequencing attribute value.
 *
 * @author Cédric Krommenhoek
 * @see StorageAttributeValue
 */
public class PortletSequencingAttributeValue implements StorageAttributeValue {

    /** Attribute. */
    private final Object attribute;


    /**
     * Constructor.
     *
     * @param attribute attribute
     */
    public PortletSequencingAttributeValue(Object attribute) {
        super();
        this.attribute = attribute;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PortletSequencingAttributeValue clone() {
        return new PortletSequencingAttributeValue(this.attribute);
    }


    /**
     * Getter for attribute.
     *
     * @return the attribute
     */
    public Object getAttribute() {
        return this.attribute;
    }

}
