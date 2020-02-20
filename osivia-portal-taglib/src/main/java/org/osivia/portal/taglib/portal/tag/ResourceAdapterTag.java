package org.osivia.portal.taglib.portal.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.theming.IPageHeaderResourceService;
import org.osivia.portal.taglib.common.PortalSimpleTag;


/**
 * Adapt resource name
 *
 * @author Jean-Sébastien Steux
 * @see SimpleTagSupport
 */
public class ResourceAdapterTag extends PortalSimpleTag {


    /** The resource adapter. */
    private final IPageHeaderResourceService resourceAdapter;


    /** Script/resource insertion directive. */
    private String directive;




    /**
     * Constructor.
     */
    public ResourceAdapterTag() {
        super();
        this.resourceAdapter = Locator.findMBean(IPageHeaderResourceService.class, IPageHeaderResourceService.MBEAN_NAME);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Page context
        PageContext pageContext = (PageContext) this.getJspContext();


        // Write property into JSP
        JspWriter out = pageContext.getOut();
        out.write(resourceAdapter.adaptResourceElement(getDirective()));

    }


    
    /**
     * Getter for directive.
     * @return the directive
     */
    public String getDirective() {
        return directive;
    }


    
    /**
     * Setter for directive.
     * @param directive the directive to set
     */
    public void setDirective(String directive) {
        this.directive = directive;
    }


}
