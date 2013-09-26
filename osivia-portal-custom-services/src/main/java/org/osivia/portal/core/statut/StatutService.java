package org.osivia.portal.core.statut;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.api.net.ProxyUtils;
import org.osivia.portal.api.statut.ServeurIndisponible;


public class StatutService extends ServiceMBeanSupport implements StatutServiceMBean, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long intervalleTest = 60L * 1000L;
	
	private static final String NUXEO_RUNNINGSTATUS_URL = "/runningstatus";
	private static final String NUXEO_RUNNINGSTATUS_OK = "Ok";


	private static Log statutLog = LogFactory.getLog("PORTAL_STATUS");

	Map<String, ServiceState> listeServices;

	public void stopService() throws Exception {
		statutLog.info("Gestionnaire statut arrete");

	}

	public void startService() throws Exception {
		statutLog.info("Gestionnaire statut demarre");
		listeServices = new HashMap<String, ServiceState>();
	}

	public boolean isReady(String url) {

		if (url == null || !url.startsWith("http://"))
			return false;

		ServiceState service = listeServices.get(url);

		if (service == null) {
			synchronized (listeServices) {
				if( service == null)	{
					ServiceState newService = new ServiceState(url);
					listeServices.put(url, newService);
				}
			}
			service = listeServices.get(url);
		}

		if (service.isServiceUp())
			return true;
		
		synchronized (service) {
			checkService(service);
			}
		
		
	

		return service.isServiceUp();
	}

	private void checkService(ServiceState service) {

	
		// On assure la périodicité des tests
		
		if (!service.isServiceUp() && System.currentTimeMillis() - service.getLastCheckTimestamp() > intervalleTest) {

			statutLog.debug("Test du service " + service.getUrl());

			service.setLastCheckTimestamp(System.currentTimeMillis());

			try {
				testerService(service);

				service.setServiceUp(true);

				statutLog.info("Le service " + service.getUrl() + " est UP");
			}

			catch (ServeurIndisponible e) {
				
				service.setServiceUp(false);
				
				statutLog.info("Service " + service.getUrl() + " DOWN . Raison : " + e.toString());

			}

		}

	}

	public void notifyError(String serviceCode, ServeurIndisponible e) {

		statutLog.error("Erreur " + serviceCode + " : " + e.toString());

		ServiceState service = listeServices.get(serviceCode);
		if (service != null) {
			service.setServiceUp(false);
		}
	}

	/**
	 * Teste l'état d'un service . Un thread est lancé pour s'assurer qu'on ne bloquera pas
	 * la requete
	 * 
	 * @param service
	 * @throws ServeurIndisponible
	 */
	
	public void testerService(ServiceState service) throws ServeurIndisponible {

		try {
			
			// 10 secondes de timeout
	
			int timeOut = 10;
			if( service.getLastCheckTimestamp() == 0)	{
				
				// sauf au démarrage ...
				timeOut = 60;
			}
			String url = service.getUrl();
			
			if( url.endsWith("/nuxeo"))
			    url += NUXEO_RUNNINGSTATUS_URL;
			
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(new URLTesteur(url, timeOut));
			
			String response = future.get(timeOut, TimeUnit.SECONDS);
			
			if( url.endsWith(NUXEO_RUNNINGSTATUS_URL))   {
			    if( !StringUtils.containsIgnoreCase(response, NUXEO_RUNNINGSTATUS_OK))    {
			        throw new ServeurIndisponible(response);
			    }
			}
			

		} catch (Exception e) {
			if (e.getCause() instanceof ServeurIndisponible)
				throw (ServeurIndisponible) e.getCause();
			else	
				throw new ServeurIndisponible("Probleme controle url : " + e.getClass().getName() + " "
						+ e.getMessage() + e.getCause());
		}

	}

	private static class URLTesteur implements Callable<String> {

		private String url = null;
		private int timeOut = 0;

		/** Constructor. */
		private URLTesteur(String url, int timeOut) {
			this.url = url;
			this.timeOut = timeOut;
		}

		public String call() throws Exception {
		    
		    String responseBody;
		    
			try {
				HttpClient client = new HttpClient();

				// Set the timeout in milliseconds until a connection is
				// established.
				client.getParams().setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, new Long(timeOut * 1000));
				client.getParams().setParameter(HttpClientParams.HEAD_BODY_CHECK_TIMEOUT, new Long(timeOut * 1000));
				client.getParams().setParameter(HttpClientParams.SO_TIMEOUT, new Integer(timeOut * 1000));

				HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {

					public boolean retryMethod(final HttpMethod method, final IOException exception, int executionCount) {
						if (executionCount >= 1) {
							// Do not retry if over max retry count
							return false;
						}
						if (exception instanceof NoHttpResponseException) {
							// Retry if the server dropped connection on us
							return true;
						}
						if (!method.isRequestSent()) {
							// Retry if the request has not been sent fully or
							// if it's OK to retry methods that have been sent
							return true;
						}
						// otherwise do not retry
						return false;
					}
				};

				client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, myretryhandler);

				ProxyUtils.setProxyConfiguration(url, client);
				
				
				GetMethod get = new GetMethod(url);

				statutLog.debug("testerDisponibilite ");
				int rc = client.executeMethod(get);
				statutLog.debug("rc= " + rc);
				
				responseBody = get.getResponseBodyAsString();

				if (rc != HttpStatus.SC_OK) {
					ServeurIndisponible e = new ServeurIndisponible(rc);
					throw e;
				}

			} catch (Exception e) {
				if( ! (e instanceof ServeurIndisponible))	{
					ServeurIndisponible exc = new ServeurIndisponible(e.getMessage());
					throw exc;
					}
				else 
					throw e;

			}

			return responseBody;
		}
	}

}
