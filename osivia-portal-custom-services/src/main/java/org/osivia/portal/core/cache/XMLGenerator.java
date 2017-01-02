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
package org.osivia.portal.core.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Export configuration utility.
 *
 * @author Cédric Krommenhoek
 * @see HttpServlet
 */
public class XMLGenerator {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal object container. */
    private final PortalObjectContainer portalObjectContainer;
    /** Dynamic object container. */
    private final IDynamicObjectContainer dynamicObjectContainer;
    /** Authorization domain registry. */
    private final AuthorizationDomainRegistry authorizationDomainRegistry;



    public XMLGenerator(PortalObjectContainer portalObjectContainer, IDynamicObjectContainer dynamicObjectContainer,
            AuthorizationDomainRegistry authorizationDomainRegistry) {
        super();
        this.portalObjectContainer = portalObjectContainer;
        this.dynamicObjectContainer = dynamicObjectContainer;
        this.authorizationDomainRegistry = authorizationDomainRegistry;
    }


    /**
     * {@inheritDoc}
     */
   

    /**
     * Getter for portalObjectContainer.
     *
     * @return the portalObjectContainer
     */
    private  PortalObjectContainer getPortalObjectContainer() {

        return portalObjectContainer;
    }


    /**
     * Getter for dynamicObjectContainer.
     *
     * @return the dynamicObjectContainer
     */
    private  IDynamicObjectContainer getDynamicObjectContainer() {

        return dynamicObjectContainer;
    }


    /**
     * Getter for authorizationDomainRegistry.
     *
     * @return the authorizationDomainRegistry
     */
    private  AuthorizationDomainRegistry getAuthorizationDomainRegistry() {

        return authorizationDomainRegistry;
    }


    /**
     * Utility method used to export config.
     *
     * @param output output stream
     * @param portalObject portal object to export
     * @param filter filter
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public void configExport(OutputStream output, PortalObject portalObject, String filter) throws ParserConfigurationException, TransformerException {
        String xalanIndentAmount = "{http://xml.apache.org/xslt}" + "indent-amount";

        Source source = new DOMSource(this.parametersGeneration(portalObject, filter));
        Result result = new StreamResult(output);

        // Transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(xalanIndentAmount, "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, result);
    }


    /**
     * Utility method used to generate export parameters.
     *
     * @param portalObject portal object to export
     * @param filter filter
     * @return DOM document
     * @throws ParserConfigurationException
     */
    private Document parametersGeneration(PortalObject portalObject, String filter) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // DOM properties
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        Element mainPortalObject;
        String parentRef = null;
        if (portalObject instanceof Page) {
            Page page = (Page) portalObject;
            mainPortalObject = this.pageExport(page, document, filter);
            parentRef = page.getId().getPath().getParent().toString(PortalObjectPath.LEGACY_FORMAT);
        } else {
            Portal portal = (Portal) portalObject;
            mainPortalObject = this.portalExport(portal, document, filter);
        }

        Element deployments = this.elementCreation(document, "deployments");

        Element deployment = this.elementCreation(document, "deployment");
        deployments.appendChild(deployment);

        if (parentRef == null) {
            deployment.appendChild(this.elementCreation(document, "parent-ref"));
        } else {
            deployment.appendChild(this.elementCreation(document, "parent-ref", parentRef));
        }

        deployment.appendChild(this.elementCreation(document, "if-exists", "overwrite"));

        deployment.appendChild(mainPortalObject);

        document.appendChild(deployments);

        return document;
    }


    /**
     * Utility method used to export page.
     *
     * @param page page to export
     * @param document DOM document
     * @param filter filter
     * @return DOM element
     */
    private Element pageExport(Page page, Document document, String filter) {
        return this.pageCreation(document, page, filter);
    }


    /**
     * Utility method used to create DOM page.
     *
     * @param document DOM document
     * @param page page to export
     * @param filter filter
     * @return DOM element
     */
    private Element pageCreation(Document document, Page page, String filter) {
        Element pageElement = this.elementCreation(document, "page");
        pageElement.appendChild(this.elementCreation(document, "page-name", page.getName()));

        // Display names
        LocalizedString displayName = page.getDisplayName();
        Map<Locale, Value> values = displayName.getValues();
        for (Entry<Locale, Value> entry : values.entrySet()) {
            Locale locale = entry.getKey();
            Value value = entry.getValue();
            Element displayNameElement = this.elementCreation(document, "display-name", value.getString());
            displayNameElement.setAttribute("xml:lang", locale.getLanguage());
            pageElement.appendChild(displayNameElement);
        }

        // Properties
        Element propertiesElement = this.elementCreation(document, "properties");
        Map<String, String> properties = page.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            Element propertyElement = this.elementCreation(document, "property");
            propertyElement.appendChild(this.elementCreation(document, "name", entry.getKey()));
            propertyElement.appendChild(this.elementCreation(document, "value", entry.getValue()));
            propertiesElement.appendChild(propertyElement);
        }
        pageElement.appendChild(propertiesElement);

        // Security constraint
        Element securityConstraint = this.securityConstraintCreation(document, page);
        pageElement.appendChild(securityConstraint);

        // Windows
        for (PortalObject child : page.getChildren()) {
            if (child instanceof Window) {
                pageElement.appendChild(this.windowCreation(document, (Window) child));
            }
        }

        // Sub pages
        for (PortalObject child : page.getChildren()) {
            if ((child instanceof Page) && ((!"true".equals(filter)) || (!"1".equals(((Page) child).getProperty("osivia.draftPage"))))) {
                pageElement.appendChild(this.pageCreation(document, (Page) child, filter));
            }
        }

        return pageElement;
    }


    /**
     * Utility method used to export portal.
     *
     * @param portal portal to export
     * @param document DOM document
     * @param filter filter
     * @return DOM element
     */
    private Element portalExport(Portal portal, Document document, String filter) {
        Element portalElement = this.elementCreation(document, "portal");
        portalElement.appendChild(this.elementCreation(document, "portal-name", portal.getName()));

        // Supported modes
        Element supportedModes = this.elementCreation(document, "supported-modes");
        supportedModes.appendChild(this.elementCreation(document, "mode", "view"));
        supportedModes.appendChild(this.elementCreation(document, "mode", "edit"));
        supportedModes.appendChild(this.elementCreation(document, "mode", "help"));
        portalElement.appendChild(supportedModes);

        // Supported window states
        Element supportedWindowStates = this.elementCreation(document, "supported-window-states");
        supportedWindowStates.appendChild(this.elementCreation(document, "window-state", "normal"));
        supportedWindowStates.appendChild(this.elementCreation(document, "window-state", "minimized"));
        supportedWindowStates.appendChild(this.elementCreation(document, "window-state", "maximized"));
        portalElement.appendChild(supportedWindowStates);

        // Security constraint
        Element securityConstraint = this.securityConstraintCreation(document, portal);
        portalElement.appendChild(securityConstraint);

        // Properties
        Element propertiesElement = this.elementCreation(document, "properties");
        Map<String, String> properties = portal.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            Element propertyElement = this.elementCreation(document, "property");
            propertyElement.appendChild(this.elementCreation(document, "name", entry.getKey()));
            propertyElement.appendChild(this.elementCreation(document, "value", entry.getValue()));
            propertiesElement.appendChild(propertyElement);
        }
        portalElement.appendChild(propertiesElement);

        // Sub pages
        for (PortalObject portalObject : portal.getChildren()) {
            if ((portalObject instanceof Page) && ((!"true".equals(filter)) || !"1".equals(((Page) portalObject).getProperty("osivia.draftPage")))) {
                portalElement.appendChild(this.pageCreation(document, (Page) portalObject, filter));
            }
        }

        return portalElement;
    }


    /**
     * Utility method used to create DOM element.
     *
     * @param document DOM document
     * @param name element name
     * @return DOM element
     */
    private Element elementCreation(Document document, String name) {
        return this.elementCreation(document, name, null);
    }


    /**
     * Utility method used to create DOM element.
     *
     * @param document DOM document
     * @param name element name
     * @param content element content, may be null
     * @return DOM element
     */
    private Element elementCreation(Document document, String name, String content) {
        Element element = document.createElement(name);
        if (content != null) {
            element.setTextContent(content);
        }
        return element;
    }


    /**
     * Utility method used to create security constraint.
     *
     * @param document DOM document
     * @param portalObject portal object to export
     * @return DOM element
     */
    @SuppressWarnings("unchecked")
    private Element securityConstraintCreation(Document document, PortalObject portalObject) {
        Element securityConstraint = this.elementCreation(document, "security-constraint");

        DomainConfigurator domainConfigurator = this.getAuthorizationDomainRegistry().getDomain("portalobject").getConfigurator();
        String id = portalObject.getId().toString(PortalObjectPath.CANONICAL_FORMAT);
        Set<RoleSecurityBinding> constraint = domainConfigurator.getSecurityBindings(id);

        for (RoleSecurityBinding roleSecurityBinding : constraint) {
            Set<String> actions = roleSecurityBinding.getActions();
            Element policyPermission = this.elementCreation(document, "policy-permission");

            for (String action : actions) {
                policyPermission.appendChild(this.elementCreation(document, "action-name", action));
            }

            String role = roleSecurityBinding.getRoleName();
            if (role.equals(SecurityConstants.UNCHECKED_ROLE_NAME)) {
                policyPermission.appendChild(this.elementCreation(document, "unchecked"));
            } else {
                Element roleElement = this.elementCreation(document, "role-name", roleSecurityBinding.getRoleName());
                policyPermission.appendChild(roleElement);
            }

            securityConstraint.appendChild(policyPermission);
        }

        return securityConstraint;
    }


    /**
     * Utility method used to create window.
     *
     * @param document DOM document
     * @param window window to export
     * @return DOM element
     */
    private Element windowCreation(Document document, Window window) {
        Element windowElement = this.elementCreation(document, "window");
        windowElement.appendChild(this.elementCreation(document, "window-name", window.getName()));
        windowElement.appendChild(this.elementCreation(document, "instance-ref", window.getContent().getURI()));

        // Properties
        Element propertiesElement = this.elementCreation(document, "properties");
        Map<String, String> properties = window.getDeclaredProperties();
        for (Entry<String, String> entry : properties.entrySet()) {
            if ("theme.region".equals(entry.getKey())) {
                windowElement.appendChild(this.elementCreation(document, "region", entry.getValue()));
            } else {
                // Other properties
                Element propertyElement = this.elementCreation(document, "property");
                propertyElement.appendChild(this.elementCreation(document, "name", entry.getKey()));
                propertyElement.appendChild(this.elementCreation(document, "value", entry.getValue()));
                propertiesElement.appendChild(propertyElement);
            }
        }
        windowElement.appendChild(propertiesElement);

        // Mandatory height
        windowElement.appendChild(this.elementCreation(document, "height", "0"));

        return windowElement;
    }

}
