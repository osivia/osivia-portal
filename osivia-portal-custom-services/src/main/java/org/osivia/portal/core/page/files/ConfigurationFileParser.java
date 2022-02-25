package org.osivia.portal.core.page.files;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.common.i18n.LocalizedString.Value;
import org.jboss.portal.common.io.IOTools;
import org.jboss.portal.common.xml.XMLTools;
import org.jboss.portal.core.controller.coordination.CoordinationConfigurator;
import org.jboss.portal.core.model.content.ContentType;
import org.jboss.portal.core.model.content.spi.ContentProvider;
import org.jboss.portal.core.model.content.spi.ContentProviderRegistry;
import org.jboss.portal.core.model.content.spi.handler.ContentHandler;
import org.jboss.portal.core.model.portal.PortalObject;
import org.jboss.portal.core.model.portal.PortalObjectContainer;
import org.jboss.portal.core.model.portal.PortalObjectId;
import org.jboss.portal.core.model.portal.PortalObjectPath;
import org.jboss.portal.core.model.portal.metadata.BuildContext;
import org.jboss.portal.core.model.portal.metadata.ContextMetaData;
import org.jboss.portal.core.model.portal.metadata.PageMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalMetaData;
import org.jboss.portal.core.model.portal.metadata.PortalObjectMetaData;
import org.jboss.portal.core.model.portal.metadata.WindowMetaData;
import org.jboss.portal.security.RoleSecurityBinding;
import org.jboss.portal.security.SecurityConstants;
import org.jboss.portal.server.deployment.PortalWebApp;
import org.osivia.portal.api.directory.v2.DirServiceFactory;
import org.osivia.portal.api.directory.v2.service.GroupService;
import org.osivia.portal.api.locator.Locator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public class ConfigurationFileParser {

	private String path;

	protected static final Log log = LogFactory.getLog(ConfigurationFileParser.class);

	/** . */
	public static final int OVERWRITE_IF_EXISTS = 0;

	public ConfigurationFileParser(String path) {
		super();
		this.path = path;
	};

	public ImportPortalObjectContainer parse() throws Exception {

		ImportPortalObjectContainer portalObjectContainer = new ImportPortalObjectContainer();

		DocumentBuilder builder = XMLTools.getDocumentBuilderFactory().newDocumentBuilder();
		EntityResolver entityResolver = Locator.findMBean(EntityResolver.class, "portal:service=EntityResolver");
		builder.setEntityResolver(entityResolver);

		ContentProviderRegistry contentProvicerRegistry = Locator.findMBean(ContentProviderRegistry.class,
				"portal:service=ContentProviderRegistry");

		CoordinationConfigurator coordinationConfigurator = Locator.findMBean(CoordinationConfigurator.class,
				"portal:service=CoordinationService");

		InputStream in = IOTools.safeBufferedWrapper(new FileInputStream(path));

		Document doc = builder.parse(in);
		Element deploymentsElt = doc.getDocumentElement();
		List<Element> deploymentElts = XMLTools.getChildren(deploymentsElt, "deployment");
		ArrayList<Unit> units = new ArrayList<Unit>(deploymentElts.size());

		for (Element deploymentElt : deploymentElts) {
			Unit unit = new Unit();

			//
			Element parentRefElt = XMLTools.getUniqueChild(deploymentElt, "parent-ref", false);
			unit.parentRef = parentRefElt == null ? null
					: PortalObjectId.parse(XMLTools.asString(parentRefElt), PortalObjectPath.LEGACY_FORMAT);

			//
			Element ifExistsElt = XMLTools.getUniqueChild(deploymentElt, "if-exists", false);
			unit.ifExists = OVERWRITE_IF_EXISTS;

			// The object to create
			PortalObjectMetaData metaData = null;

			//
			Element metaDataElt = XMLTools.getUniqueChild(deploymentElt, "portal", false);

			if (metaDataElt == null) {
				metaDataElt = XMLTools.getUniqueChild(deploymentElt, "page", false);
				if (metaDataElt == null) {
					metaDataElt = XMLTools.getUniqueChild(deploymentElt, "window", false);
					if (metaDataElt == null) {
						metaDataElt = XMLTools.getUniqueChild(deploymentElt, "context", false);
					}
				}
			}
			if (metaDataElt != null) {
				metaData = PortalObjectMetaData.buildMetaData(contentProvicerRegistry, metaDataElt);
			} else {
				log.debug("Instances element in -object.xml is not supported anymore");
			}

			//
			if (metaData != null) {
				unit.metaData = metaData;
				units.add(unit);
			}
		}

		BuildContext portalObjectBuildContext = new BuildContext() {
			public PortalObjectContainer getContainer() {
				return portalObjectContainer;
			}

			public ContentHandler getContentHandler(ContentType contentType) {
				ContentProvider contentProvider = contentProvicerRegistry.getContentProvider(contentType);
				return contentProvider != null ? contentProvider.getHandler() : null;
			}

			public PortalWebApp getPortalWebApp() {
				return null;
			}

			public CoordinationConfigurator getCoordinationConfigurator() {
				return coordinationConfigurator;
			}
		};

		// Create all objects
		for (Unit unit : units) {

			if (unit.metaData instanceof PortalObjectMetaData) {

				PortalObjectMetaData portalObjectMD = (PortalObjectMetaData) unit.metaData;

				if (unit.parentRef != null) {
					log.debug("Checking existence of parent portal object '" + unit.parentRef + "'");
					Object o = portalObjectContainer.getObject(unit.parentRef);
					if (o instanceof PortalObject) {
						PortalObject parent = (PortalObject) o;

						log.debug("Building portal object");
						PortalObject po = portalObjectMD.create(portalObjectBuildContext, parent);
						unit.ref = po.getId();

					} else if (o == null) {
						log.warn("Cannot create portal object " + unit.metaData + " because the parent '"
								+ unit.parentRef + "' that the deployment descriptor references does not exist");
					}
				} else {
					if (portalObjectContainer.getContext(portalObjectMD.getName()) == null) {
						log.debug("Building portal object");
						PortalObject po = portalObjectMD.create(portalObjectBuildContext, null);
						unit.ref = po.getId();
					}
				}
			}
		}

		return portalObjectContainer;
	}

	/** A unit of deployment in the deployment descriptor. */
	protected static class Unit {
		/** The strategy to use when the root object already exists. */
		protected int ifExists;

		/** The parent ref. */
		protected PortalObjectId parentRef;

		/** Meta data of the deployed portal object. */
		protected Object metaData;

		/** The handle of the deployed object if not null. */
		protected PortalObjectId ref;

		public String toString() {
			StringBuffer buffer = new StringBuffer("Unit[::ifExists=" + ifExists);
			buffer.append(":parentRef=").append(parentRef);
			buffer.append(":Metadata=").append(metaData).append(":ref=").append(ref).append("]");
			return buffer.toString();
		}
	}

}
