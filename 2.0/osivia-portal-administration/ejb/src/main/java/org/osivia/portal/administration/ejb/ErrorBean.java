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
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.portalobjects.IDynamicObjectContainer;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



@Scope(ScopeType.PAGE)
@Name("errorBean")

public class ErrorBean   {

public ErrorBean() {
		super();
	}

public String msg = "";

public String getMsg() {
	return msg;
}

public void setMsg(String msg) {
	this.msg = msg;
}

public String getDisplayMsg() {
	String curMsg = msg;
	msg = "";
	return curMsg;
}

	
}
