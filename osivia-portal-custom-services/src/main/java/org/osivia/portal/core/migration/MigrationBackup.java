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
package org.osivia.portal.core.migration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Migration backup.
 */
public class MigrationBackup {

    /**
     * Default constructor.
     */
    public MigrationBackup() {
        super();
    }


    /**
     * Backup.
     *
     * @param moduleId module identifier
     * @return file
     * @throws Exception
     */
    public File backup(int moduleId) throws Exception {

        IDynamicObjectContainer dynamicObjectContainer = null;

        try {

            dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, "osivia:service=DynamicPortalObjectContainer");

            // ignore dynamic page and windows

            dynamicObjectContainer.startPersistentIteration();

            PortalObjectContainer portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, "portal:container=PortalObject");

            Context context;

            context = (Context) portalObjectContainer.getObject(PortalObjectId.parse("/", PortalObjectPath.CANONICAL_FORMAT));

            File tempFile = File.createTempFile("portal_parameters_before_migration_" + moduleId + "_" + System.currentTimeMillis(), ".xml");
            FileOutputStream fos = new FileOutputStream(tempFile);

            /* CReate the stream */

            this.exportConfig(fos, context);
            fos.close();

            return tempFile;

        } finally {

            if (dynamicObjectContainer != null) {
                dynamicObjectContainer.stopPersistentIteration();
            }

        }

    }


    /**
     * Export config.
     *
     * @param os output stream
     * @param context context
     * @throws Exception
     */
    public void exportConfig(OutputStream os, Context context) throws Exception {
        String XALAN_INDENT_AMOUNT = "{http://xml.apache.org/xslt}" + "indent-amount";

        // Création de la source DOM
        Source source = new DOMSource(this.genererParametres(context));

        Result resultat = new StreamResult(os);

        // Configuration du transformer
        TransformerFactory fabrique = TransformerFactory.newInstance();

        Transformer transformer = fabrique.newTransformer();
        transformer.setOutputProperty(XALAN_INDENT_AMOUNT, "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(source, resultat);
    }


    /**
     * Utility method used to generate document with parameters.
     *
     * @param context context
     * @return DOM4J document
     * @throws Exception
     */
    private Document genererParametres(Context context) throws Exception {

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
        Element deployments = this.creerElement(document, "deployments");


        // Add context
        Element contextElement = this.exportContext(context, document);

        Element contextDeployment = this.creerElement(document, "deployment");
        deployments.appendChild(contextDeployment);


        contextDeployment.appendChild(this.creerElement(document, "if-exists", "overwrite"));


        contextDeployment.appendChild(contextElement);


        // Add portals

        for (PortalObject portal : context.getChildren(PortalObject.PORTAL_MASK)) {

            Element portalElement = this.exportPortal((Portal) portal, document);

            Element portalDeployment = this.creerElement(document, "deployment");
            deployments.appendChild(portalDeployment);

            portalDeployment.appendChild(this.creerElement(document, "parent-ref"));

            portalDeployment.appendChild(this.creerElement(document, "if-exists", "overwrite"));

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
            // String xmlString = sw.toString();
            // System.out.println(xmlString);
        } catch (Exception e) {

        }

        // ----------------
        return document;

    }


    /**
     * Utility method used to create element with text content.
     *
     * @param document document
     * @param name element name
     * @param content element text content
     * @return DOM4J element
     */
    private Element creerElement(Document document, String name, String content) {
        Element element;

        element = document.createElement(name);
        if (content != null) {
            element.setTextContent(content);
        }

        return element;
    }


    /**
     * Utility method used to create element.
     *
     * @param document document
     * @param name element name
     * @return DOM4J element
     */
    private Element creerElement(Document document, String name) {
        return this.creerElement(document, name, null);
    }


    /**
     * Utility method used to export contextualized portal element.
     *
     * @param context context
     * @param document document
     * @return DOM4J element
     * @throws Exception
     */
    private Element exportContext(Context context, Document document) throws Exception {

        Element portalElement = this.creerElement(document, "context");

        portalElement.appendChild(this.creerElement(document, "context-name", context.getName()));

        Element propertiesElement = this.creerElement(document, "properties");
        Map<String, String> properties = context.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            Element propertyElement = this.creerElement(document, "property");
            propertyElement.appendChild(this.creerElement(document, "name", entry.getKey()));
            propertyElement.appendChild(this.creerElement(document, "value", entry.getValue()));
            propertiesElement.appendChild(propertyElement);
        }
        portalElement.appendChild(propertiesElement);

        return portalElement;
    }


    /**
     * Utility method used to export portal element.
     *
     * @param portal portal
     * @param document document
     * @return DOM4J element
     * @throws Exception
     */
    private Element exportPortal(Portal portal, Document document) throws Exception {

        Element portalElement = this.creerElement(document, "portal");

        portalElement.appendChild(this.creerElement(document, "portal-name", portal.getName()));

        Element supportedModes = this.creerElement(document, "supported-modes");
        supportedModes.appendChild(this.creerElement(document, "mode", "view"));
        supportedModes.appendChild(this.creerElement(document, "mode", "edit"));
        supportedModes.appendChild(this.creerElement(document, "mode", "help"));
        portalElement.appendChild(supportedModes);

        Element supportedWindowStates = this.creerElement(document, "supported-window-states");
        supportedWindowStates.appendChild(this.creerElement(document, "window-state", "normal"));
        supportedWindowStates.appendChild(this.creerElement(document, "window-state", "minimized"));
        supportedWindowStates.appendChild(this.creerElement(document, "window-state", "maximized"));
        portalElement.appendChild(supportedWindowStates);

        Element securityConstraint = this.creerSecurityConstraint(document, portal);

        portalElement.appendChild(securityConstraint);

        Element propertiesElement = this.creerElement(document, "properties");
        Map<String, String> properties = portal.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            Element propertyElement = this.creerElement(document, "property");
            propertyElement.appendChild(this.creerElement(document, "name", entry.getKey()));
            propertyElement.appendChild(this.creerElement(document, "value", entry.getValue()));
            propertiesElement.appendChild(propertyElement);
        }
        portalElement.appendChild(propertiesElement);

        for (PortalObject portalObject : portal.getChildren()) {
            if (portalObject instanceof Page) {
                portalElement.appendChild(this.creerPage(document, (Page) portalObject));
            }

        }

        return portalElement;
    }


    /**
     * Utility method used to create window element.
     *
     * @param document document
     * @param window window
     * @return DOM4J element
     */
    private Element creerWindow(Document document, Window window) {

        Element windowElement = this.creerElement(document, "window");

        windowElement.appendChild(this.creerElement(document, "window-name", window.getName()));
        if (window.getContent() != null) {
            windowElement.appendChild(this.creerElement(document, "instance-ref", window.getContent().getURI()));
        }

        Element propertiesElement = this.creerElement(document, "properties");
        Map<String, String> properties = window.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            if ("theme.region".equals(entry.getKey())) {
                windowElement.appendChild(this.creerElement(document, "region", entry.getValue()));
            } else {
                // Recopie des autres propriétés
                Element propertyElement = this.creerElement(document, "property");
                propertyElement.appendChild(this.creerElement(document, "name", entry.getKey()));
                propertyElement.appendChild(this.creerElement(document, "value", entry.getValue()));
                propertiesElement.appendChild(propertyElement);
            }
        }
        windowElement.appendChild(propertiesElement);

        // valeur height obligatoire
        windowElement.appendChild(this.creerElement(document, "height", "0"));

        return windowElement;
    }


    /**
     * Utility method used to create page element.
     *
     * @param document document
     * @param page page
     * @return DOM4 element
     * @throws Exception
     */
    private Element creerPage(Document document, Page page) throws Exception {
        Element pageElement = this.creerElement(document, "page");

        pageElement.appendChild(this.creerElement(document, "page-name", page.getName()));

        // Création des langues
        LocalizedString displayName = page.getDisplayName();
        Map<Locale, Value> values = displayName.getValues();
        for (Entry<Locale, Value> entry : values.entrySet()) {
            Locale locale = entry.getKey();
            Value value = entry.getValue();
            Element displayNameElement = this.creerElement(document, "display-name", value.getString());
            displayNameElement.setAttribute("xml:lang", locale.getLanguage());
            pageElement.appendChild(displayNameElement);
        }

        Map<String, String> properties = page.getDeclaredProperties();
        Element propertiesElement = this.creerElement(document, "properties");
        for (Entry<String, String> entry : properties.entrySet()) {
            Element propertyElement = this.creerElement(document, "property");
            propertyElement.appendChild(this.creerElement(document, "name", entry.getKey()));
            propertyElement.appendChild(this.creerElement(document, "value", entry.getValue()));
            propertiesElement.appendChild(propertyElement);
        }
        pageElement.appendChild(propertiesElement);

        Element securityConstraint = this.creerSecurityConstraint(document, page);
        pageElement.appendChild(securityConstraint);

        // Création des windows
        for (PortalObject child : page.getChildren()) {
            if (child instanceof Window) {
                pageElement.appendChild(this.creerWindow(document, (Window) child));
            }
        }

        // Création des sous-pages
        for (PortalObject child : page.getChildren()) {
            if (child instanceof Page) {

                pageElement.appendChild(this.creerPage(document, (Page) child));
            }
        }

        return pageElement;

    }


    /**
     * Utility method used to create security constraint element.
     *
     * @param document document
     * @param po portal object
     * @return DOM4J element
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Element creerSecurityConstraint(Document document, PortalObject po) throws Exception {

        AuthorizationDomainRegistry auth = Locator.findMBean(AuthorizationDomainRegistry.class, "portal:service=AuthorizationDomainRegistry");

        Element securityConstraint = this.creerElement(document, "security-constraint");

        DomainConfigurator dc = auth.getDomain("portalobject").getConfigurator();
        Set<RoleSecurityBinding> constraint = dc.getSecurityBindings(po.getId().toString(PortalObjectPath.CANONICAL_FORMAT));

        for (RoleSecurityBinding roleSecurityBinding : constraint) {
            Set<String> actions = roleSecurityBinding.getActions();
            Element policyPermission = this.creerElement(document, "policy-permission");

            for (String action : actions) {
                policyPermission.appendChild(this.creerElement(document, "action-name", action));
            }

            String role = roleSecurityBinding.getRoleName();
            if (role.equals(SecurityConstants.UNCHECKED_ROLE_NAME)) {
                policyPermission.appendChild(this.creerElement(document, "unchecked"));
            } else {
                Element roleElement = this.creerElement(document, "role-name", roleSecurityBinding.getRoleName());
                policyPermission.appendChild(roleElement);
            }

            securityConstraint.appendChild(policyPermission);
        }

        return securityConstraint;
    }

}
