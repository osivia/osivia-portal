package org.osivia.portal.core.migration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.jboss.portal.core.model.portal.Context;
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

public class MigrationBackup {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public File backup(int moduleId) throws Exception {

		IDynamicObjectContainer dynamicObjectContainer = null;

		try {

			dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");

			// ignore dynamic page and windows

			dynamicObjectContainer.startPersistentIteration();

			PortalObjectContainer portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");

			Context context;

			context = (Context) portalObjectContainer.getObject(PortalObjectId.parse("/", PortalObjectPath.CANONICAL_FORMAT));

			File tempFile = File.createTempFile("portal_parameters_before_migration_" + moduleId + "_"+System.currentTimeMillis(), ".xml");
			FileOutputStream fos = new FileOutputStream(tempFile);

			/* CReate the stream */

			exportConfig(fos, context);
			fos.close();

			return tempFile;

		}

		finally {

			if (dynamicObjectContainer != null)
				dynamicObjectContainer.stopPersistentIteration();

		}

	}

	public void exportConfig(OutputStream os, Context context) throws DOMException, Exception {
		String XALAN_INDENT_AMOUNT = "{http://xml.apache.org/xslt}" + "indent-amount";

		// Création de la source DOM

		Source source = new DOMSource(genererParametres(context));

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

	private Document genererParametres(Context context) throws DOMException, Exception {

		// Création d'une fabrique de documents
		DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

		// création d'un constructeur de documents
		DocumentBuilder constructeur = fabrique.newDocumentBuilder();

		Document document = constructeur.newDocument();

		// Propriétés du DOM
		document.setXmlVersion("1.0");
		document.setXmlStandalone(true);

		// document.

		

		// Création de l'arborescence du DOM
		Element deployments = creerElement(document, "deployments");
		
		
		// Add context
		Element contextElement = exportContext(context, document);

		Element contextDeployment = creerElement(document, "deployment");
		deployments.appendChild(contextDeployment);
		

		contextDeployment.appendChild(creerElement(document, "if-exists", "overwrite"));

		
		
		contextDeployment.appendChild(contextElement);


		
		// Add portals

		for (PortalObject portal : context.getChildren(PortalObject.PORTAL_MASK)) {

			Element portalElement = exportPortal((Portal) portal, document);

			Element portalDeployment = creerElement(document, "deployment");
			deployments.appendChild(portalDeployment);

			portalDeployment.appendChild(creerElement(document, "parent-ref"));

			portalDeployment.appendChild(creerElement(document, "if-exists", "overwrite"));

			// deployment.setAttribute("id","0");

			portalDeployment.appendChild(portalElement);
		}

		document.appendChild(deployments);

		// ------------------
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			transformer.transform(source, result);
//			String xmlString = sw.toString();
//			System.out.println(xmlString);
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

	
	private Element exportContext(Context context, Document document) throws Exception {

		Element portalElement = creerElement(document, "context");

		portalElement.appendChild(creerElement(document, "context-name", context.getName()));

		Element propertiesElement = creerElement(document, "properties");
		Map<String, String> properties = context.getDeclaredProperties();
		for (String name : properties.keySet()) {
			Element propertyElement = creerElement(document, "property");
			propertyElement.appendChild(creerElement(document, "name", name));
			propertyElement.appendChild(creerElement(document, "value", properties.get(name)));
			propertiesElement.appendChild(propertyElement);
		}
		portalElement.appendChild(propertiesElement);

	
		return portalElement;
	}

	
	@SuppressWarnings("unchecked")
	private Element exportPortal(Portal portal, Document document) throws Exception {

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
			if (portalObject instanceof Page)
				portalElement.appendChild(creerPage(document, (Page) portalObject));

		}

		return portalElement;
	}

	@SuppressWarnings("unchecked")
	private Element exportPage(Page page, Document document) throws Exception {

		return creerPage(document, (Page) page);

	}

	private Element creerWindow(Document document, Window window) {

		Element windowElement = creerElement(document, "window");

		windowElement.appendChild(creerElement(document, "window-name", window.getName()));
		if( window.getContent() != null)
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

	private Element creerPage(Document document, Page page) throws Exception {

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
			if (child instanceof Page) {

				pageElement.appendChild(creerPage(document, (Page) child));
			}
		}

		return pageElement;

	}

	private Element creerSecurityConstraint(Document document, PortalObject po) throws Exception {

		AuthorizationDomainRegistry auth = Locator.findMBean(AuthorizationDomainRegistry.class,
				"portal:service=AuthorizationDomainRegistry");

		Element securityConstraint = creerElement(document, "security-constraint");

		DomainConfigurator dc = auth.getDomain("portalobject").getConfigurator();
		Set<RoleSecurityBinding> constraint = dc.getSecurityBindings(po.getId().toString(PortalObjectPath.CANONICAL_FORMAT));

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
