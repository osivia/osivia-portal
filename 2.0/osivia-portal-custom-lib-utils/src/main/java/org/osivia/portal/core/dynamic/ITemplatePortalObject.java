package org.osivia.portal.core.dynamic;

import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.PortalObject;

public interface ITemplatePortalObject {
	public PortalObject getTemplate();
	public Page getEditablePage();
	public boolean isClosable() ;

}
