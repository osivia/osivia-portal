package org.osivia.portal.api.oauth2;


/**
 * OAuth2 client detail bean.
 *
 * @author Jean-Sébastien Steux
 */

public class ClientDetail {

    /** The code. */
    private String code;
    
    /** The title. */
    private String title;

    /**
     * Instantiates a new client detail.
     *
     * @param code the code
     * @param title the title
     */
    public ClientDetail(String code, String title) {
        super();
        this.code = code;
        this.title = title;
    }

    
    /**
     * Getter for code.
     * @return the code
     */
    public String getCode() {
        return code;
    }

    
    /**
     * Setter for code.
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    
    /**
     * Getter for title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    
    /**
     * Setter for title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }



}
