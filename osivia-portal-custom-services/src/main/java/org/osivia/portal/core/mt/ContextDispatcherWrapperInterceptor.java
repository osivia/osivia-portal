/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.osivia.portal.core.mt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderPageCommand;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.PortletInvokerInterceptor;
import org.jboss.portal.portlet.container.ContainerPortletInvoker;
import org.jboss.portal.portlet.container.PortletApplication;
import org.jboss.portal.portlet.container.PortletContainer;
import org.jboss.portal.portlet.invocation.PortletInvocation;
import org.jboss.portal.portlet.invocation.response.PortletInvocationResponse;
import org.jboss.portal.portlet.spi.ServerContext;
import org.jboss.portal.web.RequestDispatchCallback;
import org.jboss.portal.web.ServletContainer;
import org.jboss.portal.web.ServletContainerFactory;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.portlet.Refreshable;
import org.osivia.portal.api.portlet.RequestLifeCycle;
import org.osivia.portal.core.tracker.ITracker;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * Cette requete permet d'associer une requete à chaque thread sans risque
 * d'écrasement entre les threads
 * 
 * Cet intercepteur doit remplacer
 * org.jboss.portal.portlet.aspects.portlet.ContextDispatcherInterceptor
 */

public class ContextDispatcherWrapperInterceptor extends PortletInvokerInterceptor {

	private static Log logger = LogFactory.getLog(ContextDispatcherWrapperInterceptor.class);

	private transient ITracker tracker;

	public ITracker getTracker() {
		return tracker;
	}

	public void setTracker(ITracker tracker) {
		this.tracker = tracker;
	}

	/** . */
	public static final String REQ_ATT_COMPONENT_INVOCATION = "org.jboss.portal.attribute.component_invocation";

	public static final String REQ_SYNCHRONIZER = "osivia.attribute.synchronizer";

	/** . */
	private ServletContainerFactory servletContainerFactory;

	public ServletContainerFactory getServletContainerFactory() {
		return servletContainerFactory;
	}

	public void setServletContainerFactory(ServletContainerFactory servletContainerFactory) {
		this.servletContainerFactory = servletContainerFactory;
	}

	public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException {
		PortletContainer container = (PortletContainer) invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
		PortletApplication portletApplication = container.getPortletApplication();
		ServerContext reqCtx = invocation.getServerContext();
		ServletContext targetCtx = portletApplication.getContext().getServletContext();
		ServletContainer servletContainer = servletContainerFactory.getServletContainer();
		try {
			return (PortletInvocationResponse) reqCtx.dispatch(servletContainer, targetCtx, callback, invocation);
		} catch (Exception e) {
			if (e instanceof PortletInvokerException) {
				throw (PortletInvokerException) e;
			} else if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else if (e instanceof ServletException) {
				ServletException se = (ServletException) e;

				//
				if (se.getRootCause() != null && se.getRootCause() instanceof Exception) {
					e = (Exception) se.getRootCause();
				}
			}

			//
			throw new PortletInvokerException(e);
		}
	}

	private final RequestDispatchCallback callback = new RequestDispatchCallback() {
		public Object doCallback(ServletContext dispatchedServletContext, HttpServletRequest req, HttpServletResponse resp, Object handback)
				throws ServletException, IOException {
			PortletInvocation invocation = (PortletInvocation) handback;

			//
			try {
                // HTTP session
                HttpSession session = req.getSession();

                // On met le bean du thread parent pour gérer la synchronisation
                Object parentBean = getTracker().getParentBean();

                
                // Création d'une requete wrappée spécifique à chaque portlet
                HttpServletRequestWrapper wrappedRequest = new JBossWrappedRequest(req, parentBean);

				// invocation.setDispatchedRequest(req);
				invocation.setDispatchedRequest(wrappedRequest);

				// Set dispatched request and response and invocation
				// invocation.setDispatchedResponse(resp);
				invocation.setDispatchedResponse(resp);

				invocation.getDispatchedRequest().setAttribute(REQ_ATT_COMPONENT_INVOCATION, invocation);

				/* Synchronize session datas 
				 * 
				 * To avoid conflict between sessions associated with dynamic windows which have the same path
				 * 
				 * 
				 * */

				String windowUniqueID = (String) invocation.getAttribute("osivia.window.uniqueID");


                if (windowUniqueID != null) {
					
					//logger.info("windowUniqueID =" + windowUniqueID);

					String windowPath = (String) invocation.getAttribute("osivia.window.path");

					String osiviaPrefix = "osivia.portlet.p." + windowPath;

					String currentUIDName = osiviaPrefix + ".currentUniqueUID";

					String oldUID = (String) session.getAttribute(currentUIDName);
					


					if (oldUID != null && !windowUniqueID.equals(oldUID)) {

						//logger.info("remove " + oldUID);
						
						
						String saveNewPrefix = osiviaPrefix + ".save."+windowUniqueID+".";
						String saveOldPrefix = osiviaPrefix + ".save."+oldUID+".";
						int saveNewPrefixLength = saveNewPrefix.length();

						/*
						 * Pour ce path, il y a une autre window il faut
						 * sauvegarder les données de session et restaure les
						 * nouvelles données
						 */

						String portletName = "javax.portlet.p." + windowPath;

						List<String> toRemove = new ArrayList<String>();
						Enumeration attrs = session.getAttributeNames();
						while (attrs.hasMoreElements()) {
							String attName = (String) attrs.nextElement();
							if (attName.startsWith(portletName)) {
								
								// Sauve parameters
								session.setAttribute(saveOldPrefix+ attName, session.getAttribute(attName) );

								toRemove.add(attName);
							}
						}

						/* Destroy existing values */
						
						for (String name : toRemove) {
							//logger.info("remove data" + name);
							session.setAttribute(name, null);
						}
						
						/* Restore values */
						
						attrs = session.getAttributeNames();
						while (attrs.hasMoreElements()) {
							String attName = (String) attrs.nextElement();
							if (attName.startsWith(saveNewPrefix)) {
								
								String restoreName = attName.substring(saveNewPrefixLength);
								
								//logger.info("restore data " + attName );
								
								// restore parameters
								session.setAttribute(restoreName, session.getAttribute(attName) );
							}
						}
				
						
					}
					
					
					

					
					// Save current window ID
					session.setAttribute(currentUIDName, windowUniqueID);

				}

                      

                // Refresh indicator
				Boolean refresh = (Boolean) invocation.getRequestAttributes().get(Constants.PORTLET_ATTR_PAGE_REFRESH);
				
				if( BooleanUtils.isTrue(refresh) )   {
                    Window window = (Window) invocation.getRequestAttributes().get("osivia.window");
                    String attributePrefix = "javax.portlet.p." + window.getId() + "?";
                    
                    // Remove Spring Framework implicit model
                    session.removeAttribute(attributePrefix + AnnotationMethodHandlerAdapter.IMPLICIT_MODEL_SESSION_ATTRIBUTE);

                    Enumeration<?> attributeNames = session.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String attributeName = (String) attributeNames.nextElement();
                        if (StringUtils.startsWith(attributeName, attributePrefix)) {
                            Object attribute = session.getAttribute(attributeName);
                            if (attribute.getClass().isAnnotationPresent(Refreshable.class)) {
                                // Remove portlet session attribute with @Refreshable annotation
                                session.removeAttribute(attributeName);
                            }
                        }
                    }
				}
				
				
				
                // reload model on page rendering
                Boolean renderPage;
                Stack stack = getTracker().getStack();
                if (stack.size() >= 2 && stack.get(1) instanceof RenderPageCommand)
                    renderPage = true;
                else
                    renderPage = false;

                
                if( renderPage )   {
                    Window window = (Window) invocation.getRequestAttributes().get("osivia.window");
                    String attributePrefix = "javax.portlet.p." + window.getId() + "?";
                    
                    boolean found = false;

                    Enumeration<?> attributeNames = session.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String attributeName = (String) attributeNames.nextElement();
                        if (StringUtils.startsWith(attributeName, attributePrefix)) {
                            found = chekRefreshCycle(session, found, attributeName);
                        }
                    }
                    
                    if( found)  {
                        // Remove Spring Framework implicit model
                        session.removeAttribute(attributePrefix + AnnotationMethodHandlerAdapter.IMPLICIT_MODEL_SESSION_ATTRIBUTE);
                    }
                    
                }			
				
				

				
				//
				wrappedRequest.setAttribute(REQ_ATT_COMPONENT_INVOCATION, invocation);

//				// On met le bean du thread parent pour gérer la synchronisation
//				Object parentBean = getTracker().getParentBean();
//				wrappedRequest.setAttribute(REQ_SYNCHRONIZER, parentBean);

				//
				PortletInvocationResponse response = ContextDispatcherWrapperInterceptor.super.invoke(invocation);


				return response;

			} catch (Exception e) {
				throw new ServletException(e);
			} finally {
				// Clear dispatched request and response
				req.setAttribute(REQ_ATT_COMPONENT_INVOCATION, null);

				//
				invocation.setDispatchedRequest(null);
				invocation.setDispatchedResponse(null);
			}
		}

        private boolean chekRefreshCycle(HttpSession session, boolean found, String attributeName) {
            
            Object attribute = session.getAttribute(attributeName);
            if (attribute.getClass().isAnnotationPresent(RequestLifeCycle.class)) {
                // Remove portlet session attribute with @RequestLifeCycle annotation
                found = true;
            }   else if (attribute instanceof BindingAwareModelMap) {
                Collection<Object> values = ((BindingAwareModelMap) attribute).values();
                for( Object childAttribute: values) {
                    if (childAttribute.getClass().isAnnotationPresent(RequestLifeCycle.class)) {
                        found = true;
                    }
                }
            }
            
            if( found)
                session.removeAttribute(attributeName);
            return found;
        }
	};

}
