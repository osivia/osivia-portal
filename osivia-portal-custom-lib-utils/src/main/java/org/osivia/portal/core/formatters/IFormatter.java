package org.osivia.portal.core.formatters;

import org.jboss.portal.core.model.portal.PortalObject;

public interface IFormatter {
	public String formatScopeList(PortalObject po, String scopeName, String selectedScope) throws Exception ;
	public String formatContextualization(PortalObject po, String selectedScope) throws Exception ;
	public String formatPortletFilterScopeList(String name, String selectedScope) throws Exception ;

	
}
