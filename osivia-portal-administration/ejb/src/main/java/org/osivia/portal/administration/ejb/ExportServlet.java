package org.osivia.portal.administration.ejb;

import java.io.OutputStream;
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


public class ExportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static String EXPORT_PORTALNAME_SESSION = "osivia.export.config";

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException {
		
		if( ! FileUploadBean.checkAdminPrivileges(request))	{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		IDynamicObjectContainer dynamicObjectContainer = null;

		UserTransaction tx = null;
		// As we are in servlet, cache must explicitly initialized

		try {

			dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class,
					"osivia:service=DynamicPortalObjectContainer");

			// ignore dynamic windows

			dynamicObjectContainer.startPersistentIteration();

			PortalObjectContainer portalObjectContainer = Locator.findMBean(PortalObjectContainer.class,
					"portal:container=PortalObject");

			/* Create the transaction */

			InitialContext ctx = new InitialContext();
			tx = (UserTransaction) ctx.lookup("UserTransaction");
			tx.begin();

			/* Read the portal object */

			String pageId = request.getParameter("pageId");
			String filter = request.getParameter("filter");


			String portalName = (String) request.getSession().getAttribute(EXPORT_PORTALNAME_SESSION);

			PortalObject po;

			if (pageId != null)
				po = portalObjectContainer.getObject(PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT));
			else
				po = portalObjectContainer.getObject(PortalObjectId.parse("/" + portalName,
						PortalObjectPath.CANONICAL_FORMAT));

			if (po != null) {

				response.setContentType("text/xml");

				String fileName = "export_";

				if (po instanceof Page)
					fileName += "page_";
				else
					fileName += "portal_";

				fileName += po.getName().toLowerCase() + ".xml";

				response.addHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");

				/* CReate the stream */

				ServletOutputStream os = response.getOutputStream();
				exportConfig(os, po, filter);
				os.flush();
				os.close();

			}
		} catch (Exception e) {
			throw new ServletException(e);
		}

		finally {

			try {
				if (tx != null)
					tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (dynamicObjectContainer != null)
				dynamicObjectContainer.stopPersistentIteration();

		}

	}

	public void exportConfig(OutputStream os, PortalObject po, String filter) throws DOMException, Exception {
		String XALAN_INDENT_AMOUNT = "{http://xml.apache.org/xslt}" + "indent-amount";

		// Création de la source DOM
		Source source = new DOMSource(genererParametres(po, filter));

		Result resultat = new StreamResult(os);

		// Configuration du transformer
		TransformerFactory fabrique = TransformerFactory.newInstance();

		Transformer transformer = fabrique.newTransformer();
		transformer.setOutputProperty(XALAN_INDENT_AMOUNT, "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		// transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"http://www.jboss.org/portal/dtd/portal-object_2_6.dtd");
		transformer.transform(source, resultat);

	}

	private Document genererParametres(PortalObject po, String filter) throws DOMException, Exception {

		// Création d'une fabrique de documents
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

		// création d'un constructeur de documents
		DocumentBuilder constructeur = fabrique.newDocumentBuilder();

		Document document = constructeur.newDocument();

		// Propriétés du DOM
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		// document.

		Element mainPortalObject;
		String parentRef = null;

		if (po instanceof Page) {

			mainPortalObject = exportPage((Page) po, document, filter);
			parentRef = po.getId().getPath().getParent().toString(PortalObjectPath.LEGACY_FORMAT);
		} else {

			mainPortalObject = exportPortal((Portal) po, document, filter);
		}

		// Création de l'arborescence du DOM
		Element deployments = creerElement(document, "deployments");
		// racine.appendChild(document.createComment("Commentaire sous la racine"));

		Element deployment = creerElement(document, "deployment");
		deployments.appendChild(deployment);

		if (parentRef == null)
			deployment.appendChild(creerElement(document, "parent-ref"));
		else
			deployment.appendChild(creerElement(document, "parent-ref", parentRef));

		deployment.appendChild(creerElement(document, "if-exists", "overwrite"));

		// deployment.setAttribute("id","0");

		deployment.appendChild(mainPortalObject);

		document.appendChild(deployments);

		// ------------------
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			transformer.transform(source, result);
			String xmlString = sw.toString();
			System.out.println(xmlString);
		} catch (Exception e) {

		}

		// ----------------
		return document;

	}

	private Element creerElement(Document document, String name, String content) {
		Element element;

		element = document.createElement(name);
		if (content != null)
			element.setTextContent(content);

		return element;
	}

	private Element creerElement(Document document, String name) {

		return creerElement(document, name, null);
	}

	@SuppressWarnings("unchecked")
	private Element exportPortal(Portal portal, Document document, String filter) throws Exception {

		Element portalElement = creerElement(document, "portal");

		portalElement.appendChild(creerElement(document, "portal-name", portal.getName()));

		Element supportedModes = creerElement(document, "supported-modes");
		supportedModes.appendChild(creerElement(document, "mode", "view"));
		supportedModes.appendChild(creerElement(document, "mode", "edit"));
		supportedModes.appendChild(creerElement(document, "mode", "help"));
		portalElement.appendChild(supportedModes);

		Element supportedWindowStates = creerElement(document, "supported-window-states");
		supportedWindowStates.appendChild(creerElement(document, "window-state", "normal"));
		supportedWindowStates.appendChild(creerElement(document, "window-state", "minimized"));
		supportedWindowStates.appendChild(creerElement(document, "window-state", "maximized"));
		portalElement.appendChild(supportedWindowStates);

		Element securityConstraint = creerSecurityConstraint(document, portal);

		portalElement.appendChild(securityConstraint);

		Element propertiesElement = creerElement(document, "properties");
		Map<String, String> properties = portal.getDeclaredProperties();
		for (String name : properties.keySet()) {
			Element propertyElement = creerElement(document, "property");
			propertyElement.appendChild(creerElement(document, "name", name));
			propertyElement.appendChild(creerElement(document, "value", properties.get(name)));
			propertiesElement.appendChild(propertyElement);
		}
		portalElement.appendChild(propertiesElement);

		for (PortalObject portalObject : portal.getChildren()) {
			if (portalObject instanceof Page && ( (! "true".equals(filter)) || ! "1".equals( ((Page) portalObject).getProperty("osivia.draftPage")) ))
				portalElement.appendChild(creerPage(document, (Page) portalObject, filter));

		}

		return portalElement;
	}

	@SuppressWarnings("unchecked")
	private Element exportPage(Page page, Document document, String filter) throws Exception {

		return creerPage(document, (Page) page, filter);

	}

	private Element creerWindow(Document document, Window window) {

		Element windowElement = creerElement(document, "window");

		windowElement.appendChild(creerElement(document, "window-name", window.getName()));
		windowElement.appendChild(creerElement(document, "instance-ref", window.getContent().getURI()));

		Element propertiesElement = creerElement(document, "properties");
		Map<String, String> properties = window.getDeclaredProperties();
		for (String name : properties.keySet()) {

			if (name.equals("theme.region")) {
				windowElement.appendChild(creerElement(document, "region", properties.get(name)));
			} else {
				// Recopie des autres propriétés
				Element propertyElement = creerElement(document, "property");
				propertyElement.appendChild(creerElement(document, "name", name));
				propertyElement.appendChild(creerElement(document, "value", properties.get(name)));
				propertiesElement.appendChild(propertyElement);
			}
		}
		windowElement.appendChild(propertiesElement);

		// valeur height obligatoire
		windowElement.appendChild(creerElement(document, "height", "0"));

		return windowElement;
	}

	private Element creerPage(Document document, Page page, String filter) throws Exception {

		Element pageElement = creerElement(document, "page");

		pageElement.appendChild(creerElement(document, "page-name", page.getName()));

		// Création des langues

		LocalizedString displayName = page.getDisplayName();
		Map<Locale, Value> values = displayName.getValues();
		for (Locale locale : values.keySet()) {
			Value value = values.get(locale);
			Element displayNameElement = creerElement(document, "display-name", value.getString());
			displayNameElement.setAttribute("xml:lang", locale.getLanguage());
			pageElement.appendChild(displayNameElement);
		}

		Map<String, String> properties = page.getDeclaredProperties();

		Element propertiesElement = creerElement(document, "properties");
		for (String name : properties.keySet()) {
			Element propertyElement = creerElement(document, "property");
			propertyElement.appendChild(creerElement(document, "name", name));
			propertyElement.appendChild(creerElement(document, "value", properties.get(name)));
			propertiesElement.appendChild(propertyElement);
		}
		pageElement.appendChild(propertiesElement);

		Element securityConstraint = creerSecurityConstraint(document, page);
		pageElement.appendChild(securityConstraint);

		// Création des windows

		for (PortalObject child : page.getChildren()) {
			if (child instanceof Window) {
				pageElement.appendChild(creerWindow(document, (Window) child));
			}
		}

		// Création des sous-pages

		for (PortalObject child : page.getChildren()) {
			if (child instanceof Page   && ((! "true".equals(filter)) || ( ! "1".equals( ((Page) child).getProperty("osivia.draftPage")))  )) {
				
				pageElement.appendChild(creerPage(document, (Page) child, filter));
			}
		}

		return pageElement;

	}

	private Element creerSecurityConstraint(Document document, PortalObject po) throws Exception {

		AuthorizationDomainRegistry auth = Locator.findMBean(AuthorizationDomainRegistry.class,
				"portal:service=AuthorizationDomainRegistry");

		Element securityConstraint = creerElement(document, "security-constraint");

		DomainConfigurator dc = auth.getDomain("portalobject").getConfigurator();
		Set<RoleSecurityBinding> constraint = dc.getSecurityBindings(po.getId().toString(
				PortalObjectPath.CANONICAL_FORMAT));

		for (RoleSecurityBinding roleSecurityBinding : constraint) {
			Set<String> actions = roleSecurityBinding.getActions();
			Element policyPermission = creerElement(document, "policy-permission");

			for (String action : actions) {
				policyPermission.appendChild(creerElement(document, "action-name", action));
			}

			String role = roleSecurityBinding.getRoleName();
			if (role.equals(SecurityConstants.UNCHECKED_ROLE_NAME)) {
				policyPermission.appendChild(creerElement(document, "unchecked"));
			} else {
				Element roleElement = creerElement(document, "role-name", roleSecurityBinding.getRoleName());
				policyPermission.appendChild(roleElement);
			}

			securityConstraint.appendChild(policyPermission);
		}

		return securityConstraint;
	}

}
