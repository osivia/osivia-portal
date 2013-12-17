/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/

package org.jboss.portal.core.admin.ui.actions;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.common.util.ParameterValidation;
import org.jboss.portal.core.admin.ui.PortalObjectManagerBean;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalContainer;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.PortalObjectPermission;
import org.jboss.portal.faces.gui.ManagedBean;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.security.spi.provider.DomainConfigurator;
import org.jboss.portal.theme.ThemeConstants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 JSSTEUX : ajout du mode ADMIN par défaut
 */
public class PortalAction extends ManagedBean
{

   /** . */
   private PortalObjectManagerBean pomgr;

   /** . */
   private String portalName;

   private static final String MESSAGE_TARGET = "create-portal-form:name";
   protected static final String PORTAL_TYPE = "PORTAL_TYPE";

   public PortalObjectManagerBean getPortalObjectManager()
   {
      return pomgr;
   }

   public void setPortalObjectManager(PortalObjectManagerBean portalObjectManager)
   {
      this.pomgr = portalObjectManager;
   }

   public String getPortalName()
   {
      return portalName;
   }

   public void setPortalName(String portalName)
   {
      this.portalName = portalName;
   }

   public String getDefaultObjectName()
   {
      return pomgr.getSelectedObject().getDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME);
   }

   public void setDefaultObjectName(String defaultObjectName)
   {
      if (!ParameterValidation.isNullOrEmpty(defaultObjectName))
      {
         pomgr.getSelectedObject().setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, defaultObjectName);
      }
      else
      {
         pomgr.getSelectedObject().setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, null);
      }

   }

   public void addPortal()
   {
      try
      {
         PortalContainer portalContainer = (PortalContainer)pomgr.getSelectedObject();

         portalName = checkNameValidity(portalName, MESSAGE_TARGET);
         if (portalName != null)
         {
            Portal portal = portalContainer.createPortal(portalName);
            DomainConfigurator configurator = pomgr.getDomainConfigurator();

            // Initial portal permissions
            Set actions = new HashSet();
            actions.add(PortalObjectPermission.VIEW_RECURSIVE_ACTION);
            actions.add(PortalObjectPermission.PERSONALIZE_RECURSIVE_ACTION);
            RoleSecurityBinding binding = new RoleSecurityBinding(actions, SecurityConstants.UNCHECKED_ROLE_NAME);
            Set constraints = Collections.singleton(binding);
            configurator.setSecurityBindings(portal.getId().toString(PortalObjectPath.CANONICAL_FORMAT), constraints);

            // We need to add initial layout sets to avoid problems...
            portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_LAYOUT, "generic");
            portal.setDeclaredProperty(ThemeConstants.PORTAL_PROP_RENDERSET, "divRenderer");

            //
            portal.getSupportedWindowStates().add(WindowState.MAXIMIZED);
            portal.getSupportedWindowStates().add(WindowState.MINIMIZED);
            portal.getSupportedWindowStates().add(WindowState.NORMAL);

            //
            portal.getSupportedModes().add(Mode.EDIT);
            portal.getSupportedModes().add(Mode.HELP);
            portal.getSupportedModes().add(Mode.VIEW);
            portal.getSupportedModes().add(Mode.ADMIN);

            // Create the default page
            Page page = portal.createPage("default");
            constraints = Collections.singleton(new RoleSecurityBinding(PortalObjectPermission.VIEW_RECURSIVE_ACTION, SecurityConstants.UNCHECKED_ROLE_NAME));
            configurator.setSecurityBindings(page.getId().toString(PortalObjectPath.CANONICAL_FORMAT), constraints);

            portal.setDeclaredProperty(PortalObject.PORTAL_PROP_DEFAULT_OBJECT_NAME, page.getName());
         }
      }
      catch (Exception e)
      {
 //        log.error("An error occurred during portal creation.", e);
         beanContext.createErrorMessageFrom(MESSAGE_TARGET, e);
      }
   }

   protected String getObjectTypeName()
   {
      return PORTAL_TYPE;
   }

   public boolean isAlreadyExisting(String objectName)
   {
      return pomgr.getSelectedObject().getChild(objectName) != null;
   }
}
