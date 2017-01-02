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
package org.osivia.portal.administration.ejb;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.apache.commons.lang.StringUtils;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.osivia.portal.administration.util.AdministrationConstants;
import org.osivia.portal.administration.util.AdministrationUtils;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cache.global.ICacheService;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

/**
 * Export servlet.
 *
 * @author Cédric Krommenhoek
 * @see HttpServlet
 */
public class ExportServlet extends HttpServlet {

    /** Default serial version ID. */
    private static final long serialVersionUID = 1L;

    /** Portal object container. */
    private static PortalObjectContainer portalObjectContainer;
    /** Dynamic object container. */
    private static IDynamicObjectContainer dynamicObjectContainer;
    /** Cache service */
    private static ICacheService cacheService;


    /**
     * Default constructor.
     */
    public ExportServlet() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check administrator privileges
        if (!AdministrationUtils.checkAdminPrivileges(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        UserTransaction transaction = null;
        try {
            this.getDynamicObjectContainer().startPersistentIteration();

            // Create the transaction
            InitialContext initialContext = new InitialContext();
            transaction = (UserTransaction) initialContext.lookup("UserTransaction");
            transaction.begin();

            String pageId = request.getParameter(AdministrationConstants.PAGE_ID_PARAMETER_NAME);
            String filter = request.getParameter(AdministrationConstants.EXPORT_FILTER_PARAMETER_NAME);

            String portalId = (String) request.getSession().getAttribute(AdministrationConstants.PORTAL_ID_ATTRIBUTE_NAME);

            PortalObject portalObject;
            if (StringUtils.isNotBlank(pageId)) {
                portalObject = this.getPortalObjectContainer().getObject(PortalObjectId.parse(pageId, PortalObjectPath.SAFEST_FORMAT));
            } else {
                portalObject = this.getPortalObjectContainer().getObject(PortalObjectId.parse(portalId, PortalObjectPath.SAFEST_FORMAT));
            }

            if (portalObject != null) {
                response.setContentType("text/xml");

                // Header
                StringBuffer headerValue = new StringBuffer();
                headerValue.append("attachment; filename=\"export_");
                if (portalObject instanceof Page) {
                    headerValue.append("page_");
                } else {
                    headerValue.append("portal_");
                }
                headerValue.append(portalObject.getName().toLowerCase());
                headerValue.append(".xml\"");
                response.addHeader("Content-disposition", headerValue.toString());

                // Stream creation
                ServletOutputStream output = response.getOutputStream();
                getCacheService().configExport(output, portalObject, filter);
                output.flush();
                output.close();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            try {
                // Commit
                if (transaction != null) {
                    transaction.commit();
                }
            } catch (Exception e) {
                throw new ServletException(e);
            } finally {
                this.getDynamicObjectContainer().stopPersistentIteration();
            }
        }
    }


    /**
     * Getter for portalObjectContainer.
     *
     * @return the portalObjectContainer
     */
    private synchronized PortalObjectContainer getPortalObjectContainer() {
        if (portalObjectContainer == null) {
            String portalObjectContainerName = this.getInitParameter(AdministrationConstants.PORTAL_OBJECT_CONTAINER_NAME);
            portalObjectContainer = Locator.findMBean(PortalObjectContainer.class, portalObjectContainerName);
        }
        return portalObjectContainer;
    }


    /**
     * Getter for dynamicObjectContainer.
     *
     * @return the dynamicObjectContainer
     */
    private synchronized IDynamicObjectContainer getDynamicObjectContainer() {
        if (dynamicObjectContainer == null) {
            String dynamicObjectContainerName = this.getInitParameter(AdministrationConstants.DYNAMIC_OBJECT_CONTAINER_NAME);
            dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, dynamicObjectContainerName);
        }
        return dynamicObjectContainer;
    }


    /**
     * Getter for authorizationDomainRegistry.
     *
     * @return the authorizationDomainRegistry
     */
    private synchronized ICacheService getCacheService() {
        if (cacheService == null) {
            String clusterCache = this.getInitParameter(AdministrationConstants.CLUSTER_CACHE_SERVICE_NAME);
            cacheService = Locator.findMBean(ICacheService.class, clusterCache);
        }
        return cacheService;
    }




}
