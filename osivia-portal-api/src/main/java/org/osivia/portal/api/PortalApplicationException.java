package org.osivia.portal.api;

/**
 * Portal application exception.
 * 
 * @author Cédric Krommenhoek
 * @see PortalException
 */
public class PortalApplicationException extends PortalException {

    /** Default serial version identifier. */
    private static final long serialVersionUID = 1L;


    /** Application domain. */
    private String domain;
    /** Application error code. */
    private String code;


    /**
     * Constructor.
     * 
     * @param message exception message
     */
    public PortalApplicationException(String message) {
        super(message);
    }


    /**
     * Constructor.
     * 
     * @param message exception message
     * @param domain application domain
     * @param code application error code
     */
    public PortalApplicationException(String message, String domain, String code) {
        super(message);
        this.domain = domain;
        this.code = code;
    }


    /**
     * Getter for domain.
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Getter for code.
     * 
     * @return the code
     */
    public String getCode() {
        return code;
    }

}
