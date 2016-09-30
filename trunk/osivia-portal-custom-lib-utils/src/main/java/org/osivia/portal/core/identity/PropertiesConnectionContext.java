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
