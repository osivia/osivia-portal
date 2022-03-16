package org.osivia.portal.core.instances;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceDefinition;
import org.jboss.portal.core.impl.model.instance.InstanceContainerContext;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.jems.hibernate.ContextObject;
import org.jboss.portal.security.RoleSecurityBinding;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Instance definition implementation
 */

class InstanceDefinitionImpl extends AbstractInstanceDefinition implements ContextObject
{

    
   // Persistent fields

   protected Long key;
   protected String instanceId;
   protected boolean mutable;
   protected Map relatedSecurityBindings;
   protected Map relatedCustomizations;
   protected Map<Locale, String> displayNames;

   // Runtime fields

   /** . */
   protected InstanceContainerContext containerContext;

   public InstanceDefinitionImpl()
   {
      this.mutable = false;
      this.portletRef = null;
      this.instanceId = null;
      this.relatedSecurityBindings = null;
      this.relatedCustomizations = null;
      this.state = null;
   }

   public InstanceDefinitionImpl(InstanceContainerContext containerContext, String id, String portletRef)
   {
      this.containerContext = containerContext;
      this.mutable = false;
      this.portletRef = portletRef;
      this.instanceId = id;
      this.relatedSecurityBindings = new HashMap();
      this.relatedCustomizations = new HashMap();
      this.displayNames = new HashMap();
      this.state = null;
   }

   public InstanceDefinitionImpl(InstanceContainerContext containerContext, InstanceMetaData instanceMD)
   {
      this.containerContext = containerContext;
      this.mutable = false;
      this.portletRef = instanceMD.getPortletRef();
      this.instanceId = instanceMD.getId();
      this.displayNames = getDisplayNamesMap(instanceMD.getDisplayName());
      this.relatedSecurityBindings = new HashMap();
      this.relatedCustomizations = instanceMD.getPreferences().getPortletPreferences();
      this.state = null;
   }

   private Map<Locale, String> getDisplayNamesMap(LocalizedString lString)
   {
      Map<Locale, String> map = new HashMap<Locale, String>();
      if (lString != null)
      {
         Map<Locale, Value> values = lString.getValues();
         for (Locale locale: values.keySet())
         {
            map.put(locale, values.get(locale).getString());         
         }
      }
      return map;
   }

   public LocalizedString getDisplayName()
   {
      return new LocalizedString(displayNames, Locale.ENGLISH);
   }

   public void setDisplayName(LocalizedString localizedString)
   {
      if (localizedString == null)
      {
         throw new IllegalArgumentException("No null display name accepted");
      }

      displayNames = new HashMap();
      
      Map map = localizedString.getValues();
      Iterator it = map.values().iterator();
      while (it.hasNext())
      {
         LocalizedString.Value value = (LocalizedString.Value)it.next();
         displayNames.put(value.getLocale(), value.getString());
      }
   }

   // Hibernate ********************************************************************************************************

   public Long getKey()
   {
      return key;
   }

   public void setKey(Long key)
   {
      this.key = key;
   }

   public String getInstanceId()
   {
      return instanceId;
   }

   public void setInstanceId(String instanceId)
   {
      this.instanceId = instanceId;
   }

   public Map getRelatedSecurityBindings()
   {
      return relatedSecurityBindings;
   }

   public void setRelatedSecurityBindings(Map relatedSecurityBindings)
   {
      this.relatedSecurityBindings = relatedSecurityBindings;
   }

   public Map getRelatedCustomizations()
   {
      return relatedCustomizations;
   }

   public void setDisplayNames(Map displayNames)
   {
      this.displayNames = displayNames;
   }

   public Map getDisplayNames()
   {
      return displayNames;
   }

   public void setRelatedCustomizations(Map relatedCustomizations)
   {
      this.relatedCustomizations = relatedCustomizations;
   }

   public boolean isMutable()
   {
      return mutable;
   }

   public void setMutable(boolean mutable)
   {
      this.mutable = mutable;
   }

   // Instance implementation ******************************************************************************************

   public String getId()
   {
      return instanceId;
   }

   // AbstractInstanceDefinition implementation ************************************************************************

   public Collection getCustomizations()
   {
      return relatedCustomizations.values();
   }

   public Set getSecurityBindings()
   {
      Set constraints = new HashSet();
      for (Iterator i = relatedSecurityBindings.values().iterator(); i.hasNext();)
      {
    	  MemoryInstanceSecurityBinding isc = (MemoryInstanceSecurityBinding)i.next();
         RoleSecurityBinding sc = new RoleSecurityBinding(isc.getActions(), isc.getRole());
         constraints.add(sc);
      }
      return constraints;
   }

   // ContextObject implementation *************************************************************************************

   public void setContext(Object context)
   {
      this.containerContext = (InstanceContainerContext)context;
   }

   protected InstanceContainerContext getContainerContext()
   {
      return containerContext;
   }
}
