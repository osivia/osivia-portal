package org.osivia.portal.administration.ejb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.security.AuthorizationDomainRegistry;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DumpServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static String EXPORT_PORTALNAME_SESSION = "pia.export.config";

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException,
			IOException {
		
		if( ! FileUploadBean.checkAdminPrivileges(request))	{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		response.setContentType("text/plain");

		String fileName = "dump_" + System.currentTimeMillis() + ".txt";

		response.addHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");

		/* CReate the stream */


		PrintWriter out = new PrintWriter (response.getOutputStream());

		dumpThread(out);
		out.flush();
		out.close();

	}

	public void dumpThread(PrintWriter os) {

		// Walk up all the way to the root thread group
		ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
		ThreadGroup parent;
		while ((parent = rootGroup.getParent()) != null) {
			rootGroup = parent;
		}

		listThreads(os, rootGroup);
	}

	// List all threads and recursively list all subgroup
	public void listThreads(PrintWriter os, ThreadGroup group) {
		//os.println(indent + "Group[" + group.getName() + ":" + group.getClass() + "]");
		int nt = group.activeCount();
		Thread[] threads = new Thread[nt * 2 + 10]; // nt is not accurate
		nt = group.enumerate(threads, false);

		// List every thread in the group
		for (int i = 0; i < nt; i++) {
			Thread t = threads[i];
			os.println( "Thread[" + t.getName() + ":" + t.getClass() + "]");


			StackTraceElement[] stack = t.getStackTrace();
			for (int j = 0; j < stack.length; j++) {
				os.println(  "   " + stack[j]);
			}
			
			os.println( "       ---------------");

		}

		// Recursively list all subgroups
		int ng = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[ng * 2 + 10];
		ng = group.enumerate(groups, false);

		for (int i = 0; i < ng; i++) {
			listThreads(os, groups[i]);
		}
	}

}
