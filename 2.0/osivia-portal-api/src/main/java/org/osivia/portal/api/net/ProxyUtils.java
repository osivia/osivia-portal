package org.osivia.portal.api.net;

import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/** Classe utilitaire pour la gestion des paramètres du proxy Http. */
public abstract class ProxyUtils {
	
	
	public static void setProxyConfiguration(String url, HttpClient client) throws Exception {
		ProxyUtils.ProxyConfig proxyConf = ProxyUtils.getProxyConfigFromEnvProperties();
	
		// Les flux sortants doivent passer par un proxy		
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
	}
	
	private static boolean validateNonProxyHosts(String targetHost) {
		return ProxyUtils.isNotProxyHost(targetHost);
	}

	
	public static boolean isProxyEnabled(URL targetURL, String proxyHost) {

		boolean state = false;
		if (proxyHost != null) {
			state = true;
		}
		boolean isNonProxyHost = validateNonProxyHosts(targetURL.getHost());

		return state && !isNonProxyHost;
	}
	/**
	 * Vérifie si le serveur spécifié est dans la liste des adresses exclues du proxy http.
	 * @param targetHost 
	 * 			le nom du serveur
	 * @return vrai si le host ne doit pas être contacté via le proxy http
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
	
	public static class ProxyConfig {
		private String host;
		private int port=-1;
		
		public ProxyConfig(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}
		
		public ProxyConfig(String host, String port) {
			this(host, getPortAsInt(port));
		}
		
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

		
		public String getHost() {
			return host;
		}
		
		public int getPort() {
			return port;
		}		
	}
	
	public static class ProxyCredentials {
		private String userName;
		private String domain; //proxy domain for NTLM authentication (Windows OS) 
		private String userPassword;
		
		public ProxyCredentials(String userName, String userPassword, String domain) {
			super();
			this.userName = userName;
			this.domain = domain;
			this.userPassword = userPassword;
		}
		
		public String getUserName() {
			return userName;
		}

		public String getDomain() {
			return domain;
		}
		
		public String getUserPassword() {
			return userPassword;
		}
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;		
	}

}
