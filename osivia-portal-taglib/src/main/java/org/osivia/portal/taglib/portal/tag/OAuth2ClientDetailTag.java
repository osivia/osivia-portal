package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.api.oauth2.ClientDetail;
import org.osivia.portal.api.oauth2.IOAuth2Service;


/**
 *  OAuth2 client detail
 *
 * @author Jean-Sébastien Steux
 * @see SimpleTagSupport
 */
public class OAuth2ClientDetailTag extends SimpleTagSupport {

    
    /** OAuth2Service. */
    private final IOAuth2Service oauth2Service; 
    
    /** Client identifier. */
    private String clientId;

    /** Request variable name. */
    private String var;


    /**
     * Constructor.
     */
    public OAuth2ClientDetailTag() {
        super();
        
        // OAuth2 service
        this.oauth2Service = Locator.findMBean(IOAuth2Service.class, IOAuth2Service.MBEAN_NAME);
         
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Request
        ServletRequest request = pageContext.getRequest();

        // Child
        ClientDetail clientDetail = oauth2Service.getClientDetail(clientId);

        request.setAttribute(this.var, clientDetail);
    }


    /**
     * Getter for id.
     *
     * @return the id
     */
    public String getClientId() {
        return this.clientId;
    }

    /**
     * Setter for id.
     *
     * @param clientId the id to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }



    /**
     * Getter for var.
     *
     * @return the var
     */
    public String getVar() {
        return this.var;
    }

    /**
     * Setter for var.
     *
     * @param var the var to set
     */
    public void setVar(String var) {
        this.var = var;
    }

}
