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
package org.osivia.portal.core.tracker;

import java.io.Serializable;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.portal.common.http.HttpRequest;



public interface ITracker extends Serializable {
	public void init();
	public Object getCurrentState();
	public Stack getStack();
	public void pushState( Object state);
	public void popState();
	public HttpSession getHttpSession();
	public void setHttpSession( HttpSession session);
	public HttpServletRequest getHttpRequest() ;
	public void setHttpRequest(HttpServletRequest request) ;
	public Object getInternalBean();
	public void createThreadContext( Object main);
	public Object getParentBean();

}
