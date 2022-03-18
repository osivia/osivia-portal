package org.osivia.portal.core.instances;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.portal.core.impl.model.instance.AbstractInstance;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceCustomization;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceDefinition;

import org.jboss.portal.core.impl.model.instance.JBossInstanceContainerContext;
import org.jboss.portal.core.model.instance.DuplicateInstanceException;
import org.jboss.portal.core.model.instance.Instance;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.InstancePermission;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.portlet.PortletContext;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManager;

/**
 * This class replaces PersistentInstanceContainerContext.
 * It stores the instances in memory
 */
public class InstanceContainerContextImpl implements JBossInstanceContainerContext {



	private InstanceContainer container;
	
	private Map<String, InstanceDefinitionImpl> instances;

	public InstanceContainerContextImpl() {
		super();
		instances = Collections.synchronizedMap(new HashMap<String, InstanceDefinitionImpl>());
	}
	
	@Override
	public Collection<InstanceDefinition> getInstanceDefinitions() {
		Collection<InstanceDefinitionImpl> impls = instances.values();

		Collection<InstanceDefinition> list = new ArrayList<InstanceDefinition>();
		for (Iterator i = impls.iterator(); i.hasNext();) {
			list.add((InstanceDefinition) i.next());
		}


		//
		return list;
	}

	@Override
	public AbstractInstanceDefinition getInstanceDefinition(String id) {
		return instances.get(id);
	}

	@Override
	public AbstractInstanceDefinition newInstanceDefinition(String id, String portletRef) {
		return null;
	}

	@Override
	public AbstractInstanceDefinition newInstanceDefinition(InstanceMetaData instanceMetaData) {
		return null;
	}

	@Override
	public void createInstanceDefinition(AbstractInstanceDefinition instanceDef) throws DuplicateInstanceException {
		instances.put(instanceDef.getId(), (InstanceDefinitionImpl) instanceDef);
	}

	@Override
	public void destroyInstanceDefinition(AbstractInstanceDefinition instanceDef) {
		instances.remove(instanceDef.getId());
	}

	@Override
	public void destroyInstanceCustomization(AbstractInstanceCustomization customization) {
	}

	@Override
	public AbstractInstanceCustomization getCustomization(AbstractInstanceDefinition instanceDef,
			String customizationId) {
		return null;
	}

	@Override
	public AbstractInstanceCustomization newInstanceCustomization(AbstractInstanceDefinition def, String id,
			PortletContext portletContext) {
		return null;
	}

	@Override
	public void createInstanceCustomizaton(AbstractInstanceCustomization customization) {
	}

	@Override
	public void updateInstance(AbstractInstance instance, PortletContext portletContext, boolean mutable) {
		
		instance.setPortletRef(portletContext.getId());
		instance.setState(portletContext.getState());
		

		instances.put(instance.getId(), (InstanceDefinitionImpl) instance);
	}

	@Override
	public void updateInstance(AbstractInstance instance, PortletContext portletContext) {

		instance.setPortletRef(portletContext.getId());
		instance.setState(portletContext.getState());
		
		instances.put(instance.getId(), (InstanceDefinitionImpl) instance);
	}

	@Override
	public void updateInstanceDefinition(AbstractInstanceDefinition def, Set securityBindings) {
		((InstanceContainerImpl) container).setSecurityBindings(def.getId(), securityBindings);
	}

	@Override
	public boolean checkPermission(InstancePermission perm) {
		return false;
	}

	public InstanceContainer getContainer() {
		return container;
	}

	@Override
	public void setContainer(InstanceContainer container) {
		this.container = (InstanceContainerImpl) container;

	}

}
