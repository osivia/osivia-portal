package org.osivia.portal.core.theming.temp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.impl.JSPRendererContext;
import org.jboss.portal.theme.render.RenderException;
import org.jboss.portal.theme.render.renderer.PageRendererContext;

/**
 * Region tag handler.
 *
 * @author Cédric Krommenhoek
 * @see SimpleTagSupport
 */
public class RegionTagHandler extends SimpleTagSupport {

    /** Region name. */
    private String name;
    /** HTML identifier for CSS style. */
    private String id;
    /** Region CMS indicator. */
    private Boolean regionCms;


    /**
     * Default constructor.
     */
    public RegionTagHandler() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        ServletRequest request = pageContext.getRequest();

        PageRendererContext page = (PageRendererContext) request.getAttribute(LayoutConstants.ATTR_PAGE);
        JSPRendererContext renderContext = (JSPRendererContext) request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
        if ((page == null) || (renderContext == null)) {
            return;
        }

        IRegionThemingRendererContext regionRendererContext = new RegionThemingRendererContext(page, this.name, this.id, this.regionCms);
        PrintWriter writer = new PrintWriter(pageContext.getOut());
        renderContext.setWriter(writer);
        try {
            renderContext.render(regionRendererContext);
        } catch (RenderException e) {
            throw new JspException(e);
        }
        writer.flush();
    }


    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter for regionCms.
     *
     * @param regionCms the regionCms to set
     */
    public void setRegionCms(Boolean regionCms) {
        this.regionCms = regionCms;
    }

}
