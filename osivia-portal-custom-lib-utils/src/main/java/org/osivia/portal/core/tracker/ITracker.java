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
