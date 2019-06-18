package org.osivia.portal.api.cms;

import javax.activation.MimeType;

/**
 * File MIME type.
 * 
 * @author Cédric Krommenhoek
 */
public class FileMimeType {

    /** MIME type object. */
    private MimeType mimeType;
    /** MIME type extension. */
    private String extension;
    /** MIME type description. */
    private String description;
    /** MIME type display. */
    private String display;
    /** MIME type icon. */
    private String icon;
    /** MIME type pdf convertion */
    private boolean pdfConvertible;


    


    /**
     * Constructor.
     */
    public FileMimeType() {
        super();
    }

    
    /**
     * Getter for pdfConvertible.
     * @return the pdfConvertible
     */
    public boolean isPdfConvertible() {
        return pdfConvertible;
    }


    
    /**
     * Setter for pdfConvertible.
     * @param pdfConvertible the pdfConvertible to set
     */
    public void setPdfConvertible(boolean pdfConvertible) {
        this.pdfConvertible = pdfConvertible;
    }


    /**
     * Getter for mimeType.
     * 
     * @return the mimeType
     */
    public MimeType getMimeType() {
        return mimeType;
    }

    /**
     * Setter for mimeType.
     * 
     * @param mimeType the mimeType to set
     */
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Getter for extension.
     * 
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Setter for extension.
     * 
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * Getter for description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for display.
     * 
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Setter for display.
     * 
     * @param display the display to set
     */
    public void setDisplay(String display) {
        this.display = display;
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

}
