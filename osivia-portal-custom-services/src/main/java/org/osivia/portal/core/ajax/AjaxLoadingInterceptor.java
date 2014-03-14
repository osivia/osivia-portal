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
package org.osivia.portal.core.ajax;

import org.jboss.portal.portlet.impl.jsr168.PortletContainerImpl;
import org.jboss.portal.portlet.impl.jsr168.api.PortletRequestImpl;
import org.jboss.portal.portlet.impl.jsr168.api.PortletURLImpl;
import org.jboss.portal.portlet.impl.jsr168.api.RenderRequestImpl;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.RenderInvocation;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.aspects.portlet.cache.ContentRef;
import org.jboss.portal.portlet.aspects.portlet.cache.StrongContentRef;
import org.jboss.portal.portlet.invocation.response.FragmentResponse;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.invocation.response.RevalidateMarkupResponse;
import org.jboss.portal.portlet.invocation.response.ContentResponse;
import org.jboss.portal.portlet.StateString;
import org.jboss.portal.portlet.ParametersStateString;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.spi.UserContext;
import org.jboss.portal.portlet.cache.CacheControl;
import org.jboss.portal.portlet.container.ContainerPortletInvoker;
import org.jboss.portal.portlet.container.PortletContainer;
import org.jboss.portal.common.util.ParameterMap;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.portlet.info.AjaxInfo;
import org.jboss.portal.WindowState;
import org.jboss.portal.Mode;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

import javax.portlet.PortletURL;

/**
 * Render Ajax des portlets
 * 
 * DESACTIVE
 * 
 */
public class AjaxLoadingInterceptor extends PortletInvokerInterceptor {

	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException,
			PortletInvokerException {
		

		
		boolean ajaxRequest = false;
	
		// Il s'agit déjà d'une recherche Ajax
		if ("true".equals(invocation.getDispatchedRequest().getParameter("pia-ajax")))	
			ajaxRequest= true;
		
		if( ajaxRequest)
		{
			// TODO : enlever le pia-ajax des NavigationalState
			//invocation.getNavigationalState(null);
			
			
			Map<String, Object> attributes = invocation.getRequestAttributes();
			Window window = (Window) attributes.get("osivia.window");
			
			//System.out.println("render portlet AJAX" + window.getName());
			
			
			PortletInvocationResponse response =  super.invoke(invocation);
			
			return response;
		}

		// Null pointer sur page layout dans back-office
		if (!(invocation instanceof RenderInvocation))
			return super.invoke(invocation);
		
		

		Map<String, Object> attributes = invocation.getRequestAttributes();
		
		if( attributes == null)
			return super.invoke(invocation);
		
		// Pas d'ajax en mode wizzard
		if( "true".equals(attributes.get(("osivia.window.wizzard"))))
				return super.invoke(invocation);
				
		
		Window window = (Window) attributes.get("osivia.window");
		if (window == null)
			return super.invoke(invocation);

		// Ajax activé dans propriétés
		if (!"1".equals(window.getProperties().get("osivia.ajaxLoading")))
			return super.invoke(invocation);

		// Rafraichissement partiel activé
		if (!"true".equals(window.getPage().getProperties().get("theme.dyna.partial_refresh_enabled")))
			return super.invoke(invocation);
		
		PortletContainer container = (PortletContainer) invocation
		.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
		
		//mode ajax supporté par le portlet
		 AjaxInfo ajax = container.getInfo().getAttachment(AjaxInfo.class);
		 if( ajax.getPartialRefresh() == false)
			 return super.invoke(invocation);

		try {



			PortletRequestImpl request = new RenderRequestImpl((PortletContainerImpl) container,
					(RenderInvocation) invocation);
			PortletURL url = PortletURLImpl.createRenderURL(invocation, request);
			
			// Ajout etat navigation pour requete ajax
			if( invocation.getNavigationalState() != null)	{
			Map<String, String[]> ns = invocation.getNavigationalState().decodeOpaqueValue(invocation.getNavigationalState().getStringValue());
			for (String nsKey : ns.keySet()) {
				url.setParameter(nsKey, ns.get(nsKey));

			}
			}

			url.setParameter("pia-ajax", "true");

			// reprise des paramétres d'init
			/*
			 * PortletURL initUrl = response.createRenderURL(); Enumeration
			 * enumParams = request.getParameterNames(); while
			 * (enumParams.hasMoreElements()) { String nomParam = (String)
			 * enumParams.nextElement(); initUrl.setParameter(nomParam,
			 * request.getParameter(nomParam)); }
			 */

			// Ajout d'un parametre pour éviter que le cache n'empeche
			// la requete Ajax de s'exécuter
			// initUrl.setParameter("ajax", "" + System.currentTimeMillis());
			
			System.out.println("--prepare portlet AJAX" + window.getName());

			String ajaxId = "ajaxLink_" + window.getName();

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);

			pw.println("<p align=\"center\">");
			pw.println("	<a id=\"" + ajaxId + "\" href=\"" + url.toString() + "\">");
			pw.println("			<img src=\"/osivia-portal-custom-web-assets/images/ajax_loading.gif\" border=\"0\">");
			pw.println("	</a>");
			pw.println("</p>");

			pw.println("<script type=\"text/javascript\">");

			pw.println("function loadPortlet_" + window.getName() + "()	{	");
			pw.println("  server_base_url=\"/portal/\";");
			pw.println("  var element = document.getElementById('" + ajaxId + "');");
			pw.println("  if( document.createEvent ) { ");
			pw.println("   var myEvt = document.createEvent('MouseEvents');");
			pw.println("   myEvt.initEvent(	   'click'  	   ,true	   ,true  	);");
			pw.println("   document.getElementById('" + ajaxId + "').dispatchEvent(myEvt);");
			pw.println("  } else	{ ");	
			// pour IE uniquement
			pw.println("   document.getElementById('" + ajaxId + "').fireEvent('onclick');");
			pw.println("  } ");
			pw.println(" } ");
			// TODO délai
			pw.println("setTimeout( \"loadPortlet_" + window.getName() + "()\", 100);");

			pw.println("</script>");

			ContentResponse fragment = new FragmentResponse(null, null, // Attributs
																		// Map
					"text/html", null, sw.toString(), "", null, // cachecontrol
					invocation.getPortalContext().getModes()); // Next modes

			//
			return fragment;
			
			

		} catch (Exception e) {
			// TODO logger
		}

		return super.invoke(invocation);

	}

}
