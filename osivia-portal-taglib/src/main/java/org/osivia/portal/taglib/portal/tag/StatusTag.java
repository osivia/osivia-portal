package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;


/**
 * Set Response status code
 *
 * @author Jean-Sébastien Steux
 * @see SimpleTagSupport
 */
public class StatusTag extends SimpleTagSupport {


    /** Request variable name. */
    private String code;

    
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
     * Constructor.
     */
    public StatusTag() {
        super();

         
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Context
        PageContext pageContext = (PageContext) this.getJspContext();
        // Request
        HttpServletResponse reponse = (HttpServletResponse) pageContext.getResponse();


        reponse.setStatus(Integer.parseInt(getCode()));
    }






}
