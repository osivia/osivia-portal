/*
 * Created on 14 sept. 2005
 *
 *
 * Unit� d'ex�cution permettant de lancer un service
 */
package org.osivia.portal.core.mt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;

import javax.naming.InitialContext;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.UserTransaction;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.portal.core.aspects.server.UserInterceptor;
import org.jboss.portal.core.controller.ControllerCommand;
import org.jboss.portal.core.controller.ControllerContext;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.command.render.RenderWindowCommand;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.model.portal.navstate.WindowNavigationalState;
import org.jboss.portal.core.navstate.NavigationalStateKey;
import org.jboss.portal.identity.User;
import org.jboss.portal.server.ServerInvocation;
import org.jboss.security.SecurityAssociation;
import org.osivia.portal.api.profiler.IProfilerService;
import org.osivia.portal.core.page.PageProperties;
import org.osivia.portal.core.portalobjects.DynamicPortalObjectContainer;
import org.osivia.portal.core.tracker.ITracker;

import com.arjuna.ats.jta.TransactionManager;


/**
 * 
 * Chaque instance est cr�ee � partir d'un ServiceInvoker qui sera notifi� � la
 * fin de l'ex�cution
 * 
 * @author jeanseb
 */

public class ServiceThread implements Runnable {
	

	ServicesInvoker invoker;
	RenderWindowCommand renderCmd;
	ControllerContext context;
	Window window;
	String policyCtxId;
	Subject subject ;
	Principal principal ;
	Object credential ;
	ITracker trackerService;
	Object trackerBean;
	PageProperties pageBean;
	IProfilerService profiler;
	Thread currentThread;
	KillerThread killerThread;

	protected static final Log windowlogger = LogFactory.getLog("PORTAL_WINDOW");
	
	public void setKillerThread(KillerThread killerThread) {
		this.killerThread = killerThread;
		logger.info("Supervisor thread "+killerThread+ " associated with current thread " +  currentThread);
		
	}

	public Thread getCurrentThread() {
		return currentThread;
	}

	protected static final Log logger = LogFactory.getLog(ServiceThread.class);


	public ServiceThread(ServicesInvoker invoker, RenderWindowCommand renderCmd, ControllerContext context,
			Window window, String policyCtxId, 	Subject subject ,
	Principal principal ,
	Object credential, ITracker trackerService, Object trackerBean, PageProperties pageBean, IProfilerService profiler )
 {
		super();
		this.invoker = invoker;
		this.renderCmd = renderCmd;
		this.context = context;
		this.window = window;
		this.policyCtxId = policyCtxId;
		this.subject = subject;
		this.principal = principal;
		this.credential = credential;
		this.trackerService = trackerService;
		this.trackerBean = trackerBean;
		this.pageBean = pageBean;
		this.profiler = profiler;


	}

	public void run() {


			
		WindowRendition rendition = null;
		
		PolicyContext.setContextID( policyCtxId);
		SecurityAssociation.setSubject(subject);
		SecurityAssociation.setPrincipal(principal);
		SecurityAssociation.setCredential(credential);
		
		trackerService.createThreadContext(trackerBean);
		PageProperties.createThreadContext(pageBean);
		
		DynamicPortalObjectContainer.clearCache();
		
		UserTransaction tx = null;
		
		
		this.currentThread =  Thread.currentThread();
		invoker.registerThread(window.getId(),this);		
		

		boolean transactionBegin = false;
		
		try {
				

		         // setup the portal user information to be used by the CMS Business Layer
		         // for fine grained access control enforcement
			
				 // Le CMS est désactivé dans le PIA
		         //User user = (User)context.getServerInvocation().getAttribute(ServerInvocation.PRINCIPAL_SCOPE, UserInterceptor.USER_KEY);
		         //JCRCMS.getUserInfo().set(user);
			
			//logger.debug("debut thread "+ window.getName());
			
			
			InitialContext ctx = new InitialContext();
			tx = (UserTransaction) ctx.lookup("UserTransaction");
			
			
			if( tx.getStatus() == Status.STATUS_NO_TRANSACTION)	{
			
			try
			{
				tx.begin();
				transactionBegin = true;
			
			}catch( NotSupportedException e){
				// Ne doit pas arriver
				logger.error( "Erreur transaction status=" + tx.getStatus(), e );
			}
			}

			
			if( windowlogger.isDebugEnabled()){
				
				windowlogger.debug("-------------- DEBUT DUMP ServiceThread "+ context +" w:" + window.getId());
				

				HttpServletRequest request = context.getServerInvocation().getServerContext().getClientRequest();
				HttpSession session = context.getServerInvocation().getServerContext().getClientRequest().getSession(false);
	
				
				windowlogger.debug("request "+ request + " session " +session );
				/*

				NavigationalStateKey nsKey = new NavigationalStateKey(WindowNavigationalState.class, window.getId());
				
				Object attr = session.getAttribute("portal.principaladmin"+ window.getId());
				windowlogger.debug("attr " + window.getId() + " = " +attr );

				WindowNavigationalState ws = (WindowNavigationalState) context.getAttribute(
					ControllerCommand.NAVIGATIONAL_STATE_SCOPE, nsKey);
				
				if (ws != null) {

					windowlogger.debug("   window :" +window.getName());
					windowlogger.debug("      state :"  +ws.getWindowState());
					if( ws.getContentState() != null)	{
						if( ws.getContentState().decodeOpaqueValue(ws.getContentState().getStringValue()).size() > 0)
							windowlogger.debug("      content state :" + ws.getContentState());
					}
					if( ws.getPublicContentState() != null)	{
						if( ws.getPublicContentState().decodeOpaqueValue(ws.getPublicContentState().getStringValue()).size() > 0)
						windowlogger.debug("      public state :" + ws.getPublicContentState());
					}

				}		
				*/
				
				windowlogger.debug("-------------- FIN DUMP ServiceThread");
			}

			
			
			rendition = renderCmd.render(context);
			
			if( transactionBegin)

				tx.commit();

		} 
		
		catch (Exception e) {
			
			try{
				if( transactionBegin)
					tx.rollback();
			} catch(Exception e2)	{
				logger.error(e2);
			}

			boolean shouldLog = true;
			
			// Illegal State are due to session expiration
			// Should be catched at a pportlet level
			if (e instanceof IllegalStateException)    {
			    shouldLog = false;
			}
	        if (e instanceof PortletException)    {
               if( e.getCause() instanceof IllegalStateException)
                   shouldLog = false;
	        }
	        if(shouldLog)
	            logger.error(e);

		}
		

		finally {
			if( killerThread != null)
				killerThread.notifyEndThread();
		}
			
			//logger.debug("fin thread "+ window.getName());
			
	         // setup the portal user information to be used by the CMS Business Layer
	         // for fine grained access control enforcement
		
			 // Le CMS est désactivé dans le PIA			
			
	         //JCRCMS.getUserInfo().set(null);
	         
	        


		invoker.finRequete(window, rendition);

		//System.out.println("fin thread service ");

	}

}