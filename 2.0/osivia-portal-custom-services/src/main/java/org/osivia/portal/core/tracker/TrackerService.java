package org.osivia.portal.core.tracker;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.portal.common.http.HttpRequest;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerInterceptor;
import org.jboss.portal.core.controller.ControllerResponse;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.core.tracker.ITracker;


/**
 * 
 * Permet de connaitrea tout moment la commande en cours
 *  (les commandes sont stockées dans une pile)
 * 
 * 
 * @author jsteux
 *
 */
@SuppressWarnings("unchecked")

public class TrackerService extends ServiceMBeanSupport implements ITracker, Serializable {
	
	private Log log = LogFactory.getLog(TrackerService.class);
	
	private  ThreadLocal<TrackerBean> trackerBean = new ThreadLocal<TrackerBean>();

		
	
	public TrackerBean getTrackerBean(){
		TrackerBean bean = trackerBean.get();
		if( bean == null)	{
			bean = new TrackerBean( );
			trackerBean.set(bean);
		}
			
		return bean;
	}
	
	
	public Object getInternalBean(){
		TrackerBean bean = trackerBean.get();
		if( bean == null)	{
			bean = new TrackerBean( );
			trackerBean.set(bean);
		}
			
		return bean;
	}

	
	public void createThreadContext( Object main){
			TrackerBean mainBean = (TrackerBean) main;
			TrackerBean newBean = new TrackerBean( );
			newBean.setStack((Stack) mainBean.getStack().clone());
			newBean.setRequest(mainBean.getRequest());
			newBean.setSession(mainBean.getSession());
			newBean.setParent(mainBean);
			trackerBean.set(newBean);
		}
	
	public Object getParentBean()	{
		return getTrackerBean().getParent();
	}


	public Stack getStack(){
		
		Stack stack =  getTrackerBean().getStack();
		return stack;
	}
	
	

	public Object getCurrentState() {
		try	{ return getStack().peek();
		}
		catch(EmptyStackException e)	{
			return null;
		}
	}

	public void popState() {
		getStack().pop();
	}

	public void pushState(Object state) {
		getStack().push( state);
	}
	

	public void startService() throws Exception {
		log.info("start service TrackerService");
	}

	public void stopService() throws Exception {
		log.info("stop service TrackerService");
	}

	public HttpSession getHttpSession() {
		
		return getTrackerBean().getSession();
	}

	public void setHttpSession(HttpSession session) {
		getTrackerBean().setSession(session);
	}
	
	public HttpServletRequest getHttpRequest() {
		
		return getTrackerBean().getRequest();
	}

	public void setHttpRequest(HttpServletRequest request) {
		getTrackerBean().setRequest(request);
	}


	public void init() {
		trackerBean.set(null);
		
	}
	
	
}
