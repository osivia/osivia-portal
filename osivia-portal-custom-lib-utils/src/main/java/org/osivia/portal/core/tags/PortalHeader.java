package org.osivia.portal.core.tags;



import org.jboss.portal.theme.LayoutConstants;
import org.jboss.portal.theme.page.PageResult;
import org.jboss.portal.theme.page.WindowContext;
import org.jboss.portal.theme.page.WindowResult;
import org.w3c.dom.Element;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PortalHeader extends SimpleTagSupport
{
   protected static final OutputFormat serializerOutputFormat = new OutputFormat() {
	   {
          setOmitXMLDeclaration(true);
       }
   };

   public void doTag() throws JspException, IOException
   {
      // Get page and region
      PageContext app = (PageContext)getJspContext();
      HttpServletRequest request = (HttpServletRequest)app.getRequest();

      //
      PageResult page = (PageResult)request.getAttribute(LayoutConstants.ATTR_PAGE);
      JspWriter out = this.getJspContext().getOut();
      if (page == null)
      {
         out.write("<p bgcolor='red'>No page to render!</p>");
         out.write("<p bgcolor='red'>The page to render (PageResult) must be set in the request attribute '" + LayoutConstants.ATTR_PAGE + "'</p>");
         out.flush();
         return;
      }

    
      out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/jquery.js\"></script>");
      out.write("<link rel=\"stylesheet\" id=\"settings_css\" href=\"/osivia-portal-custom-web-assets/common-css/common.css\" type=\"text/css\"/>");
      
      
      out.write("<link rel=\"stylesheet\" id=\"main_css\" href=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.css\" type=\"text/css\"/>");
      out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.js\"></script>");	 
      out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/fancybox/jquery.fancybox.pack.js\"></script>");
      
      
      out.write("<script type=\"text/javascript\" src=\"/osivia-portal-custom-web-assets/js/fancy-integration.js\"></script>");
      
      out.flush();
   }
}
