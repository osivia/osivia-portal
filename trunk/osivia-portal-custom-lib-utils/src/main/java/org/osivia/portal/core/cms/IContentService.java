package org.osivia.portal.core.cms;

import org.jboss.portal.core.model.portal.Page;

public interface IContentService {
	
	public Page getDisplayPage(String contentId) throws Exception;
	public String getPortletInstanceName( String contentId);

}
