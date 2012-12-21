package org.jboss.portal.core.aspects.portlet;

import java.util.Iterator;

import org.jboss.portal.common.util.MultiValuedPropertyMap;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.impl.spi.AbstractServerContext;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.UpdateNavigationalStateResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="mailto:mholzner@novell.com">Martin Holzner</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar B</a>
 * @version $Revision: 11068 $
 */
public class HttpHeaderInterceptor extends CorePortletInterceptor
{

   private String defaultCookiePath = null;

   /**
    *  Cookie path to set when none has been defined
    */
   public String getDefaultCookiePath()
   {
      return defaultCookiePath;
   }

   public void setDefaultCookiePath(String defaultCookiePath)
   {
      this.defaultCookiePath = defaultCookiePath;
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException,
         PortletInvokerException
   {

      PortletInvocationResponse response = super.invoke(invocation);

      if (response instanceof UpdateNavigationalStateResponse)
      {
         UpdateNavigationalStateResponse unsr = (UpdateNavigationalStateResponse) response;
         if (unsr.getProperties() != null)
         {
            for (Cookie cookie : unsr.getProperties().getCookies())
            {
               addCookie(invocation, cookie);
            }
            setHeaders(unsr.getProperties().getTransportHeaders(),
                        ((AbstractServerContext) invocation.getServerContext()).getResponse());
         }
      }
      else if (response instanceof FragmentResponse)
      {
         FragmentResponse fr = (FragmentResponse) response;
         if (fr.getProperties() != null)
         {
            for (Cookie cookie : fr.getProperties().getCookies())
            {
               addCookie(invocation, cookie);
            }
          setHeaders(fr.getProperties().getTransportHeaders(),
                        ((AbstractServerContext) invocation.getServerContext()).getResponse());
         }
      }
      //
      return response;
   }

   private void addCookie(PortletInvocation invocation, Cookie cookie)
   {
      if ((cookie.getPath() == null || "".equals(cookie.getPath().trim())) && defaultCookiePath != null)
      {
         cookie.setPath(defaultCookiePath);
      }
   ((AbstractServerContext) invocation.getServerContext()).getResponse().addCookie(cookie);
   }

   private void setHeaders(MultiValuedPropertyMap<String> map, HttpServletResponse response)
   {
      for (String key: map.keySet())
      {
         // It might have already been added by another portlet or
         // its the first time we are adding it so reset it with first value.
         // The winner is the portlet in the last order of render phase.
    	  
    	  try	{
    		  if( key != null && map.getValue(key) != null)
    			  response.setHeader(key, map.getValue(key));
    	  }  catch (NullPointerException e){
          	   // Parfois, les requetes issues de firefox n'accepte pas les modifs de Header
          	   // ceci provoque un NullPointerException
          	   // (heureusement, il s'agit uniquement de la première requete)        
           	  
            }

         if (map.size() > 1)
         {
            // If multiple values found just add them
            String value;
            for (Iterator<String> i = map.getValues(key).listIterator(1); i.hasNext();)
            {
               value = i.next();
               response.addHeader(key, value);
            }
         }
      }
   }
}