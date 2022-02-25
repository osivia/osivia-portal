package org.osivia.portal.core.page.files;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private final String FILE_PATH = "/opt/portal/jboss-as/server/production/deploy/jboss-portal-ha.sar/conf/data/default-object.xml";

	/** The last modified. of the current parsed file */
	private long lastModified = 0L;

	/** The watcher thread for modifications. */
	private WatcherThread watcher;

	private final Log logger = LogFactory.getLog(FilesPortalObjectContainer.class);

	public ConfigurationImportManager() {
		super();

		authorizationDomainRegistry = Locator.findMBean(JBossAuthorizationDomainRegistry.class,
				"portal:service=AuthorizationDomainRegistry");

		parseFile();

		// Start a watcher thread
		watcher = new WatcherThread(this);
		Thread thread = new Thread(watcher);
		thread.start();		
	}

	public String getFilePath() {
		return FILE_PATH;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void parseFile() {

		logger.info("Importing configuration file");

		try {

			File f = new File(getFilePath());

			lastModified = f.lastModified();

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

		} catch (Exception e) {
			logger.error("Error during file import " + e.getMessage());
		}
	}

	@Override
	public PortalObject getObject(PortalObjectId id) throws IllegalArgumentException {
		logger.info("ConfigurationImportManager getObject " + id);
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

}
