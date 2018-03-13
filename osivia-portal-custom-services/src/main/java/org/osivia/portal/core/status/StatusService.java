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
package org.osivia.portal.core.status;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
import org.osivia.portal.api.log.LoggerMessage;
import org.osivia.portal.api.net.ProxyUtils;
import org.osivia.portal.api.status.UnavailableServer;
import org.osivia.portal.core.error.IPortalLogger;

import com.sun.mail.smtp.SMTPTransport;



public class StatusService extends ServiceMBeanSupport implements StatusServiceMBean, Serializable {

	/**
	 * Property setted in environnement_portal.properties
	 */
	private static final String NUXEOPROBE_PROPERTY = "osivia.status.nuxeoprobe";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long intervalleTest = 60L * 1000L;
	
	private static final String NUXEO_RUNNINGSTATUS_URL = "/runningstatus";
	private static final String NUXEO_RUNNINGSTATUS_OK = "Ok";
	
	private static final String NUXEO_PROBESTATUS_URL = "/site/probes/status";
	private static final String NUXEO_PROBESTATUS_OK = "{\"error\":0}";
	

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

		if (url == null )
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

		
		
        if( ! service.getUrl().startsWith("http://"))    {
            // update by external way 
            return service.isServiceUp();
        }
        
        
		if (service.isServiceUp()  && !service.isMustBeChecked())
			return true;
		
		synchronized (service) {
			checkService(service);
			}
		
		
	

		return service.isServiceUp();
	}

	private void checkService(ServiceState service) {

	
		// On assure la périodicité des tests
       if (service.isMustBeChecked() || (!service.isServiceUp() && System.currentTimeMillis() - service.getLastCheckTimestamp() > intervalleTest)) {
            
            service.setMustBeChecked(false);

            service.setLastCheckTimestamp(System.currentTimeMillis());
            
            try {
                
                statutLog.info("Checking " + service.getUrl() );
                
                testerService(service);

                service.setServiceUp(true);

                statutLog.info("Service " + service.getUrl() + " UP");
            }

            catch (UnavailableServer e) {
                
                // v 2.0.21 : la mise en DOWN est explicite (erreur runningstatus)
                
                service.setServiceUp(false);
                
                
                statutLog.info("Service " + service.getUrl() + " DOWN . Reason : " + e.toString());
                
                String url = service.getUrl();
                if( url.endsWith("/nuxeo"))
                     IPortalLogger.logger.fatal(new LoggerMessage("nuxeo "+ e.toString()));

            }
            

        }

	}


    
    public void sendMail(String from, String to, String subject, String content) {

        // Récupération des propriétés systemes (configurés dans le portal.properties).
        Properties props = System.getProperties();

        Session mailSession = Session.getInstance(props, null);

        // Nouveau message
        final MimeMessage msg = new MimeMessage(mailSession);

        // -- Set the FROM and TO fields --
        try {
            
            msg.setFrom(new InternetAddress(from));
            
            String mailDestinataire = to;
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mailDestinataire, false));
            
            msg.setSubject(subject,"UTF-8");

            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            mp.addBodyPart(htmlPart);

            msg.setContent(mp);
            
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) mailSession.getTransport(System.getProperty("portal.mail.transport"));

            t.connect(System.getProperty("portal.mail.host"),
                    System.getProperty("portal.mail.login"),
                    System.getProperty("portal.mail.password"));
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();
        } catch (AddressException e) {
            log.info(e.getMessage());
        } catch (MessagingException e) {
            log.info(e.getMessage());
        }


    }
    
    
	
	public void notifyError(String serviceCode, UnavailableServer e) {
        statutLog.error("Error notification for service " + serviceCode + " : " + e.toString());
        

        ServiceState service = listeServices.get(serviceCode);
        
        if (service != null) {
            String msg =  e.toString();
            
            
            // Le DOWN,UP peut être forcé par l'appelant
            //TOD0 : notion de DOWN forcé à intégrer dans l'API
            
            if( msg != null &&  msg.indexOf("[DOWN]") != -1)    {
                if( System.getProperty("portal.status.mail.to") != null){
                    sendMail(System.getProperty("portal.status.mail.from"), System.getProperty("portal.status.mail.to"), "Gestionnaire de statut toutatice", msg);
                }
                service.setServiceUp(false);
                service.setLastCheckTimestamp( System.currentTimeMillis());
            }
            else if( msg != null &&  msg.indexOf("[UP]") != -1)    {
                service.setServiceUp(true);
                service.setLastCheckTimestamp( System.currentTimeMillis());
                statutLog.info("Service " + service.getUrl() + " UP");                
             }   else
                service.setMustBeChecked(true);
        }
	}

	/**
	 * Marque un service a tester . Un thread est lancé pour s'assurer qu'on ne bloquera pas
	 * la requete
	 * 
	 * @param service
	 */
	public void markServiceToCheck(String serviceCode) {
        statutLog.warn(serviceCode + " marked as to check.");
		
		ServiceState service = listeServices.get(serviceCode);
		
		if (service != null) {
			synchronized (listeServices) {
				service.setMustBeChecked(true);
			}
		}
		
	}
	
	public void testerService(ServiceState service) throws UnavailableServer {

		try {
			
			// 10 secondes de timeout
	
			int timeOut = 10;
			if( service.getLastCheckTimestamp() == 0)	{
				
				// sauf au démarrage ...
				timeOut = 60;
			}
			String url = service.getUrl();
			
			// TODO : externaliser dans l'API status (utl d'appel différente de url service)
			if( url.endsWith("/nuxeo"))
			    url += getNuxeoStatusUrl();
			
            statutLog.info("Testing Nuxeo service URL : " + url);
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(new URLTesteur(url, timeOut));
			
			String response = future.get(timeOut, TimeUnit.SECONDS);

			if( url.endsWith(getNuxeoStatusUrl()))   {

			    if( !StringUtils.containsIgnoreCase(response, getNuxeoStatusMessage()))    {
			        throw new UnavailableServer(response);
			    }
			}
			
			
		} catch (Exception e) {
			if (e.getCause() instanceof UnavailableServer)
				throw (UnavailableServer) e.getCause();
			else	    {
			    {
	                String msg = "Error during check : " + e.getClass().getName();
	                if( e.getMessage() != null)
	                    msg += " " + e.getMessage();
	                if(  e.getCause() != null)
	                    msg += " " + e.getCause();
	                
	                throw new UnavailableServer(msg);
	            }			    

			}
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
						if (executionCount >= 3) {
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
					UnavailableServer e = new UnavailableServer(rc);
					throw e;
				}

			} catch (Exception e) {
				if( ! (e instanceof UnavailableServer))	{
					UnavailableServer exc = new UnavailableServer("url " + url + " " + e.getMessage());
					throw exc;
					}
				else 
					throw e;

			}

			return responseBody;
		}
	}
	
	/**
	 * Return the status url to check (if probe mode is activated or not)
	 * @return
	 */
	private String getNuxeoStatusUrl() {
		
		if(System.getProperty(NUXEOPROBE_PROPERTY) != null && "true".equals(System.getProperty(NUXEOPROBE_PROPERTY))) {
			return NUXEO_PROBESTATUS_URL;
		}
		else {
			return NUXEO_RUNNINGSTATUS_URL;
		}
		
	}

	/**
	 * Return the status message to check (if probe mode is activated or not)
	 * @return
	 */
	private String getNuxeoStatusMessage() {
		
		if(System.getProperty(NUXEOPROBE_PROPERTY) != null && "true".equals(System.getProperty(NUXEOPROBE_PROPERTY))) {
			return NUXEO_PROBESTATUS_OK;
		}
		else {
			return NUXEO_RUNNINGSTATUS_OK;
		}
		
	}

}
