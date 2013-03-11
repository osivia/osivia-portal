package org.osivia.portal.core.tracker;

import java.io.Serializable;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.portal.common.http.HttpRequest;

public class TrackerBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Stack stack;
	private HttpSession session;
	private HttpServletRequest request;
	private TrackerBean parent;
	
	public TrackerBean getParent() {
		return parent;
	}
	public void setParent(TrackerBean parent) {
		this.parent = parent;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public TrackerBean() {
		super();
		this.stack = new Stack();

	}
	public Stack getStack() {
		return stack;
	}
	public void setStack(Stack stack) {
		this.stack = stack;
	}
	public HttpSession getSession() {
		return session;
	}
	public void setSession(HttpSession session) {
		this.session = session;
	}


}
