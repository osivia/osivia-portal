/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package org.osivia.portal.core.tag;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.PortalTheme;
import org.jboss.portal.theme.ThemeElement;
import org.jboss.portal.theme.ThemeLink;
import org.jboss.portal.theme.ThemeScript;
import org.jboss.portal.theme.render.RendererContext;
import org.jboss.portal.theme.render.ThemeContext;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.constants.InternalConstants;
import org.osivia.portal.core.theming.IPageHeaderResourceService;

/**
 * Theme tag handler.
 *
 * @author Cédric Krommenhoek
 * @see org.jboss.portal.theme.tag.ThemeTagHandler
 */
public class ThemeTagHandler extends org.jboss.portal.theme.tag.ThemeTagHandler {

    

    private static final String RESOURCE_SCRIPT = "script";

    private static final String RESOURCE_LINK = "link";

    /** . */
    private static Logger log = Logger.getLogger(ThemeTagHandler.class);

    /** . */
    private String themeName;
    
    /** . */
    private String resourceType;    
    




    /**
     * Default constructor.
     */
    public ThemeTagHandler() {
        super();
    }

    
    IPageHeaderResourceService pageHeaderResourceService;
    
    
    IPageHeaderResourceService getPageHeaderResourceService()   {
        if( pageHeaderResourceService == null)
            pageHeaderResourceService = Locator.findMBean(IPageHeaderResourceService.class, "osivia:service=PageHeaderResourceService");
        return pageHeaderResourceService;
    }

    
    public void originalDoTag() throws JspException, IOException
    {
       JspWriter out = this.getJspContext().getOut();

       // get page and region
       PageContext app = (PageContext)getJspContext();
       HttpServletRequest request = (HttpServletRequest)app.getRequest();

       // Get the theme provided as a render context attribute
       RendererContext rendererContext = (RendererContext)request.getAttribute(LayoutConstants.ATTR_RENDERCONTEXT);
       ThemeContext themeContext = rendererContext.getThemeContext();

       PortalTheme theme = themeContext.getTheme();

          // If no theme provided we use what may be on the tag
       if (theme == null && themeName != null && themeName.length() > 0)
       {
          theme = themeContext.getTheme(getThemeName());
       }

       //
       if (theme != null)
       {
            for (Iterator i = theme.getElements().iterator(); i.hasNext();) {
                ThemeElement el = (ThemeElement) i.next();

                boolean includeElement;

                if (StringUtils.isEmpty(resourceType))
                    includeElement = true;
                else {
                    if (RESOURCE_LINK.equals(resourceType) && (el instanceof ThemeLink))
                        includeElement = true;
                    else if (RESOURCE_SCRIPT.equals(resourceType) && (el instanceof ThemeScript))
                        includeElement = true;
                    else
                        includeElement = false;
                }


                if (includeElement) {

                    String element = getPageHeaderResourceService().adaptResourceElement(el.getElement());
                    out.println(element.toString());
                }

                // out.println(el.getElement());

            }
       }
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) this.getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Boolean layoutParsing = (Boolean) request.getAttribute(InternalConstants.ATTR_LAYOUT_PARSING);
        if (BooleanUtils.isNotTrue(layoutParsing)) {
            originalDoTag();
        }
    }
    public String getThemeName()
    {
       return themeName;
    }

    public void setThemeName(String name)
    {
       themeName = name;
    }
    
    


    
    /**
     * Getter for resourceType.
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }


    
    /**
     * Setter for resourceType.
     * @param resourceType the resourceType to set
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

}
