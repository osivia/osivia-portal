package org.osivia.portal.core.identity;

import org.jboss.portal.identity.ldap.LDAPConnectionContext;
import org.jboss.util.StringPropertyReplacer;

public class PropertiesConnectionContext extends LDAPConnectionContext {

	@Override
	public void setHost(String host) {
		
		
		String converted = StringPropertyReplacer.replaceProperties(host);
		super.setHost(converted);
	}

	@Override
	public void setAdminDN(String adminDN) {
		String converted = StringPropertyReplacer.replaceProperties(adminDN);
		super.setAdminDN(converted);
	}

	@Override
	public void setAdminPassword(String adminPassword) {
		String converted = StringPropertyReplacer.replaceProperties(adminPassword);
		super.setAdminPassword(converted);
	}

	@Override
	public void setPort(String port) {
		String converted = StringPropertyReplacer.replaceProperties(port);
		super.setPort(converted);
	}

	@Override
	public void setProtocol(String protocol) {
		String converted = StringPropertyReplacer.replaceProperties(protocol);
		super.setProtocol(converted);
	}
	

}
