package org.osivia.portal.core.page;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.PersistentPortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;





/**
 * 
 * Ce module surcharge le container par defaut de JBoss Portal 
 * 
 * @author jsteux
 *
 */
public class PortalObjectContainer extends PersistentPortalObjectContainer {

	private Log logger = LogFactory.getLog(PortalObjectContainer.class);
	
	private IDynamicObjectContainer dynamicObjectContainer = null;
	private String dynamicObjectContainerBeanName = null;


	
	public String getDynamicObjectContainerBeanName() {
		return dynamicObjectContainerBeanName;
	}

	public void setDynamicObjectContainerBeanName(String dynamicObjectContainerBeanName) {
		this.dynamicObjectContainerBeanName = dynamicObjectContainerBeanName;
	}

	public IDynamicObjectContainer getDynamicObjectContainer() {
		try	{
			if( dynamicObjectContainer == null)
				dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, getDynamicObjectContainerBeanName());
			} catch( Exception e)	{
				logger.error(e);
			}
			return dynamicObjectContainer;
		
	}

	public PortalObject getNonDynamicObject(PortalObjectId id) throws IllegalArgumentException {
	
		return super.getObject(id);


	}

	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		
		PortalObject object = getDynamicObjectContainer().getObject(this, id);

		return object;


	}

}
