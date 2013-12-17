/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
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
package org.jboss.portal.core.admin.ui;

import org.jboss.portal.Mode;
import org.jboss.portal.core.model.instance.Instance;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.NoSuchInstanceException;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.portlet.Portlet;
import org.jboss.portal.portlet.PortletInvokerException;
import org.jboss.portal.portlet.info.ModeInfo;
import org.jboss.portal.portlet.info.PortletInfo;
import org.jboss.portal.portlet.info.PreferenceInfo;
import org.jboss.portal.portlet.info.PreferencesInfo;
import org.jboss.portal.portlet.state.PropertyChange;
import org.jboss.portal.portlet.state.PropertyMap;
import org.jboss.portal.security.spi.provider.DomainConfigurator;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 12407 $
 */

/**
 * 
 * OSIVIA : Mise en place du multi-portails
 * @author jeanseb
 *
 */
public class InstanceManagerBean
{

   /** . */
   private final Mode INTERNAL_EDIT_CONTENT = Mode.create("edit_content");

   // Wired services

   /** . */
   private InstanceContainer instanceContainer;

   /** . */
   private RoleModule roleModule;

   /** . */
   private DomainConfigurator domainConfigurator;
   
   private PortalObjectContainer portalObjectContainer;

   // Navigational state of the user

   /** . */
   private String selectedId;

   /** . */
   private String selectedPlugin;

   /** . */
   private int selectedFrom;

   /** . */
   private Integer selectedRow;

   /** . */
   private int paginationSize;

   // Runtime fields depending on the navigational state

   /** . */
   private PreferencesBean selectedPrefs;

   /** . */
   private Instance selectedInstance;

   /** . */
   private List<InstanceDefinition> instances;

   /** . */
   private AuthorizationBean auth = new InstanceAuthorizationBean();

   // Services accessors

   public RoleModule getRoleModule()
   {
      return roleModule;
   }

   public void setRoleModule(RoleModule roleModule)
   {
      this.roleModule = roleModule;
   }

   public InstanceContainer getInstanceContainer()
   {
      return instanceContainer;
   }

   public void setInstanceContainer(InstanceContainer instanceContainer)
   {
      this.instanceContainer = instanceContainer;
   }

   public DomainConfigurator getDomainConfigurator()
   {
      return domainConfigurator;
   }

   public void setDomainConfigurator(DomainConfigurator domainConfigurator)
   {
      this.domainConfigurator = domainConfigurator;
   }
   

   public PortalObjectContainer getPortalObjectContainer()
   {
      return portalObjectContainer;
   }

   public void setPortalObjectContainer(PortalObjectContainer poc)
   {
      this.portalObjectContainer = poc;
   }

   // Navigational state accessor

   public int getPaginationSize()
   {
      return paginationSize;
   }

   public void setPaginationSize(int paginationSize)
   {
      this.paginationSize = paginationSize;
   }

   public int getSelectedFrom()
   {
      return selectedFrom;
   }

   public void setSelectedFrom(int selectedFrom)
   {
      this.selectedFrom = selectedFrom;
   }

   public Integer getSelectedRow()
   {
      return selectedRow;
   }

   public void setSelectedRow(Integer selectedRow)
   {
      this.selectedRow = selectedRow;
   }

   public String getSelectedId()
   {
      return selectedId;
   }

   public void setSelectedId(String selectedId)
   {
      this.selectedId = selectedId;
      int index = getInstances().indexOf(getSelectedInstance());
      if (index != -1)
      {
         selectedRow = index;
         selectedFrom = (index / paginationSize) * paginationSize;
      }
   }

   public String getSelectedPlugin()
   {
      return selectedPlugin;
   }

   public void setSelectedPlugin(String selectedPlugin)
   {
      this.selectedPlugin = selectedPlugin;
   }

   // Runtime fields

   public Instance getSelectedInstance()
   {
      if (selectedInstance == null && selectedId != null)
      {
         selectedInstance = instanceContainer.getDefinition(selectedId);
      }
      return selectedInstance;
   }

   public PreferencesBean getSelectedPrefs()
   {
      Instance selectedInstance = getSelectedInstance();

      //
      if (selectedPrefs == null && selectedInstance != null)
      {
         try
         {
            selectedPrefs = new PreferencesBean(true);

            //
            PreferencesInfo prefsInfo = selectedInstance.getPortlet().getInfo().getPreferences();
            Set keys = prefsInfo.getKeys();
            PropertyMap props = selectedInstance.getProperties();
            for (Object o : keys)
            {
               String key = (String)o;
               PreferenceInfo prefInfo = prefsInfo.getPreference(key);
               List<String> value = props.getProperty(key);
               selectedPrefs.addEntry(prefInfo, value);
            }
         }
         catch (PortletInvokerException e)
         {
            e.printStackTrace();
         }
      }

      //
      return selectedPrefs;
   }

   public AuthorizationBean getAuth()
   {
      return auth;
   }

   private List<InstanceDefinition> getInstances()
   {
      if (instances == null)
      {
         // Remove content editors
         List<InstanceDefinition> tmpInstances = new ArrayList<InstanceDefinition>();
         nextInstance:
         for (InstanceDefinition instance : instanceContainer.getDefinitions())
         {
            //
            try
            {
               // Filter portlets that are editors 
               Portlet portlet = instance.getPortlet();
               PortletInfo info = portlet.getInfo();
               for (ModeInfo modeInfo : info.getCapabilities().getAllModes())
               {
                  if (modeInfo.getMode().equals(INTERNAL_EDIT_CONTENT))
                  {
                     continue nextInstance;
                  }
               }
               //
               tmpInstances.add(instance);
            }
            catch (PortletInvokerException ignore)
            {
            }
         }
         Collections.sort(tmpInstances, INSTANCE_COMPARATOR);
         instances = tmpInstances;
      }
      //
      return instances;
   }

   public Collection<InstanceDefinition> getSelectedInstances()
   {
      List<InstanceDefinition> list = getInstances();

      //
      int to = Math.min(selectedFrom + paginationSize, list.size());

      //
      return list.subList(selectedFrom, to);
   }

   public Map getSelectedInstancesPrefs()
   {
      Map<String, PropertyMap> map = new HashMap<String, PropertyMap>();
      Collection<InstanceDefinition> instances = getSelectedInstances();

      for (Instance instance : instances)
      {
         InstanceDefinition instanceDef = (InstanceDefinition)instance;
         try
         {
            map.put(instanceDef.getId(), instanceDef.getProperties());
         }
         catch (PortletInvokerException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }

      return map;
   }

   public int getInstanceCount()
   {
      return getInstances().size();
   }

   /** Refresh the selected prefs. */
   public void refresh()
   {
      selectedInstance = null;
      instances = null;
   }

   // UI operations

   public void selectFrom()
   {
      Map pmap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      selectedFrom = Integer.parseInt((String)pmap.get("from"));
      selectedPlugin = null;
      selectedId = null;
   }

   public void selectPlugin()
   {
      Map pmap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      selectedPlugin = (String)pmap.get("plugin");
   }

   public void selectInstance()
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ExternalContext ectx = ctx.getExternalContext();
      Map params = ectx.getRequestParameterMap();
      selectedId = (String)params.get("id");
      selectedPlugin = (String)params.get("plugin");
      selectedPrefs = null;
   }

   public void selectInstance(ActionEvent e)
   {
      selectInstance();
   }


   public void deleteInstance(ActionEvent ae)
   {
      try
      {
         String id = (String)ae.getComponent().getAttributes().get("instanceId");

         if (id != null)
         {
            //
            instanceContainer.destroyDefinition(id);

            //
            selectedId = null;
            selectedPrefs = null;
            selectedFrom = 0;
            selectedPlugin = null;
         }
      }
      catch (NoSuchInstanceException e)
      {
         e.printStackTrace();
      }
      catch (PortletInvokerException e)
      {
         e.printStackTrace();
      }
   }

   public void updatePrefs()
   {
      try
      {
         List<PropertyChange> tmp = new ArrayList<PropertyChange>();
         List entries = selectedPrefs.getEntries();
         for (Object entry1 : entries)
         {
            PreferenceBean entry = (PreferenceBean)entry1;
            if (entry.isStale())
            {
               PropertyChange change = PropertyChange.newUpdate(entry.getName(), entry.getValue());
               tmp.add(change);
            }
         }
         PropertyChange[] changes = tmp.toArray(new PropertyChange[tmp.size()]);
         getSelectedInstance().setProperties(changes);

         // Todo handle that change was ok in the UI
      }
      catch (PortletInvokerException e)
      {
         // Todo handle issue in the UI
         e.printStackTrace();
      }

      //
      selectedId = null;
      selectedPrefs = null;
      selectedPlugin = null;
   }

   public void cancelPrefs()
   {
      selectedId = null;
      selectedPrefs = null;
      selectedPlugin = null;
   }

   /** A comparator for portlets. */
   static final Comparator<Instance> INSTANCE_COMPARATOR = new Comparator<Instance>()
   {
      public int compare(Instance i1, Instance i2)
      {
         return i1.getId().compareToIgnoreCase(i2.getId());
      }
   };

   public class InstanceAuthorizationBean extends MultiPortalsAuthorizationBean
   {

      public DomainConfigurator getDomainConfigurator()
      {
         return InstanceManagerBean.this.getDomainConfigurator();
      }

      protected String getURI()
      {
         Instance instance = getSelectedInstance();
         if (instance != null)
         {
            return instance.getId();
         }
         else
         {
            return null;
         }
      }
      
	   // Get current default portal for roles
	   public String getManagedPortalName()	{

      
		   Portal portal = (Portal)portalObjectContainer.getContext().getDefaultPortal();
		   
		   return portal.getName();
	   }

      public String submit()
      {
         String stringMessage = "Security has been correctly updated";
         FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, stringMessage, stringMessage);
         FacesContext.getCurrentInstance().addMessage("status", message);
         selectedId = null;
         selectedPlugin = null;
         return null;
      }

      public String cancel()
      {
         selectedId = null;
         selectedPlugin = null;
         return null;
      }

      public RoleModule getRoleModule()
      {
         return roleModule;
      }

      public SelectItem[] getAvailableActions()
      {
         return new SelectItem[]{new SelectItem("view", "View"), new SelectItem("admin", "Admin")};
      }
   }
}
