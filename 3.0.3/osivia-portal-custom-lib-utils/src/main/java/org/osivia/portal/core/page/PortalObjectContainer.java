package org.osivia.portal.core.page;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.PersistentPortalObjectContainer;
import org.jboss.portal.core.model.portal.Context;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
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

	private final Log logger = LogFactory.getLog(PortalObjectContainer.class);

	private IDynamicObjectContainer dynamicObjectContainer = null;
	private String dynamicObjectContainerBeanName = null;



	public String getDynamicObjectContainerBeanName() {
		return this.dynamicObjectContainerBeanName;
	}

	public void setDynamicObjectContainerBeanName(String dynamicObjectContainerBeanName) {
		this.dynamicObjectContainerBeanName = dynamicObjectContainerBeanName;
	}

	public IDynamicObjectContainer getDynamicObjectContainer() {
		try	{
			if( this.dynamicObjectContainer == null) {
                this.dynamicObjectContainer = Locator.findMBean(IDynamicObjectContainer.class, this.getDynamicObjectContainerBeanName());
            }
			} catch( Exception e)	{
				this.logger.error(e);
			}
			return this.dynamicObjectContainer;

	}

	public PortalObject getNonDynamicObject(PortalObjectId id) throws IllegalArgumentException {

		return super.getObject(id);


	}

	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {

		PortalObject object = this.getDynamicObjectContainer().getObject(this, id);

		return object;


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

}
