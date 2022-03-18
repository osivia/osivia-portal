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
package org.osivia.portal.core.page.files;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.impl.model.portal.ObjectNode;
import org.jboss.portal.core.impl.model.portal.PersistentPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.TransientPortalObjectContainer;
import org.jboss.portal.core.impl.model.portal.AbstractPortalObjectContainer.ContainerContext;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.jboss.portal.security.spi.auth.PortalAuthorizationManagerFactory;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.page.IPortalObjectContainer;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;

/**
 *
 * Ce module surcharge le container par defaut de JBoss Portal
 *
 * @author jsteux
 *
 */
public class FilesPortalObjectContainer extends AbstractPortalObjectContainer implements IPortalObjectContainer {

	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);

	private IDynamicObjectContainer dynamicObjectContainer = null;
	private String dynamicObjectContainerBeanName = null;

	private PortalAuthorizationManagerFactory portalAuthorizationManagerFactory;

	private JBossAuthorizationDomainRegistry authorizationDomainRegistry;
	
	private IConfigurationImportManager configurationImportManager;


	public FilesPortalObjectContainer() {
		
		super();
		
		// At any time, this property can be read to adapt local rules
		System.setProperty("osivia.portal.storage", "memory");
	}

	public IConfigurationImportManager getConfigurationImportManager() {
		return configurationImportManager;
	}

	public void setConfigurationImportManager(IConfigurationImportManager configurationImportManager) {
		this.configurationImportManager = configurationImportManager;
	}

	public JBossAuthorizationDomainRegistry getAuthorizationDomainRegistry() {
		return authorizationDomainRegistry;
	}

	public void setAuthorizationDomainRegistry(JBossAuthorizationDomainRegistry authDomainRegistry) {
		this.authorizationDomainRegistry = authDomainRegistry;
	}

	public PortalAuthorizationManagerFactory getPortalAuthorizationManagerFactory() {
		return portalAuthorizationManagerFactory;
	}

	public void setPortalAuthorizationManagerFactory(PortalAuthorizationManagerFactory pamf) {
		this.portalAuthorizationManagerFactory = pamf;
	}

	public String getDynamicObjectContainerBeanName() {
		return this.dynamicObjectContainerBeanName;
	}

	public void setDynamicObjectContainerBeanName(String dynamicObjectContainerBeanName) {
		this.dynamicObjectContainerBeanName = dynamicObjectContainerBeanName;
	}

	public IDynamicObjectContainer getDynamicObjectContainer() {
		try {
			if (this.dynamicObjectContainer == null) {
				this.dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class,
						this.getDynamicObjectContainerBeanName());
			}
		} catch (Exception e) {
			this.logger.error(e);
		}
		return this.dynamicObjectContainer;

	}

	public PortalObject getNonDynamicObject(PortalObjectId id) throws IllegalArgumentException {
		logger.debug("getNonDynamicObject " + id);
		
		return getConfigurationImportManager().getObject(id);

	}

	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {

		PortalObject object = this.getDynamicObjectContainer().getObject(this, id);
		return object;

	}

	

	@Override
	public void flushNaturalIdCache() {
	
	}

	@Override
	protected ContextImpl createRoot(String namespace) throws DuplicatePortalObjectException {
		return getConfigurationImportManager().createRoot(namespace);
	}

	@Override
	protected ObjectNode getObjectNode(PortalObjectId id) {
		return null;
	}
	
	   /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext(String namespace) {
        String newNamespace = namespace;
        if (newNamespace == null) {
            newNamespace = StringUtils.EMPTY;
        }

        PortalObject object = this.getDynamicObjectContainer().getObject(this, new PortalObjectId(newNamespace, PortalObjectPath.ROOT_PATH));

        return (Context) object;
    }


	@Override
	public AuthorizationDomain getAuthorizationDomain() {
		return getConfigurationImportManager().getAuthorizationDomain();
	}
    
}
