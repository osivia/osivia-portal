package org.osivia.portal.administration.ejb;

import java.util.Locale;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.transaction.UserTransaction;

import org.jboss.portal.common.i18n.LocalizedString;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.core.model.portal.Page;
import org.jboss.portal.core.model.portal.Portal;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.Window;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.osivia.portal.api.cache.services.ICacheService;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.cms.SessionListener;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


@Name("systemBean")
@Scope(ScopeType.EVENT)
public class SystemDatasBean   {

	
	private long nbSessions = -1;

	public long getNbSessions() {
		if( nbSessions == -1)
			nbSessions = SessionListener.activeSessions;
		return nbSessions;
	}

	public void setNbSessions(long nbSessions) {
		this.nbSessions = nbSessions;
	}

	

	public void reload() {
		nbSessions = -1;
	}
	
	public void reloadPortalParameters() throws Exception	{
		
		// As we are in servlet, cache must explicitly initialized
		ICacheService cacheService  = Locator.findMBean(ICacheService.class,
				"pia:service=CacheServices");
		
		cacheService.initPortalParameters();
		
		setParametersMsg("Vocabulaires réinitialisés");
		
		
	}

	
	private String parametersMsg = "";

	public String getParametersMsg() {
		return parametersMsg;
	}

	public void setParametersMsg(String parametersMsg) {
		this.parametersMsg = parametersMsg;
	}
	
	
}
