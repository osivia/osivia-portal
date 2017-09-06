package org.osivia.portal.api.cms;

import java.util.Arrays;
import java.util.List;

/**
 * File document type.
 * 
 * @author Cédric Krommenhoek
 */
public class FileDocumentType {

    /** Icon. */
    private String icon;
    
    /** Name. */
    private final String name;
    /** MIME primary type. */
    private final String mimePrimaryType;
    /** MIME sub types. */
    private final List<String> mimeSubTypes;
    
    
    /**
     * Constructor.
     * 
     * @param name file type name
     * @param mimePrimaryType file MIME primary type
     * @param mimeSubTypes file MIME sub types
     */
    public FileDocumentType(String name, String mimePrimaryType, String... mimeSubTypes) {
        super();
        this.name = name;
        this.mimePrimaryType = mimePrimaryType;
        this.mimeSubTypes = Arrays.asList(mimeSubTypes);
    }


    /**
     * Getter for icon.
     * 
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter for icon.
     * 
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Getter for name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for mimePrimaryType.
     * 
     * @return the mimePrimaryType
     */
    public String getMimePrimaryType() {
        return mimePrimaryType;
    }

    /**
     * Getter for mimeSubTypes.
     * 
     * @return the mimeSubTypes
     */
    public List<String> getMimeSubTypes() {
        return mimeSubTypes;
    }
    
}
