package org.osivia.portal.core.page.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.impl.model.portal.ContextImpl;
import org.jboss.portal.core.model.portal.DuplicatePortalObjectException;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.security.impl.JBossAuthorizationDomainRegistry;
import org.jboss.portal.security.spi.provider.AuthorizationDomain;
import org.jboss.system.ServiceMBeanSupport;
import org.osivia.portal.api.locator.Locator;
import org.osivia.portal.core.tracker.RequestContextUtil;

/**
 * Create a PortalObjectContainer from a default-object.xml
 * 
 * @author JS Steux
 */

public class ConfigurationImportManager extends ServiceMBeanSupport implements IConfigurationImportManager {

	/** The current active poc. */
	private ImportPortalObjectContainer poc;

	/** The current authorization domain */
	private AuthorizationDomain authDomain;

	/** The authorization domain registry. */
	private JBossAuthorizationDomainRegistry authorizationDomainRegistry;

	/** The default configuration file path. */
	private final String DEFAULT_FILE_PATH = "/opt/portal/jboss-as/server/production/deploy/jboss-portal-ha.sar/conf/data/default-object.xml";

	/** The configuration file path. */
	private String configurationFilePath = null;

	/** The checksum of the current parsed file */
	private String lastChecksum = null;

	/** The watcher thread for modifications. */
	private WatcherThread watcher;

	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);

	public void startService() throws Exception {
		
		log.info("start service ConfigurationImportManager");

		authorizationDomainRegistry = Locator.findMBean(JBossAuthorizationDomainRegistry.class,
				"portal:service=AuthorizationDomainRegistry");

		configurationFilePath = System.getProperty("portal.configuration.path");
		if (configurationFilePath == null)
			configurationFilePath = DEFAULT_FILE_PATH;

		// Initialization
		parseFile();

		// Start a watcher thread
		watcher = new WatcherThread(this);
		Thread thread = new Thread(watcher);
		thread.start();
	}

	public String getFilePath() {
		return configurationFilePath;
	}

	public void parseFile() {


		try {

			File f = new File(getFilePath());
			String newChecksum = DigestUtils.md5Hex(new FileInputStream(new File(getFilePath())));
			if (!StringUtils.equals(lastChecksum, newChecksum)) {
				
				logger.info("Importing configuration file . checksum : " + newChecksum);
				
				lastChecksum = newChecksum;

				ConfigurationFileParser parser = new ConfigurationFileParser(getFilePath());
				ImportPortalObjectContainer newPoc = parser.parse();

				// Remove old domain
				if (poc != null) {
					authorizationDomainRegistry.removeDomain(poc);
				}

				// Register new poc
				poc = newPoc;
				authDomain = poc;

				authorizationDomainRegistry.addDomain(authDomain);
			}

		} catch (Exception e) {
			logger.error("Error during file import " + e.getMessage());
		}
	}

	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		logger.debug("ConfigurationImportManager getObject " + id);
		PortalObject res = poc.getObject(id);
		return res;
	}

	@Override
	public AuthorizationDomain getAuthorizationDomain() {
		return authDomain;
	}

	public void stopService() throws Exception {
		log.info("stop service ConfigurationImportManager");
		if (watcher != null) {
			watcher.endThread();
		}
	}

	@Override
	public ContextImpl createRoot(String namespace) throws DuplicatePortalObjectException {
		return poc.createRoot(namespace);
	}

}
