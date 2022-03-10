package org.osivia.portal.core.instances;

import java.util.Collection;
import java.util.Set;

import org.jboss.portal.core.impl.model.instance.AbstractInstance;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceCustomization;
import org.jboss.portal.core.impl.model.instance.AbstractInstanceDefinition;

import org.jboss.portal.core.impl.model.instance.JBossInstanceContainerContext;
import org.jboss.portal.core.model.instance.DuplicateInstanceException;
import org.jboss.portal.core.model.instance.InstanceContainer;
import org.jboss.portal.core.model.instance.InstanceDefinition;
import org.jboss.portal.core.model.instance.InstancePermission;
import org.jboss.portal.core.model.instance.metadata.InstanceMetaData;
import org.jboss.portal.portlet.PortletContext;

/**
 * This class replaces PersistentInstanceContainerContext.
 * It does nothing else but giving access to InstanceContainer
 */
public class InstanceContainerContextImpl implements JBossInstanceContainerContext {

	private InstanceContainer container;

	@Override
	public Collection<InstanceDefinition> getInstanceDefinitions() {
		return null;
	}

	@Override
	public AbstractInstanceDefinition getInstanceDefinition(String id) {
		return null;
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
	}

	@Override
	public void destroyInstanceDefinition(AbstractInstanceDefinition instanceDef) {
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
		((InstanceContainerImpl) container).updateDefinition((InstanceDefinitionImpl)instance);
	}

	@Override
	public void updateInstance(AbstractInstance instance, PortletContext portletContext) {
		((InstanceContainerImpl) container).updateDefinition((InstanceDefinitionImpl)instance);
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
