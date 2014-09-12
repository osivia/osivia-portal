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
package org.osivia.portal.api.net;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osivia.portal.api.PortalException;



// TODO: Auto-generated Javadoc
/**
 * The Class ProxyUtils.
 * 
 * Utility class for http proxy configuration
 */
public abstract class ProxyUtils {
	
	
	/**
	 * Sets the proxy configuration.
	 *
	 * @param url the url
	 * @param client the client
	 * @throws PortalException the portal exception
	 */
	public static void setProxyConfiguration(String url, HttpClient client) throws PortalException {
		ProxyUtils.ProxyConfig proxyConf = ProxyUtils.getProxyConfigFromEnvProperties();
	
		// for outgoing streams		
		try {
            if (isProxyEnabled(new URL(url), proxyConf.getHost())) {
            	
            	HostConfiguration configuration = new HostConfiguration();
            	configuration.setProxy(proxyConf.getHost(), proxyConf.getPort());			
            	client.setHostConfiguration(configuration);
            	
            	ProxyUtils.ProxyCredentials identity = ProxyUtils.getProxyUserFromEnvProperties();
            	String proxyUser = identity.getUserName();
            	String domain = identity.getDomain();
            	String proxyPassword = identity.getUserPassword(); 			
            	
            	// Authentification proxy
            	if( proxyUser != null) {
            		UsernamePasswordCredentials credentials;
            		if( domain != null )
            			credentials = new NTCredentials(proxyUser, proxyPassword, "", "");
            		else
            			credentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
            		client.getState().setProxyCredentials(AuthScope.ANY, credentials);					
            	}
            	
            }
        } catch (MalformedURLException e) {
            throw new PortalException(e);
        }
	}
	
	/**
	 * Validate non proxy hosts.
	 *
	 * @param targetHost the target host
	 * @return true, if successful
	 */
	private static boolean validateNonProxyHosts(String targetHost) {
		return ProxyUtils.isNotProxyHost(targetHost);
	}

	
	/**
	 * Checks if  proxy is enabled.
	 *
	 * @param targetURL the target url
	 * @param proxyHost the proxy host
	 * @return true, if is proxy enabled
	 */
	public static boolean isProxyEnabled(URL targetURL, String proxyHost) {

		boolean state = false;
		if (proxyHost != null) {
			state = true;
		}
		boolean isNonProxyHost = validateNonProxyHosts(targetURL.getHost());

		return state && !isNonProxyHost;
	}

	/**
	 * Checks if is not proxy host.
	 *
	 * @param targetHost the target host
	 * @return true, if is not proxy host
	 */
	public static boolean isNotProxyHost(String targetHost) {

		// From system property http.nonProxyHosts
		String nonProxyHosts = System.getProperty("http.nonProxyHosts");
		if (nonProxyHosts == null) {
			return false;
		}

		String[] nonProxyHostsArray = nonProxyHosts.split("\\|");

		if (nonProxyHostsArray.length == 1) {
			return targetHost.matches(nonProxyHosts);
		} else {
			boolean pass = false;
			for (int i = 0; i < nonProxyHostsArray.length; i++) {
				String a = nonProxyHostsArray[i];
				if (targetHost.matches(a)) {
					pass = true;
					break;
				}
			}
			return pass;
		}
	}
	
	/** 
	 * Retourne les paramètres du proxy http, lus depuis des propriétés système.
	 * Pour plus d'infos, voir : http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html
	 * @return ProxyConfig
	 */
	public static ProxyConfig getProxyConfigFromEnvProperties() {
		return new ProxyConfig(System.getProperty("http.proxyHost"), System.getProperty("http.proxyPort"));		
	}
	
	/**
	 * Retourne les paramètres d'authentification au proxy http, lus depuis des propriétés système.
	 * Pour plus d'infos, voir : http://java.sun.com/j2se/1.5.0/docs/guide/net/properties.html
	 * @return ProxyCredentials
	 */
	
	public static ProxyCredentials getProxyUserFromEnvProperties() {
		String domain = System.getProperty("http.auth.ntlm.domain");
		String userName = System.getProperty("http.proxyUser");
		String userPassword = System.getProperty("http.proxyPassword");
		if( userName != null ) {
			int separatorIndex = userName.indexOf('\\');
			if( separatorIndex != -1 ) {  
				//cas où l'utilisateur est préfixé par un nom de domaine
				//ex: "wyniwyg.com\proxyUser"				
				String completeName = userName;
				userName = completeName.substring(separatorIndex + 1);
				if( isEmpty(domain) ) {
					domain = completeName.substring(0, separatorIndex);
				}
			}
		}
		return new ProxyCredentials(userName, userPassword, domain);		
	}
	
	/**
	 * The Class ProxyConfig.
	 */
	public static class ProxyConfig {
		
		/** The host. */
		private String host;
		
		/** The port. */
		private int port=-1;
		
		/**
		 * Instantiates a new proxy config.
		 *
		 * @param host the host
		 * @param port the port
		 */
		public ProxyConfig(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}
		
		/**
		 * Instantiates a new proxy config.
		 *
		 * @param host the host
		 * @param port the port
		 */
		public ProxyConfig(String host, String port) {
			this(host, getPortAsInt(port));
		}
		
		/**
		 * Gets the port as int.
		 *
		 * @param sPort the s port
		 * @return the port as int
		 */
		public static int getPortAsInt( String sPort) {
			int port = -1;
			try	 {
			if( sPort != null)
				port =  Integer.parseInt(sPort);
			} catch(Exception e)	{
				// DO NOTHING : return -1
			}
			return port;
		}

		
		/**
		 * Gets the host.
		 *
		 * @return the host
		 */
		public String getHost() {
			return host;
		}
		
		/**
		 * Gets the port.
		 *
		 * @return the port
		 */
		public int getPort() {
			return port;
		}		
	}
	
	/**
	 * The Class ProxyCredentials.
	 */
	public static class ProxyCredentials {
		
		/** The user name. */
		private String userName;
		
		/** The domain. */
		private String domain; //proxy domain for NTLM authentication (Windows OS) 
		
		/** The user password. */
		private String userPassword;
		
		/**
		 * Instantiates a new proxy credentials.
		 *
		 * @param userName the user name
		 * @param userPassword the user password
		 * @param domain the domain
		 */
		public ProxyCredentials(String userName, String userPassword, String domain) {
			super();
			this.userName = userName;
			this.domain = domain;
			this.userPassword = userPassword;
		}
		
		/**
		 * Gets the user name.
		 *
		 * @return the user name
		 */
		public String getUserName() {
			return userName;
		}

		/**
		 * Gets the domain.
		 *
		 * @return the domain
		 */
		public String getDomain() {
			return domain;
		}
		
		/**
		 * Gets the user password.
		 *
		 * @return the user password
		 */
		public String getUserPassword() {
			return userPassword;
		}
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @param str the str
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;		
	}

}
